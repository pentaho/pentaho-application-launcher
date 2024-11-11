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


package org.pentaho.commons.launcher.filter;

import java.io.File;
import java.io.FileFilter;

import org.pentaho.commons.launcher.util.FileUtil;
import org.pentaho.commons.launcher.util.StringUtil;

public class JarFilter implements FileFilter {

  @Override
  public boolean accept( final File pathname ) {
    final String base = pathname.getName();
    return ( StringUtil.endsWithIgnoreCase( base, FileUtil.JAR_SUFFIX ) || StringUtil.endsWithIgnoreCase( base,
        FileUtil.ZIP_SUFFIX ) );
  }
}
