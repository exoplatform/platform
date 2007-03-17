package org.exoplatform.portal.application;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;

@SuppressWarnings("serial")
public class PortalController  extends HttpServlet {
  private PortalApplication application ;
  
  @SuppressWarnings("unchecked")
  public void init(ServletConfig config) throws ServletException {
    try {
      RootContainer rootContainer = RootContainer.getInstance() ;
      PortalContainer portalContainer = rootContainer.getPortalContainer(config.getServletContext().getServletContextName()) ;
      if(portalContainer != null) {
        rootContainer.removePortalContainer(config.getServletContext()) ;
        portalContainer.stopContainer() ;
      }
      portalContainer = rootContainer.createPortalContainer(config.getServletContext()) ;
      PortalContainer.setInstance(portalContainer) ;
      application = new PortalApplication(config);
      application.init() ;
      portalContainer.registerComponentInstance(PortalApplication.class, application) ;
      PortalContainer.setInstance(null) ;
    } catch (Throwable t){
      throw new ServletException(t) ;
    }
  }
  
  public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    application.service(req, res) ;
  }

  public void destroy() {
    try {
      application.destroy() ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
  }
}