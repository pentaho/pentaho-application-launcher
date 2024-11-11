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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class FileUtilTest {
  PrintStream mockPrintStream;

  @Before
  public void setup() {
    mockPrintStream = mock( PrintStream.class );
  }

  @Test
  public void testDefaultConstructor() {
    // This util class does not privatize the constructor, the test is simply to create a new object
    new FileUtil();
  }

  @Test
  public void testComputeApplicationDirCantLocateDirWarning() {
    File defaultFile = new File( UUID.randomUUID().toString() );
    assertEquals( defaultFile, FileUtil.computeApplicationDir( null, defaultFile, mockPrintStream ) );
    verify( mockPrintStream ).println( FileUtil.CANT_LOCATE_DIR_WARNING );
  }

  @Test
  public void testComputeApplicationDirUnrecognizedFileTypeWarning() throws MalformedURLException {
    File expectedFile = new File( "." );
    assertEquals( expectedFile, FileUtil.computeApplicationDir( new URL( "http://www.google.com" ), null,
      mockPrintStream ) );
    verify( mockPrintStream ).println( FileUtil.UNRECOGNIZED_FILE_TYPE_WARNING );
  }

  @Test
  public void testComputeApplicationDirJarFile() throws MalformedURLException, URISyntaxException {
    URL testFile = new File( "test.jar" ).toURI().toURL();
    File parent = new File( testFile.toURI() ).getParentFile();
    assertEquals( parent, FileUtil.computeApplicationDir( testFile, null, mockPrintStream ) );
    verify( mockPrintStream, times( 0 ) ).println( anyString() );
  }

  @Test
  public void testComputeApplicationDirZipFile() throws MalformedURLException, URISyntaxException {
    URL testFile = new File( "test.zip" ).toURI().toURL();
    File parent = new File( testFile.toURI() ).getParentFile();
    assertEquals( parent, FileUtil.computeApplicationDir( testFile, null, mockPrintStream ) );
    verify( mockPrintStream, times( 0 ) ).println( anyString() );
  }

  @Test
  public void testComputeApplicationDirUriSyntaxException() throws MalformedURLException, URISyntaxException {
    assertNotNull( FileUtil.computeApplicationDir( new URL( "file:// " ), new File( "." ), mockPrintStream ) );
  }

  @Test
  public void testPopulateLibrariesNoPaths() {
    assertEquals( 0, FileUtil.populateLibraries( new ArrayList<String>(), null, null ).size() );
  }

  @Test
  public void testPopulateLibrariesWithPaths() {
    assertTrue( FileUtil.populateLibraries(
      Collections.singletonList( "src/test/resources/test-lib-folder" ), null, null ).size() > 0 );
  }

  @Test
  public void testPopulateClasspathNoPaths() {
    assertEquals( 0, FileUtil.populateClasspath( new ArrayList<String>(), null, null ).size() );
  }

  @Test
  public void testfileListToURLListException() {
    String exception = "Test Exception " + UUID.randomUUID().toString();
    List<File> files = new ArrayList<File>();
    File invalidFile = mock( File.class );
    files.add( invalidFile );
    when( invalidFile.exists() ).thenReturn( true );
    when( invalidFile.canRead() ).thenReturn( true );
    when( invalidFile.getAbsolutePath() ).thenReturn( "/fake/path/123" );
    when( invalidFile.toURI() ).thenThrow( new RuntimeException( exception ) );
    assertEquals( 0, FileUtil.fileListToURLList( files, mockPrintStream ).size() );
    verify( mockPrintStream ).println(
      eq( "Invalid entry, ignoring '" + invalidFile.getAbsolutePath() + "':" + exception ) );
  }

  @Test
  public void testfileListToURLListNonExistent() {
    List<File> files = new ArrayList<File>();
    File invalidFile = mock( File.class );
    files.add( invalidFile );
    when( invalidFile.exists() ).thenReturn( false );
    when( invalidFile.canRead() ).thenReturn( true );
    when( invalidFile.getAbsolutePath() ).thenReturn( "/fake/path/123" );
    assertEquals( 0, FileUtil.fileListToURLList( files, mockPrintStream ).size() );
    verify( mockPrintStream ).println( eq( "Invalid entry, ignoring '" + invalidFile.getAbsolutePath() + "'" ) );
  }

  @Test
  public void testfileListToURLListCantRead() {
    List<File> files = new ArrayList<File>();
    File invalidFile = mock( File.class );
    files.add( invalidFile );
    when( invalidFile.exists() ).thenReturn( true );
    when( invalidFile.canRead() ).thenReturn( false );
    when( invalidFile.getAbsolutePath() ).thenReturn( "/fake/path/123" );
    assertEquals( 0, FileUtil.fileListToURLList( files, mockPrintStream ).size() );
    verify( mockPrintStream ).println( eq( "Invalid entry, ignoring '" + invalidFile.getAbsolutePath() + "'" ) );
  }
}
