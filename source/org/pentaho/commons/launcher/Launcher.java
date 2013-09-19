/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.commons.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.lang.reflect.Method;

/**
 * Laucher for Java classes. Allows to modify the classpath at runtime instead
 * of relying on the jar-mechanism.
 *
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 * @noinspection AssignmentToForLoopParameter
 */
public class Launcher {
  private static final String JAR_SUFFIX = ".jar";
  private static final String ZIP_SUFFIX = ".zip";

  private static class JarFilter implements FileFilter {

    private JarFilter() {
    }

    public boolean accept(final File pathname) {
      final String base = pathname.getName();
      return (endsWithIgnoreCase(base, JAR_SUFFIX) || endsWithIgnoreCase(base, ZIP_SUFFIX));
    }
  }

  private static class LookupParser extends PropertyLookupParser
  {
    private File applicationDirectory;

    private LookupParser(final File applicationDirectory)
    {
      this.applicationDirectory = applicationDirectory;
    }

    /**
     * Looks up the property with the given name.
     *
     * @param property the name of the property to look up.
     * @return the translated value.
     */
    protected String lookupVariable(final String property)
    {
      if (property.equals("SYS:APP_DIR"))
      {
        return applicationDirectory.getAbsolutePath();
      }
      return System.getenv(property);
    }
  }

  private static boolean debug = false;
  private static JarFilter jarFilter;
  private static String mainClass;
  private static ArrayList libraries;
  private static ArrayList classpath;

  private Launcher() {
  }

  public static void main(final String[] args) throws Exception {
    jarFilter = new JarFilter();
    libraries = new ArrayList();
    classpath = new ArrayList();

    final int parsedArgs = parseParameters(args);
    final URL location = Launcher.class.getProtectionDomain().getCodeSource()
        .getLocation();

    // Location is either a Jar/ZIP file or a directory.
    final File appDir = computeApplicationDir(location, new File("."));

    final File configurationFile = new File(appDir, "launcher.properties");

    parseConfiguration(configurationFile, appDir);

    final ArrayList jars = new ArrayList();
    for (int i = 0; i < classpath.size(); i++) {
      final String path = (String) classpath.get(i);
      final File file = new File(appDir, path);
      if (file.exists() && file.canRead()) {
        try {
          jars.add(file.toURI().toURL());
        } catch (Exception e) {
          System.err
              .println("Invalid classpath entry, ignoring '" + path + "'");
        }
      } else {
        System.err.println("Invalid classpath entry, ignoring '" + path + "'");
      }
    }

    for (int i = 0; i < libraries.size(); i++) {
      final String library = (String) libraries.get(i);
      addLibrary(appDir, library, jars);
    }
    final URL[] classpathEntries = (URL[]) jars.toArray(new URL[jars.size()]);
    final ClassLoader cl = new URLClassLoader(classpathEntries);
    Thread.currentThread().setContextClassLoader(cl);

    if (Launcher.mainClass == null) {
      System.err.println("Invalid main-class entry, cannot proceed.");
      System.err.println("Application Directory: " + appDir);
      System.exit(1);
    }

    if (debug) {
      System.out.println("Application Directory: " + appDir);
      for (int i = 0; i < classpathEntries.length; i++) {
        final URL url = classpathEntries[i];
        System.out.println("ClassPath[" + i + "] = " + url);
      }
    }

    final Class mainClass = cl.loadClass(Launcher.mainClass);
    // Invoke main(..)
    final String[] newArgs = new String[args.length - parsedArgs];
    System.arraycopy(args, parsedArgs, newArgs, 0, newArgs.length);
    final Method method = mainClass.getMethod("main",
        new Class[] { String[].class });
    method.invoke(null, new Object[] { newArgs });
  }

  private static void addLibrary(final File directory, final String path,
      final ArrayList list) {
    final File realPath = new File(directory, path);
    final File[] files = realPath.listFiles(jarFilter);
    if (files == null) {
      return;
    }
    for (int i = 0; i < files.length; i++) {
      final File file = files[i];
      if (file.exists() && file.canRead()) {
        try {
          list.add(file.toURI().toURL());
        } catch (Exception e) {
          System.err
              .println("Invalid classpath entry, ignoring '" + path + "'");
        }
      } else {
        System.err.println("Invalid classpath entry, ignoring '" + path + "'");
      }
    }
  }

  private static void parseConfiguration(final File configuration,
                                         final File applicationDirectory) {
    try {
      final Properties p = new Properties();
      final InputStream in = new BufferedInputStream(new FileInputStream(
          configuration));
      try {
        p.load(in);
      } finally {
        in.close();
      }

      if (mainClass == null) {
        mainClass = p.getProperty("main");
      }
      debug = "true".equals(p.getProperty("debug"));
      parsePath(p.getProperty("libraries"), libraries, ":");
      parsePath(p.getProperty("classpath"), classpath, ":");

      final LookupParser parser = new LookupParser(applicationDirectory);
      final Enumeration keys = p.keys();
      while (keys.hasMoreElements())
      {
        final String key = (String) keys.nextElement();
        if (key.startsWith("system-property.") == false)
        {
          continue;
        }
        final String propertyName = key.substring("system-property.".length());
        final String propertyValue = p.getProperty(key);
        final String translatedValue = parser.translateAndLookup(propertyValue);
        if (translatedValue != null && "".equals(translatedValue) == false)
        {
          System.setProperty(propertyName, translatedValue);
        }
      }

      if ("true".equals(p.getProperty("uninstall-security-manager", "false")))
      {
        System.setSecurityManager(null);
      }
    } catch (Exception e) {
      // Ignore any error here ..
    }
  }

  private static int parseParameters(final String[] args) {
    for (int i = 0; i < args.length; i++) {
      final String arg = args[i];
      if ("-main".equals(arg)) {
        i += 1;
        if (i == args.length) {
          System.err
              .println("Argument parse error: '-main' needs an parameter");
          System.exit(1);
        }
        mainClass = args[i];
      } else if ("-lib".equals(arg)) {
        i += 1;
        if (i == args.length) {
          System.err.println("Argument parse error: '-lib' needs an parameter");
          System.exit(1);
        }
        parsePath(args[i], libraries, File.pathSeparator);
      } else if ("-cp".equals(arg) || "-classpath".equals(arg)) {
        i += 1;
        if (i == args.length) {
          System.err.println("Argument parse error: '-cp' needs an parameter");
          System.exit(1);
        }
        parsePath(args[i], classpath, File.pathSeparator);
      } else if ("--".equals(arg)) {
        return i + 1;
      } else {
        return i;
      }
    }
    return args.length;
  }

  private static void parsePath(final String path, final ArrayList list,
      final String separator) {
    if (path == null) {
      return;
    }

    final StringTokenizer strtok = new StringTokenizer(path, separator);
    while (strtok.hasMoreTokens()) {
      list.add(strtok.nextToken());
    }
  }

  private static File computeApplicationDir(final URL location, final File defaultDir) {
    if (location == null) {
      System.err
          .println("Warning: Cannot locate the program directory. Assuming default.");
      return defaultDir;
    }

    if (!"file".equalsIgnoreCase(location.getProtocol())) {
      System.err
          .println("Warning: Unrecognized location type. Assuming default.");
      return new File(".");
    }

    final String file = location.toExternalForm();
    if (endsWithIgnoreCase(file, JAR_SUFFIX) || endsWithIgnoreCase(file, ZIP_SUFFIX)) {
      try {
        return new File(location.toURI()).getParentFile();
      } catch (URISyntaxException e) {
        return new File(location.getFile());
      }
    } else {
      try {
        return new File(location.toURI()).getParentFile();
      } catch (URISyntaxException e) {
        return new File(location.getFile());
      }
    }
  }

  public static boolean endsWithIgnoreCase(final String base, final String end)
  {
    if (base.length() < end.length())
    {
      return false;
    }
    return base.regionMatches(true, base.length() - end.length(), end, 0, end.length());
  }
}
