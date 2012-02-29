package org.exoplatform.platform.common.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
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
  private final static String PORTAL_URI = "/portal";
  
  private TermsAndConditionsService termsAndConditionsService;
  public TermsAndConditionsService getTermsAndConditionsService() {
    if (this.termsAndConditionsService == null) {
      termsAndConditionsService = (TermsAndConditionsService) PortalContainer.getInstance() .getComponentInstanceOfType(TermsAndConditionsService.class);
    }
    return this.termsAndConditionsService;
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Get usefull parameters
    String initialURI = request.getParameter(TermsAndConditionsViewServlet.INITIAL_URI_PARAM);
    Boolean checkTc = false;
    try {
      checkTc = Boolean.valueOf(request.getParameter(PARAM_CHECKTC));
    }
    catch(Exception e) {
      logger.error("Terms and conditions: impossible to get parameter " + PARAM_CHECKTC, e);
    }
    
    if(initialURI == null || initialURI.length() == 0) {
      initialURI = PORTAL_URI;
    }
    
    // Check tc with service
    if(checkTc) {
      getTermsAndConditionsService().checkTermsAndConditions();
    }
    
    // Redirect to requested page
    response.sendRedirect(initialURI);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

}
