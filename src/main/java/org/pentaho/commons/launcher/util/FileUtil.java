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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.commons.launcher.util;

import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pentaho.commons.launcher.filter.JarFilter;

public class FileUtil {
  public static final String JAR_SUFFIX = ".jar";
  public static final String ZIP_SUFFIX = ".zip";

  public static final String CANT_LOCATE_DIR_WARNING =
      "Warning: Cannot locate the program directory. Assuming default.";
  public static final String UNRECOGNIZED_FILE_TYPE_WARNING = "Warning: Unrecognized location type. Assuming default.";

  public static File computeApplicationDir( final URL location, final File defaultDir, PrintStream outputPrintStream ) {
    if ( location == null ) {
      outputPrintStream.println( CANT_LOCATE_DIR_WARNING );
      return defaultDir;
    }

    if ( !"file".equalsIgnoreCase( location.getProtocol() ) ) {
      outputPrintStream.println( UNRECOGNIZED_FILE_TYPE_WARNING );
      return new File( "." );
    }

    try {
      return new File( location.toURI() ).getParentFile();
    } catch ( URISyntaxException e ) {
      return new File( location.getFile() );
    }
  }

  public static List<URL> populateLibraries( List<String> libraryPaths, File directory, PrintStream outputPrintStream ) {
    JarFilter jarFilter = new JarFilter();
    List<URL> result = new ArrayList<URL>();

    for ( String path : libraryPaths ) {
      final File realPath = new File( directory, path );
      final File[] files = realPath.listFiles( jarFilter );
      if ( files != null ) {
        result.addAll( fileListToURLList( Arrays.asList( files ), outputPrintStream ) );
      }
    }

    return result;
  }

  public static List<URL> populateClasspath( List<String> classpathPaths, File appDir, PrintStream outputPrintStream ) {
    final List<File> files = new ArrayList<File>( classpathPaths.size() );

    for ( String path : classpathPaths ) {
      files.add( new File( appDir, path ) );
    }

    return fileListToURLList( files, outputPrintStream );
  }

  public static List<URL> fileListToURLList( List<File> files, PrintStream outputPrintStream ) {
    List<URL> jars = new ArrayList<URL>( files.size() );
    for ( File file : files ) {
      if ( file.exists() && file.canRead() ) {
        try {
          jars.add( file.toURI().toURL() );
        } catch ( Exception e ) {
          outputPrintStream.println( "Invalid entry, ignoring '" + file.getAbsolutePath() + "':" + e.getMessage() );
        }
      } else {
        outputPrintStream.println( "Invalid entry, ignoring '" + file.getAbsolutePath() + "'" );
      }
    }
    return jars;
  }
}
