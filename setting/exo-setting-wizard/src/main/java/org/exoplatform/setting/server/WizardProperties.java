package org.exoplatform.setting.server;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/***
 * Contains methods to access to properties configured into SetupWizard
 * 
 * @author Clement
 *
 */
public class WizardProperties {
  
  private static PropertiesConfiguration propertiesConfiguration;
  private static Logger logger = Logger.getLogger(WizardProperties.class);
  
  private static PropertiesConfiguration getConf() {
    if(propertiesConfiguration == null) {
      try {
        propertiesConfiguration = new PropertiesConfiguration("setup-wizard.properties");
      }
      catch (ConfigurationException e) {
        logger.error("Error during loading wizard properties: " + e.getMessage(), e);
      }
    }
    return propertiesConfiguration;
  }
  
  /**
   * Debug mode 
   * @return true or false
   */
  public static Boolean getDebug() {
    return getConf().getBoolean("exo.setupwizard.debug");
  }
  
  /**
   * First screen number
   * @return
   */
  public static Integer getFirstScreenNumber() {
    return getConf().getInteger("exo.setupwizard.first.screen", 0);
  }
  
  /**
   * Get configuration of jndi name according to the server
   * @param serverName
   * @return
   */
  public static String getDatasourceJndiName(String serverName) {
    return getConf().getString("exo.setupwizard.datasource.jndi.name." + serverName);
  }
  
  /**
   * Get configuration of jndi name according to the server
   * @param serverName
   * @return
   */
  public static String getExoConfigurationPropertiesPath(String serverName) {
    return getConf().getString("exo.setupwizard.configuration.properties.path." + serverName);
  }
}
