/**
 * Copyright 2001-2007 The eXo platform SAS All rights reserved.
 * Please look at license.txt in info directory for more license detail.
 **/
package org.exoplatform.web.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.WebAppController;
/**
 * Created by the eXo Development team.
 */
public class ApplicationRegister implements ServletContextListener {
  
  protected static Log log = ExoLogger.getLogger("widget:ApplicationRegister");
  
  /**
   * Each time a new widget application war is deployed then widgets are registered into the
   * WebAppController
   */
  public void contextInitialized(ServletContextEvent event) {
    try {
      String applications = event.getServletContext().getInitParameter("exo.application"); 
      String[] classes = applications.split(",") ;
      RootContainer root = RootContainer.getInstance() ;
      //TODO avoid portal hardcode
      PortalContainer pcontainer =  root.getPortalContainer("portal") ;
      WebAppController controller = (WebAppController)pcontainer.getComponentInstanceOfType(WebAppController.class) ;
      ClassLoader loader = Thread.currentThread().getContextClassLoader() ;
      for(String className : classes) {
        className = className.trim() ;
        log.info("Deploy Widget class name: " + className);
        Class type = loader.loadClass(className) ;
        Application application = (Application)type.newInstance() ;
        controller.addApplication(application) ;
      }
    } catch(Exception ex) {
      log.error("Error while deploying a widget", ex);
    }
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}