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

package org.pentaho.commons.launcher.property;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class PropertyLookupParserTest {
  PropertyGetter mockPropertyGetter;
  PropertyLookupParser propertyLookupParser;

  @Before
  public void setup() {
    mockPropertyGetter = mock( PropertyGetter.class );
    propertyLookupParser = new PropertyLookupParser( mockPropertyGetter );
  }

  @Test
  public void testConstantString() {
    assertEquals( "test123", propertyLookupParser.translateAndLookup( "test123" ) );
    verify( mockPropertyGetter, times( 0 ) ).getProperty( anyString() );
  }

  @Test
  public void testVariableString() {
    String varValue = UUID.randomUUID().toString();
    String property = "testProp";
    when( mockPropertyGetter.getProperty( property ) ).thenReturn( varValue );
    assertEquals( varValue, propertyLookupParser.translateAndLookup( "${" + property + "}" ) );
  }

  @Test
  public void testVariableStringMultiple() {
    String varValue = UUID.randomUUID().toString();
    String varValue2 = UUID.randomUUID().toString();
    String property = "testProp";
    String property2 = "testProp2";
    when( mockPropertyGetter.getProperty( property ) ).thenReturn( varValue );
    when( mockPropertyGetter.getProperty( property2 ) ).thenReturn( varValue2 );
    assertEquals( varValue + "$/" + varValue2, propertyLookupParser.translateAndLookup( "${" + property + "}\\$/${"
        + property2 + "}" ) );
  }
}
