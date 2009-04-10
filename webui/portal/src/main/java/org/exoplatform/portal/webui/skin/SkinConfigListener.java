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

package org.exoplatform.portal.webui.skin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.InputStream;

import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.services.portletcontainer.PortletLifecycleListener;
import org.exoplatform.services.portletcontainer.pci.model.PortletApp;
/**
 * Created by The eXo Platform SAS
 * Jan 19, 2007  
 */
public class SkinConfigListener extends BaseComponentPlugin 
  implements PortletLifecycleListener,  ServletContextListener {

  public void contextInitialized(ServletContextEvent event) {
    preDeploy(null, null, event.getServletContext()) ;
  }

  public void contextDestroyed(ServletContextEvent arg0) {

  }
  
  public void preDeploy(String appName, PortletApp portletApp, ServletContext scontext) {
    try {
      InputStream is = scontext.getResourceAsStream("/WEB-INF/conf/script/groovy/SkinConfigScript.groovy") ;
      if(is == null)  return ;
      
      Binding binding = new Binding();
      ExoContainer rootContainer = ExoContainerContext.getTopContainer();
      org.exoplatform.portal.skin.SkinService skinService = 
        (org.exoplatform.portal.skin.SkinService)rootContainer.getComponentInstanceOfType(org.exoplatform.portal.skin.SkinService.class);
      binding.setVariable("SkinService", skinService) ;
      binding.setVariable("ServletContext", scontext) ;      
      GroovyShell shell = new GroovyShell(binding);
      shell.evaluate(is);
      is.close() ;
    } catch (Exception ex) {
      ex.printStackTrace() ;
    }
  }

  public void postDeploy(String appName, PortletApp portletApp, ServletContext scontext) {

  }

  public void preInit(PortletConfig arg0) { }

  public void postInit(PortletConfig arg0) { }

  public void preDestroy() {  }

  public void postDestroy() { }

  public void preUndeploy(String appName, PortletApp portletApp, ServletContext scontext) {
  }

  public void postUndeploy(String appName, PortletApp portletApp, ServletContext arg2) {   }
}
