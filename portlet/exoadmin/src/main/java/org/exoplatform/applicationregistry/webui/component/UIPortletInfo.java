/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import org.exoplatform.applicationregistry.webui.component.UIPortletManagement.PortletExtra;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Sep 11, 2008  
 */
@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIPortletInfo.gtmpl"
)
public class UIPortletInfo extends UIComponent {
  
  private PortletExtra portlet_;
  
  public void setPortlet(PortletExtra portlet) { portlet_ = portlet; }
  public PortletExtra getPortlet() { return portlet_; }
    
}
