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
package org.exoplatform.portal.application;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.WebAppController;

/**
 * The PortalContainer servlet is the main entry point for the eXo Portal product.
 * 
 * Both the init() and service() methods are implemented. The first one is used to configure all the
 * portal resources to prepare the platform to receive requests. The second one is used to handle them.
 * 
 * Basically, this class is just dispatcher as the real business logic is implemented inside 
 * the WebAppController class.
 */
@SuppressWarnings("serial")
public class PortalController  extends HttpServlet {
  
  protected static Log log = ExoLogger.getLogger("portal:PortalController");  
  
  /**
   * The init() method is used to prepare the portal to receive requests. 
   * 
   *  1) Create the PortalContainer and store it inside the ThreadLocal object. The PortalContainer is
   *     a child of the RootContainer
   *  2) Get the WebAppController component from the container
   *  3) Create a new PortalApplication, init it with the ServletConfig object (which contains init params)
   *  4) Register that PortalApplication inside WebAppController
   *  5) Create a new PortalRequestHandler object and register it in the WebAppController
   *  6) Release the PortalContainer ThreadLocal 
   */
  @SuppressWarnings("unchecked")
  public void init(ServletConfig config) throws ServletException {
    super.init(config) ;
    try {
      RootContainer rootContainer = RootContainer.getInstance() ;
      PortalContainer portalContainer = 
        rootContainer.getPortalContainer(config.getServletContext().getServletContextName()) ;
      portalContainer = rootContainer.createPortalContainer(config.getServletContext()) ;
      PortalContainer.setInstance(portalContainer) ;
      WebAppController controller = 
        (WebAppController)portalContainer.getComponentInstanceOfType(WebAppController.class) ;
      PortalApplication application = new PortalApplication(config);
      application.onInit() ;
      controller.addApplication(application) ;
      controller.register(new PortalRequestHandler()) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
    } finally {
      try {
        PortalContainer.setInstance(null) ;
      } catch (Exception e) {
        log.warn("An error occured while cleaning the ThreadLocal", e);
      }      
    }
    log.info("Init of PortalController Servlet successful");
  }
  
  /**
   * This method simply delegates the incoming call to the WebAppController stored in the Portal Container object
   */
  public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    try {
      ServletConfig config =  getServletConfig() ;
      RootContainer rootContainer = RootContainer.getInstance() ;
      PortalContainer portalContainer = 
        rootContainer.getPortalContainer(config.getServletContext().getServletContextName()) ;
      PortalContainer.setInstance(portalContainer) ;
      WebAppController controller = 
        (WebAppController)portalContainer.getComponentInstanceOfType(WebAppController.class) ;
      controller.service(req, res) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
    } finally {
      try {
        PortalContainer.setInstance(null) ;
      } catch (Exception e) {
        log.warn("An error occured while cleaning the ThreadLocal", e);
      }      
    }
  }
}
