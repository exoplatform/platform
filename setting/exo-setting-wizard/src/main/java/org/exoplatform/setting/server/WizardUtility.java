package org.exoplatform.setting.server;

import org.exoplatform.container.monitor.jvm.J2EEServerInfo;

/**
 * This class contains only static and testable methods.
 * <p>
 * Provides some utility methods
 * 
 * @author Clement
 *
 */
public class WizardUtility {
  
  /**
   * J2EEServerInfo is a class packaged into platform
   */
  private static J2EEServerInfo j2eeServerInfo;
  private static J2EEServerInfo getJ2EEServerInfo() {
    if(j2eeServerInfo == null) {
      j2eeServerInfo = new J2EEServerInfo();
    }
    return j2eeServerInfo;
  }
  
  /**
   * 
   * @return current server name
   */
  public static String getCurrentServerName() {
    return getJ2EEServerInfo().getServerName();
  }
  
  /**
   * 
   * @return current server home path
   */
  public static String getCurrentServerHome() {
    return getJ2EEServerInfo().getServerHome();
  }
  
  /**
   * Returns jndi name according to the server type 
   * 
   * @param serverType tomcat, jboss, ...
   * @return
   */
  public static String getDatasourceJndiName(String serverName) {
    return WizardProperties.getDatasourceJndiName(serverName);
  }
  
  /**
   * 
   * @return Path of Exo configuration properties file
   */
  public static String getExoConfigurationPropertiesPath(String serverName) {
    String path = getCurrentServerHome();
    path += WizardProperties.getExoConfigurationPropertiesPath(serverName);
    return path;
  }
}
