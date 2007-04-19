package org.exoplatform.portal.application;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.portal.application.handler.DownloadRequestHandler;
import org.exoplatform.portal.application.handler.ServiceRequestHandler;
import org.exoplatform.portal.application.handler.UploadRequestHandler;
import org.exoplatform.web.WebAppController;

@SuppressWarnings("serial")
public class PortalController  extends HttpServlet {
  
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
      controller.register(new DownloadRequestHandler()) ;
      controller.register(new UploadRequestHandler()) ;
      controller.register(new ServiceRequestHandler()) ;
      PortalContainer.setInstance(null) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
    }
  }
  
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
      t.printStackTrace() ;
    }
  }
}
