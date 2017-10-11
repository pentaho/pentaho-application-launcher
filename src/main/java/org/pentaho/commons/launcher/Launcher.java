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

package org.pentaho.commons.launcher;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.pentaho.commons.launcher.config.Configuration;
import org.pentaho.commons.launcher.config.Parameters;
import org.pentaho.commons.launcher.util.FileUtil;
import org.pentaho.commons.launcher.util.StringUtil;

/**
 * Launcher for Java classes. Allows to modify the classpath at runtime instead of relying on the jar-mechanism.
 * 
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 * @noinspection AssignmentToForLoopParameter
 */
public class Launcher {
  public static void main( final String[] args ) throws Exception {
    Parameters parameters = Parameters.fromArgs( args, System.err );
    final URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();

    // Location is either a Jar/ZIP file or a directory.
    final File appDir = FileUtil.computeApplicationDir( location, new File( "." ), System.err );

    final File configurationFile = new File( appDir, "launcher.properties" );

    Properties configProperties = new Properties();
    try {
      configProperties.load( new FileReader( configurationFile ) );
    } catch ( Exception e ) {
      // Ignore
    }
    Configuration configuration = Configuration.create( configProperties, appDir, parameters );

    if ( configuration.isUninstallSecurityManager() ) {
      System.setSecurityManager( null );
    }

    for ( Entry<String, String> systemProperty : configuration.getSystemProperties().entrySet() ) {
      System.setProperty( systemProperty.getKey(), systemProperty.getValue() );
    }

    final List<URL> jars = FileUtil.populateClasspath( configuration.getClasspath(), appDir, System.err );
    jars.addAll( FileUtil.populateLibraries( configuration.getLibraries(), appDir, System.err ) );
    final URL[] classpathEntries = (URL[]) jars.toArray( new URL[jars.size()] );
    final ClassLoader cl = new URLClassLoader( classpathEntries );
    Thread.currentThread().setContextClassLoader( cl );

    if ( StringUtil.isEmpty( configuration.getMainClass() ) ) {
      System.err.println( "Invalid main-class entry, cannot proceed." );
      System.err.println( "Application Directory: " + appDir );
      System.exit( 1 );
    }

    if ( configuration.isDebug() ) {
      System.out.println( "Application Directory: " + appDir );
      for ( int i = 0; i < classpathEntries.length; i++ ) {
        final URL url = classpathEntries[i];
        System.out.println( "ClassPath[" + i + "] = " + url );
      }
    }

    final Class<?> mainClass = cl.loadClass( configuration.getMainClass() );
    // Invoke main(..)
    final String[] newArgs = new String[args.length - parameters.getParsedArgs()];
    System.arraycopy( args, parameters.getParsedArgs(), newArgs, 0, newArgs.length );
    final Method method = mainClass.getMethod( "main", new Class[] { String[].class } );
    method.invoke( null, new Object[] { newArgs } );
  }
}
