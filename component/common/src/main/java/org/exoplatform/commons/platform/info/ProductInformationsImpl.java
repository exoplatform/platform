package org.exoplatform.commons.platform.info;

import java.io.IOException;
import java.util.Properties;

import org.exoplatform.component.product.ProductInformations;

public class ProductInformationsImpl implements ProductInformations{
  private static final String filterPropertiesPath = "conf/portal/filter.properties";

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.version property.
   * This method return the platform version.
   */
  public String getVersion() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.version");
    } catch (IOException e) {
      return "";
    }
  }

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.buildNumber property.
   * This method return the build number of the platform.
   */
  public String getBuildNumber() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.buildNumber");
    } catch (IOException e) {
      return "";
    }
  }

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.revision property.
   * This method return the current revison of the platform.
   */
  public String getRevision() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.revision");
    } catch (IOException e) {
      return "";
    }
  }
}
