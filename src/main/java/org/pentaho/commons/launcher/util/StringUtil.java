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
