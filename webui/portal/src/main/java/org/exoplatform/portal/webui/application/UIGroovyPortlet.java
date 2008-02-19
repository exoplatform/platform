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
package org.exoplatform.portal.webui.application;

import javax.portlet.PortletRequest;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;

@ComponentConfig()
public class UIGroovyPortlet extends UIPortletApplication {
  
  private String DEFAULT_TEMPLATE = "system:/groovy/portal/webui/application/UIGroovyPortlet.gtmpl" ;  
  private String template_ ;
  private String windowId ;
  
  public UIGroovyPortlet() throws Exception {
    PortletRequestContext context = (PortletRequestContext)  WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;
    template_ =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ;
    windowId = prequest.getWindowID() ;
  }
  
  public String getId() { return windowId + "-portlet" ; }
  public String getTemplate() {  return template_ ;  }
  
  public UIComponent getViewModeUIComponent() { return null; }

}
