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

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Ba Phu
 *          phului@gmail.com
 * Nov 7, 2007  
 */

@ComponentConfig (
    lifecycle = UIApplicationLifecycle.class, 
    template = "app:/groovy/portal/webui/component/UIHomePagePortlet.gtmpl"    
)

public class UIHomePagePortlet extends UIPortletApplication {

  private static String DEFAULT_TEMPLATE = "app:/groovy/portal/webui/component/UIHomePagePortlet.gtmpl" ;  
  
  public UIHomePagePortlet () throws  Exception {
  
  } 
  
  @Override
  public String getTemplate() {
    PortletRequestContext context = (PortletRequestContext) WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    String template =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ; 
    
    return template;
  }
}
