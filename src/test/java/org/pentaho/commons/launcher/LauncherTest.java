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

package org.pentaho.commons.launcher;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit Tests for Launcher
 */
public class LauncherTest {

  private static boolean success = false;

  @Before
  public void setUp() throws Exception {
    success = false;
  }

  @Test
  public void testMain() throws Exception {
    String thisClass = this.getClass().getCanonicalName();
    Launcher.main( new String[]{ "-main", thisClass } );
    assertTrue( success );
  }

  @Test
  public void testMainDebug() throws Exception {
    String thisClass = this.getClass().getCanonicalName();
    Launcher.main( new String[]{ "-main", thisClass, "-debug" } );
    assertTrue( success );
  }

  public static void main( String[] args ) {
    success = true;
  }
}
