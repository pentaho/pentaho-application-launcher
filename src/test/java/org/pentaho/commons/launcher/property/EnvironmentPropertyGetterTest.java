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

package org.pentaho.commons.launcher.property;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class EnvironmentPropertyGetterTest {
  @Test
  public void testSysAppDir() {
    String absPath = new File( "" ).getAbsolutePath();
    assertEquals( absPath, new EnvironmentPropertyGetter( new File( absPath ) ).getProperty( "SYS:APP_DIR" ) );
  }

  @Test
  public void testEnvironmentVar() {
    assertEquals( System.getenv( "PATH" ), new EnvironmentPropertyGetter( new File( "" ) ).getProperty( "PATH" ) );
  }
}
