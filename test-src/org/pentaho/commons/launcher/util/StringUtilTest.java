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
