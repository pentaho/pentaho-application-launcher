/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.commons.launcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class StringUtilTest {
  @Test
  public void testEndsWithIgnoreCase() {
    assertTrue( StringUtil.endsWithIgnoreCase( "test.java", "JAVA" ) );
    assertTrue( StringUtil.endsWithIgnoreCase( "test.JAVA", "JAVA" ) );
    assertTrue( StringUtil.endsWithIgnoreCase( "test.Java", "JAVA" ) );
    assertFalse( StringUtil.endsWithIgnoreCase( "test.java", "JAV" ) );
    assertFalse( StringUtil.endsWithIgnoreCase( "test.JAVA", "JAV" ) );
    assertFalse( StringUtil.endsWithIgnoreCase( "test.Java", "JAV" ) );
  }

  @Test
  public void testParsePathNull() {
    assertEquals( new ArrayList<String>(), StringUtil.parsePath( null, null ) );
  }

  @Test
  public void testParsePathEmptyString() {
    assertEquals( new ArrayList<String>(), StringUtil.parsePath( "", "," ) );
  }

  @Test
  public void testParsePathOneElement() {
    assertEquals( Arrays.asList( "test" ), StringUtil.parsePath( "test", "," ) );
  }

  @Test
  public void testParsePathMultipleElements() {
    assertEquals( Arrays.asList( "test", "test2" ), StringUtil.parsePath( "test,test2", "," ) );
  }

  @Test
  public void testIsEmptyNull() {
    assertTrue( StringUtil.isEmpty( null ) );
  }

  @Test
  public void testIsEmptyEmptyString() {
    assertTrue( StringUtil.isEmpty( "" ) );
  }

  @Test
  public void testIsEmptyNonEmptyString() {
    assertFalse( StringUtil.isEmpty( "t" ) );
  }
}
