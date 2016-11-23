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
