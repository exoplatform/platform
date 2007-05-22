/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail.
 **/
package org.exoplatform.web.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.web.WebAppController;
/**
 * Created by the Exo Development team.
 * Author : Mestrallet Benjamin
 * benjmestrallet@users.sourceforge.net
 * Date: 10 nov. 2003
 * Time: 12:58:52
 */
public class ApplicationRegister implements ServletContextListener {
  public void contextInitialized(ServletContextEvent event) {
    System.out.println("\n=============================================================\n") ;
    try {
      String applications = event.getServletContext().getInitParameter("exo.application"); 
      String[] classes = applications.split(",") ;
      RootContainer root = RootContainer.getInstance() ;
      PortalContainer pcontainer =  root.getPortalContainer("portal") ;
      WebAppController controller = 
        (WebAppController)pcontainer.getComponentInstanceOfType(WebAppController.class) ;
      ClassLoader loader = Thread.currentThread().getContextClassLoader() ;
      for(String className : classes) {
        className = className.trim() ;
        System.out.println("class name: " + className);
        Class type = loader.loadClass(className) ;
        Application application = (Application)type.newInstance() ;
        controller.addApplication(application) ;
        System.out.println("add application: " + application);
      }
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
    System.out.println("\n=============================================================\n") ;
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  } 
}