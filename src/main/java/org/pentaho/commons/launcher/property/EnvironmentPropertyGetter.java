/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.commons.launcher.property;

import java.io.File;

public class EnvironmentPropertyGetter implements PropertyGetter {
  private File applicationDirectory;

  public EnvironmentPropertyGetter( final File applicationDirectory ) {
    this.applicationDirectory = applicationDirectory;
  }

  /**
   * Looks up the property with the given name.
   * 
   * @param property
   *          the name of the property to look up.
   * @return the translated value.
   */
  @Override
  public String getProperty( String property ) {
    if ( property.equals( "SYS:APP_DIR" ) ) {
      return applicationDirectory.getAbsolutePath();
    }
    return System.getenv( property );
  }
}
