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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {
  Parameters mockParameters;

  @Before
  public void setup() {
    mockParameters = mock( Parameters.class );
  }

  @Test
  public void testLibraries() {
    List<String> paramLibraries = Arrays.asList( "test1", "test2" );
    List<String> libraries = Arrays.asList( "test3", "test4" );
    List<String> concat = new ArrayList<String>( paramLibraries );
    concat.addAll( libraries );
    when( mockParameters.getLibraries() ).thenReturn( paramLibraries );
    Configuration configuration =
        new Configuration( libraries, new ArrayList<String>(), false, null, new HashMap<String, String>(), false,
            mockParameters );
    assertEquals( concat, configuration.getLibraries() );
  }

  @Test
  public void testClasspath() {
    List<String> paramClasspath = Arrays.asList( "test1", "test2" );
    List<String> classpath = Arrays.asList( "test3", "test4" );
    List<String> concat = new ArrayList<String>( paramClasspath );
    concat.addAll( classpath );
    when( mockParameters.getClasspath() ).thenReturn( paramClasspath );
    Configuration configuration =
        new Configuration( new ArrayList<String>(), classpath, false, null, new HashMap<String, String>(), false,
            mockParameters );
    assertEquals( concat, configuration.getClasspath() );
  }

  @Test
  public void testMainClassWithParameter() {
    when( mockParameters.getMainClass() ).thenReturn( "param" );
    Configuration configuration =
        new Configuration( new ArrayList<String>(), new ArrayList<String>(), false, "config",
            new HashMap<String, String>(), false, mockParameters );
    assertEquals( "param", configuration.getMainClass() );
  }

  @Test
  public void testMainClassWithoutParameter() {
    when( mockParameters.getMainClass() ).thenReturn( null );
    Configuration configuration =
        new Configuration( new ArrayList<String>(), new ArrayList<String>(), false, "config",
            new HashMap<String, String>(), false, mockParameters );
    assertEquals( "config", configuration.getMainClass() );
  }

  @Test
  public void testCreate() {
    Properties p = new Properties();
    p.setProperty( "main", "config" );
    p.setProperty( "debug", "true" );
    p.setProperty( "libraries", "test:lib" );
    p.setProperty( "classpath", "test:class" );
    p.setProperty( "system-property.test1", "abc" );
    p.setProperty( "system-property.test2", "123" );
    p.setProperty( "uninstall-security-manager", "true" );
    Configuration result = Configuration.create( p, new File( "." ), mockParameters );
    assertEquals( "config", result.getMainClass() );
    assertTrue( result.isDebug() );
    assertEquals( Arrays.asList( "test", "lib" ), result.getLibraries() );
    assertEquals( Arrays.asList( "test", "class" ), result.getClasspath() );
    assertEquals( 2, result.getSystemProperties().size() );
    assertEquals( "abc", result.getSystemProperties().get( "test1" ) );
    assertEquals( "123", result.getSystemProperties().get( "test2" ) );
    assertTrue( result.isUninstallSecurityManager() );
  }

  @Test
  public void testCreateNulls() {
    Properties p = new Properties();
    Configuration result = Configuration.create( p, new File( "." ), mockParameters );
    assertNull( result.getMainClass() );
    assertFalse( result.isDebug() );
    assertEquals( Arrays.asList( ), result.getLibraries() );
    assertEquals( Arrays.asList( ), result.getClasspath() );
    assertEquals( 0, result.getSystemProperties().size() );
    assertFalse( result.isUninstallSecurityManager() );
  }
}
