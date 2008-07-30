/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.mail;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;

// TODO: Auto-generated Javadoc
/**
 * Created by The eXo Platform SAS.
 * 
 * @author Hung nguyen Quang
 * @email: hung.nguyen@exoplatform.com
 */
public class EmailNotifyPlugin extends BaseComponentPlugin {

  /** The server configuration_. */
  private Map<String,String> serverConfiguration_ = new HashMap<String, String>();

  /**
   * Instantiates a new email notify plugin.
   * 
   * @param params the params
   */
  @SuppressWarnings("unchecked")
  public EmailNotifyPlugin(InitParams params) {
    PropertiesParam param = params.getPropertiesParam("email.configuration.info");
    if (param != null) {
      serverConfiguration_ = param.getProperties();
    }
  }

  /**
   * Gets the server configuration.
   * 
   * @return the server configuration
   */
  public Map<String, String> getServerConfiguration() {
    return serverConfiguration_;
  }
  
}
