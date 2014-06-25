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
 * Copyright (c) 2002-2014 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.commons.launcher.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ParametersTest {
  PrintStream outputStream;

  @Before
  public void setup() {
    outputStream = mock( PrintStream.class );
  }

  @Test
  public void testNoArgs() {
    String[] args = new String[] {};
    Parameters parameters = Parameters.fromArgs( args, outputStream );
    assertNull( parameters.getMainClass() );
    assertEquals( 0, parameters.getLibraries().size() );
    assertEquals( 0, parameters.getClasspath().size() );
    assertEquals( 0, parameters.getParsedArgs() );
  }

  @Test
  public void testArgs() {
    String[] args =
        new String[] { "-main", "myMain", "-lib", "lib1" + File.pathSeparator + "lib2", "-cp",
          "cp1" + File.pathSeparator + "cp2" };
    Parameters parameters = Parameters.fromArgs( args, outputStream );
    assertEquals( "myMain", parameters.getMainClass() );
    assertEquals( Arrays.asList( "lib1", "lib2" ), parameters.getLibraries() );
    assertEquals( Arrays.asList( "cp1", "cp2" ), parameters.getClasspath() );
    assertEquals( 6, parameters.getParsedArgs() );
  }

  @Test
  public void testDoubleDashArgs() {
    String[] args =
        new String[] { "-main", "myMain", "-lib", "lib1" + File.pathSeparator + "lib2", "-cp",
          "cp1" + File.pathSeparator + "cp2", "--" };
    Parameters parameters = Parameters.fromArgs( args, outputStream );
    assertEquals( "myMain", parameters.getMainClass() );
    assertEquals( Arrays.asList( "lib1", "lib2" ), parameters.getLibraries() );
    assertEquals( Arrays.asList( "cp1", "cp2" ), parameters.getClasspath() );
    assertEquals( 7, parameters.getParsedArgs() );
  }

  @Test
  public void testUnknownArg() {
    String[] args =
        new String[] { "unknown", "-main", "myMain", "-lib", "lib1" + File.pathSeparator + "lib2", "-cp",
          "cp1" + File.pathSeparator + "cp2" };
    Parameters parameters = Parameters.fromArgs( args, outputStream );
    assertNull( parameters.getMainClass() );
    assertEquals( 0, parameters.getLibraries().size() );
    assertEquals( 0, parameters.getClasspath().size() );
    assertEquals( 0, parameters.getParsedArgs() );
  }
}
