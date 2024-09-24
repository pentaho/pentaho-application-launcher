/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
