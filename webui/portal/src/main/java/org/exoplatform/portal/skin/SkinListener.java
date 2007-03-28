/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.exoplatform.portal.skin;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.InputStream;

import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.services.portletcontainer.PortletLifecycleListener;
import org.exoplatform.services.portletcontainer.pci.model.PortletApp;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Jan 19, 2007  
 */
//TODO: Rename to SkinConfigListener    

public class SkinListener extends BaseComponentPlugin 
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
      RootContainer rootContainer = RootContainer.getInstance() ;
      SkinService skinService = 
        (SkinService)rootContainer.getComponentInstanceOfType(SkinService.class);
      binding.setVariable("SkinService", skinService) ;
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
