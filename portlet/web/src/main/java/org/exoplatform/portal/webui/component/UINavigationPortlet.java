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
package org.exoplatform.portal.webui.component;

import javax.portlet.PortletRequest;

import org.exoplatform.portal.webui.navigation.UIPortalNavigation;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UIHorizontalNavigation",    
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})

public class UINavigationPortlet extends UIPortletApplication {
  private static String DEFAULT_TEMPLATE = "app:/groovy/portal/webui/component/UIPortalNavigation.gtmpl" ;
  public UINavigationPortlet () throws  Exception { 
    PortletRequestContext context = (PortletRequestContext)  WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    String template =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ;    
    UIPortalNavigation portalNavigation = addChild(UIPortalNavigation.class, "UIHorizontalNavigation", null);
    portalNavigation.getComponentConfig().setTemplate(template) ;
  }  
}