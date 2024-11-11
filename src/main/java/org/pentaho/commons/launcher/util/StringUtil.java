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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {

  public static boolean endsWithIgnoreCase( final String base, final String end ) {
    if ( base.length() < end.length() ) {
      return false;
    }
    return base.regionMatches( true, base.length() - end.length(), end, 0, end.length() );
  }

  public static List<String> parsePath( final String path, final String separator ) {
    List<String> result = new ArrayList<String>();
    if ( path == null ) {
      return result;
    }

    final StringTokenizer strtok = new StringTokenizer( path, separator );
    while ( strtok.hasMoreTokens() ) {
      result.add( strtok.nextToken() );
    }
    return result;
  }

  public static boolean isEmpty( String str ) {
    return str == null || str.length() == 0;
  }
}
