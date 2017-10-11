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

package org.pentaho.commons.launcher.config;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pentaho.commons.launcher.util.StringUtil;

public class Parameters {
  private final List<String> classpath;

  private final List<String> libraries;

  private final String mainClass;

  private final int parsedArgs;

  public Parameters( String mainClass, List<String> libraries, List<String> classpath, int parsedArgs ) {
    this.mainClass = mainClass;
    this.libraries = Collections.unmodifiableList( new ArrayList<String>( libraries ) );
    this.classpath = Collections.unmodifiableList( new ArrayList<String>( classpath ) );
    this.parsedArgs = parsedArgs;
  }

  public static Parameters fromArgs( String[] args, PrintStream outputPrintStream ) {
    String mainClass = null;
    List<String> libraries = new ArrayList<String>();
    List<String> classpath = new ArrayList<String>();
    for ( int i = 0; i < args.length; i++ ) {
      final String arg = args[i];
      if ( "-main".equals( arg ) ) {
        i += 1;
        if ( i == args.length ) {
          outputPrintStream.println( "Argument parse error: '-main' needs an parameter" );
          System.exit( 1 );
        }
        mainClass = args[i];
      } else if ( "-lib".equals( arg ) ) {
        i += 1;
        if ( i == args.length ) {
          outputPrintStream.println( "Argument parse error: '-lib' needs an parameter" );
          System.exit( 1 );
        }
        libraries.addAll( StringUtil.parsePath( args[i], File.pathSeparator ) );
      } else if ( "-cp".equals( arg ) || "-classpath".equals( arg ) ) {
        i += 1;
        if ( i == args.length ) {
          outputPrintStream.println( "Argument parse error: '-cp' needs an parameter" );
          System.exit( 1 );
        }
        classpath.addAll( StringUtil.parsePath( args[i], File.pathSeparator ) );
      } else if ( "--".equals( arg ) ) {
        return new Parameters( mainClass, libraries, classpath, i + 1 );
      } else {
        return new Parameters( mainClass, libraries, classpath, i );
      }
    }
    return new Parameters( mainClass, libraries, classpath, args.length );
  }

  public List<String> getClasspath() {
    return classpath;
  }

  public List<String> getLibraries() {
    return libraries;
  }

  public String getMainClass() {
    return mainClass;
  }

  public int getParsedArgs() {
    return parsedArgs;
  }
}
