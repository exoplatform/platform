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
package org.exoplatform.web.application;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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
      //TODO avoid portal hardcode
      ExoContainer pcontainer =  ExoContainerContext.getContainerByName("portal") ;
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