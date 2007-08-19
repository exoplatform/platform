package org.exoplatform.portal.application;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
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
 * Basically, this class is just dispacther as the real business logic is implemented inside 
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
      PortalContainer.setInstance(null) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
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
      PortalContainer.setInstance(null) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
    }
  }
}
