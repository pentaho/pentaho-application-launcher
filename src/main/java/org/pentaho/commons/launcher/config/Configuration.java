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


package org.pentaho.commons.launcher.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.pentaho.commons.launcher.property.EnvironmentPropertyGetter;
import org.pentaho.commons.launcher.property.PropertyLookupParser;
import org.pentaho.commons.launcher.util.StringUtil;

public class Configuration {
  private final List<String> classpath;

  private final boolean debug;

  private final List<String> libraries;

  private final String mainClass;

  private final Map<String, String> systemProperties;

  private final boolean uninstallSecurityManager;

  private final Parameters parameters;

  public Configuration( List<String> libraries, List<String> classpath, boolean debug, String mainClass,
      Map<String, String> systemProperties, boolean uninstallSecurityManager, Parameters parameters ) {
    this.libraries = Collections.unmodifiableList( new ArrayList<String>( libraries ) );
    this.classpath = Collections.unmodifiableList( new ArrayList<String>( classpath ) );
    this.debug = debug;
    this.mainClass = mainClass;
    this.systemProperties = Collections.unmodifiableMap( new HashMap<String, String>( systemProperties ) );
    this.uninstallSecurityManager = uninstallSecurityManager;
    this.parameters = parameters;
  }

  public static Configuration create( Properties p, final File applicationDirectory, Parameters parameters ) {
    String mainClass = p.getProperty( "main" );
    boolean debug = "true".equals( p.getProperty( "debug", "false" ) );
    List<String> libraries = StringUtil.parsePath( p.getProperty( "libraries" ), ":" );
    List<String> classpath = StringUtil.parsePath( p.getProperty( "classpath" ), ":" );
    Map<String, String> systemProperties = new HashMap<String, String>();

    final PropertyLookupParser parser =
        new PropertyLookupParser( new EnvironmentPropertyGetter( applicationDirectory ) );
    final Enumeration<Object> keys = p.keys();
    while ( keys.hasMoreElements() ) {
      final String key = (String) keys.nextElement();
      if ( key.startsWith( "system-property." ) == false ) {
        continue;
      }
      final String propertyName = key.substring( "system-property.".length() );
      final String propertyValue = p.getProperty( key );
      final String translatedValue = parser.translateAndLookup( propertyValue );
      if ( translatedValue != null && "".equals( translatedValue ) == false ) {
        systemProperties.put( propertyName, translatedValue );
      }
    }

    boolean uninstallSecurityManager = "true".equals( p.getProperty( "uninstall-security-manager", "false" ) );

    return new Configuration( libraries, classpath, debug, mainClass, systemProperties, uninstallSecurityManager,
        parameters );
  }

  private List<String> concat( List<String> first, List<String> second ) {
    List<String> result = new ArrayList<String>( first.size() + second.size() );
    result.addAll( first );
    result.addAll( second );
    return result;
  }

  public List<String> getClasspath() {
    return concat( parameters.getClasspath(), classpath );
  }

  public List<String> getLibraries() {
    return concat( parameters.getLibraries(), libraries );
  }

  public String getMainClass() {
    if ( !StringUtil.isEmpty( parameters.getMainClass() ) ) {
      return parameters.getMainClass();
    }
    return mainClass;
  }

  public boolean isDebug() {
    return debug;
  }

  public Map<String, String> getSystemProperties() {
    return systemProperties;
  }

  public boolean isUninstallSecurityManager() {
    return uninstallSecurityManager;
  }
}
