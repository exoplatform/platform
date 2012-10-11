package org.exoplatform.platform.common.admin;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import java.net.URI;
import java.net.URISyntaxException;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Servlet used to validate user checking of Terms and conditions. 
 * If validation is ok, use Service to check into JCR
 * At the end, execute a redirection to initial URI
 * @author Clement
 *
 */
public class TermsAndConditionsActionServlet extends HttpServlet {
  private static final long serialVersionUID = 6467955354840693802L;
  
  private static Log logger = ExoLogger.getLogger(TermsAndConditionsActionServlet.class);
  private final static String PARAM_CHECKTC = "checktc";
  
  private TermsAndConditionsService termsAndConditionsService;
  public TermsAndConditionsService getTermsAndConditionsService() {
    if (this.termsAndConditionsService == null) {
      termsAndConditionsService = (TermsAndConditionsService) PortalContainer.getInstance() .getComponentInstanceOfType(TermsAndConditionsService.class);
    }
    return this.termsAndConditionsService;
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Get usefull parameters
    String initialURI = request.getParameter(TermsAndConditionsViewServlet.INITIAL_URI_PARAM);
    String defaultURI = PortalContainer.getCurrentPortalContainerName();
    defaultURI = "/"+ defaultURI; 
    Boolean checkTc = false;
    try {
      checkTc = Boolean.valueOf(request.getParameter(PARAM_CHECKTC));
    }
    catch(Exception e) {
      logger.error("Terms and conditions: impossible to get parameter " + PARAM_CHECKTC, e);
    }
    
    if(initialURI == null || initialURI.length() == 0) {
      initialURI = defaultURI;
    }

    try
    {
      URI uri = new URI(initialURI);
      if (uri.isAbsolute() && !(uri.getHost().equals(request.getServerName())))
      {
         logger.warn("Cannot redirect to an URI outside of the current host. Redirecting to default context.");
         initialURI = defaultURI;
      }
    }
    catch  (URISyntaxException e)
    {
      logger.warn("Initial URI in link is malformed. Redirecting to default context.");
      initialURI = defaultURI;
    }

    // Check tc with service
    if(checkTc) {
      getTermsAndConditionsService().checkTermsAndConditions();
    }
    
    // Redirect to requested page
    response.sendRedirect(initialURI);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

}
