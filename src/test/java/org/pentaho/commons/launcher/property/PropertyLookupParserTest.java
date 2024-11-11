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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
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

  @Test
  public void testGetSetOpeningBraceChar() {
    assertEquals( '{', propertyLookupParser.getOpeningBraceChar() );
    propertyLookupParser.setOpeningBraceChar( '[' );
    assertEquals( '[', propertyLookupParser.getOpeningBraceChar() );
  }

  @Test
  public void testGetSetClosingBraceChar() {
    assertEquals( '}', propertyLookupParser.getClosingBraceChar() );
    propertyLookupParser.setClosingBraceChar( ']' );
    assertEquals( ']', propertyLookupParser.getClosingBraceChar() );
  }

  @Test
  public void testGetSetEscapeChar() {
    assertEquals( '\\', propertyLookupParser.getEscapeChar() );
    propertyLookupParser.setEscapeChar( ']' );
    assertEquals( ']', propertyLookupParser.getEscapeChar() );
  }

  @Test
  public void testGetSetMarkerChar() {
    assertEquals( '$', propertyLookupParser.getMarkerChar() );
    propertyLookupParser.setMarkerChar( ']' );
    assertEquals( ']', propertyLookupParser.getMarkerChar() );
  }

  @Test
  public void testTranslateAndLookupNullValue() {
    assertNull( propertyLookupParser.translateAndLookup( null ) );
    assertEquals( "${", propertyLookupParser.translateAndLookup( "${" ) );
    assertEquals( "$}", propertyLookupParser.translateAndLookup( "$}" ) );

  }
}
