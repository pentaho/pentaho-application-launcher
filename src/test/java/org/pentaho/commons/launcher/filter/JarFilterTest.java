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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.pentaho.commons.launcher.util.FileUtil;

public class JarFilterTest {

  @Test
  public void testJarFilterAcceptsJar() {
    JarFilter jarFilter = new JarFilter();
    assertTrue( jarFilter.accept( new File( FileUtil.JAR_SUFFIX.toLowerCase() ) ) );
    assertTrue( jarFilter.accept( new File( FileUtil.JAR_SUFFIX.toUpperCase() ) ) );
  }

  @Test
  public void testJarFilterAcceptsZip() {
    JarFilter jarFilter = new JarFilter();
    assertTrue( jarFilter.accept( new File( FileUtil.ZIP_SUFFIX.toLowerCase() ) ) );
    assertTrue( jarFilter.accept( new File( FileUtil.ZIP_SUFFIX.toUpperCase() ) ) );
  }

  @Test
  public void testJarFilterDoesntAcceptOther() {
    JarFilter jarFilter = new JarFilter();
    assertFalse( jarFilter.accept( new File( FileUtil.JAR_SUFFIX.toLowerCase() + "a" ) ) );
    assertFalse( jarFilter.accept( new File( FileUtil.JAR_SUFFIX.toUpperCase() + "A" ) ) );
    assertFalse( jarFilter.accept( new File( FileUtil.ZIP_SUFFIX.toLowerCase() + "a" ) ) );
    assertFalse( jarFilter.accept( new File( FileUtil.ZIP_SUFFIX.toUpperCase() + "A" ) ) );
  }
}
