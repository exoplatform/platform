/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.commons.platform.info;

import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:anouar.chattouna@exoplatform.com">Anouar Chattouna</a>
 * @version $Revision$
 */
public final class PlatformInfo {
  private static final String filterPropertiesPath = "conf/portal/filter.properties";

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.version property.
   * This method return the platform version.
   */
  public static String getVersion() {
    Properties properties = new Properties();
    try {
      properties.load(PlatformInfo.class.getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.version");
    } catch (IOException e) {
      return "";
    }
  }

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.buildNumber property.
   * This method return the build number of the platform.
   */
  public static String getBuildNumber() {
    Properties properties = new Properties();
    try {
      properties.load(PlatformInfo.class.getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.buildNumber");
    } catch (IOException e) {
      return "";
    }
  }

  /**
   * @return an empty string if the properties file is not found, otherwise the platform.revision property.
   * This method return the current revison of the platform.
   */
  public static String getRevision() {
    Properties properties = new Properties();
    try {
      properties.load(PlatformInfo.class.getClassLoader().getResourceAsStream(filterPropertiesPath));
      return properties.getProperty("platform.revision");
    } catch (IOException e) {
      return "";
    }
  }
}
