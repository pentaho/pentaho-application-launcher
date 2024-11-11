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


package org.pentaho.commons.launcher.property;

import java.util.Stack;

/**
 * The property lookup parser is used to resolve embedded references to properties within strings.
 * <p/>
 * The default format of the property specification is: <code>${property-name}</code> where 'property-name is the name
 * of the property. If this construct is found within the text, it is replaced with the value returned from a call to
 * "lookupVariable".
 * 
 * @author Thomas Morgner
 */
public class PropertyLookupParser {

  public static final int ESCAPE_MODE_NONE = 0;
  public static final int ESCAPE_MODE_ALL = 2;
  public static final int ESCAPE_MODE_STRICT = 1;
  /**
   * A parse state indicator signaling that the parser is outside a property.
   */
  private static final int EXPECT_DOLLAR = 0;
  /**
   * A parse state indicator signaling that an open brace is expected.
   */
  private static final int EXPECT_OPEN_BRACE = 1;

  /**
   * A parse state indicator signaling that a closed brace is expected. All chars received, which are not equal to the
   * closed brace, count as property name.
   */
  private static final int EXPECT_CLOSE_BRACE = 3;
  /**
   * The initial marker char, a $ by default.
   */
  private char markerChar;
  /**
   * The closing brace char.
   */
  private char closingBraceChar;
  /**
   * The opening brace char.
   */
  private char openingBraceChar;
  /**
   * The escape char.
   */
  private char escapeChar;

  private int escapeMode;

  private final PropertyGetter propertyGetter;

  /**
   * Initializes the parser to the default format of "${..}". The escape char will be a backslash.
   */
  public PropertyLookupParser( PropertyGetter propertyGetter ) {
    markerChar = '$';
    closingBraceChar = '}';
    openingBraceChar = '{';
    escapeChar = '\\';
    escapeMode = ESCAPE_MODE_STRICT;
    this.propertyGetter = propertyGetter;
  }

  /**
   * Returns the currently defined closed-brace char.
   * 
   * @return the closed-brace char.
   */
  public char getClosingBraceChar() {
    return closingBraceChar;
  }

  /**
   * Defines the closing brace character.
   * 
   * @param closingBraceChar
   *          the closed-brace character.
   */
  public void setClosingBraceChar( final char closingBraceChar ) {
    this.closingBraceChar = closingBraceChar;
  }

  /**
   * Returns the escape char.
   * 
   * @return the escape char.
   */
  public char getEscapeChar() {
    return escapeChar;
  }

  /**
   * Defines the escape char.
   * 
   * @param escapeChar
   *          the escape char
   */
  public void setEscapeChar( final char escapeChar ) {
    this.escapeChar = escapeChar;
  }

  /**
   * Returns the currently defined opening-brace char.
   * 
   * @return the opening-brace char.
   */
  public char getOpeningBraceChar() {
    return openingBraceChar;
  }

  /**
   * Defines the opening brace character.
   * 
   * @param openingBraceChar
   *          the opening-brace character.
   */
  public void setOpeningBraceChar( final char openingBraceChar ) {
    this.openingBraceChar = openingBraceChar;
  }

  /**
   * Returns initial property marker char.
   * 
   * @return the initial property marker character.
   */
  public char getMarkerChar() {
    return markerChar;
  }

  /**
   * Defines initial property marker char.
   * 
   * @param markerChar
   *          the initial property marker character.
   */
  public void setMarkerChar( final char markerChar ) {
    this.markerChar = markerChar;
  }

  /**
   * Translates the given string and resolves the embedded property references.
   * 
   * @param value
   *          the raw value,
   * @return the fully translated string.
   */
  public String translateAndLookup( final String value ) {
    if ( value == null ) {
      return null;
    }

    final char[] chars = value.toCharArray();
    StringBuffer result = new StringBuffer( chars.length );

    boolean haveEscape = false;
    int state = PropertyLookupParser.EXPECT_DOLLAR;
    final Stack<StringBuffer> stack = new Stack<StringBuffer>();

    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[i];

      if ( haveEscape ) {
        haveEscape = false;
        if ( state == PropertyLookupParser.EXPECT_CLOSE_BRACE || escapeMode == ESCAPE_MODE_ALL ) {
          result.append( c );
        } else {
          if ( c == openingBraceChar || c == closingBraceChar || c == escapeChar || c == markerChar ) {
            result.append( c );
          } else {
            result.append( escapeChar );
            result.append( c );
          }
        }
        continue;
      }

      if ( ( state == PropertyLookupParser.EXPECT_DOLLAR || state == PropertyLookupParser.EXPECT_CLOSE_BRACE )
          && c == markerChar ) {
        state = PropertyLookupParser.EXPECT_OPEN_BRACE;
        continue;
      }

      if ( state == PropertyLookupParser.EXPECT_CLOSE_BRACE && c == closingBraceChar ) {
        final String columnName = result.toString();
        result = (StringBuffer) stack.pop();
        handleVariableLookup( result, columnName );

        if ( stack.isEmpty() ) {
          state = PropertyLookupParser.EXPECT_DOLLAR;
        } else {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }
        continue;
      }

      if ( state == PropertyLookupParser.EXPECT_OPEN_BRACE ) {
        if ( c == openingBraceChar ) {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
          stack.push( result );
          result = new StringBuffer( 100 );
          continue;
        }

        result.append( markerChar );
        if ( stack.isEmpty() ) {
          state = PropertyLookupParser.EXPECT_DOLLAR;
        } else {
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }

        // continue with adding the current char ..
      }

      if ( c == escapeChar && escapeMode != ESCAPE_MODE_NONE ) {
        haveEscape = true;
        continue;
      }

      result.append( c );
    }

    if ( state != PropertyLookupParser.EXPECT_DOLLAR ) {
      while ( stack.isEmpty() == false ) {
        final String columnName = result.toString();
        result = (StringBuffer) stack.pop();
        result.append( markerChar );
        if ( state != PropertyLookupParser.EXPECT_OPEN_BRACE ) {
          result.append( openingBraceChar );
          result.append( columnName );
          state = PropertyLookupParser.EXPECT_CLOSE_BRACE;
        }
      }
    }
    return result.toString();
  }

  protected void handleVariableLookup( final StringBuffer result, final String columnName ) {
    final String s = propertyGetter.getProperty( columnName );
    if ( s != null ) {
      result.append( s );
    }
  }
}
