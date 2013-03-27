package org.exoplatform.platform.welcomescreens.web;

import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.welcomescreens.service.TermsAndConditionsService;
import org.exoplatform.platform.welcomescreens.service.UnlockService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.filter.Filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter responsible of Terms and conditions displaying.
 * Call T&C service to know if T&C are checked, if not, forward to T&C page
 * <p>
 * 2 conditions to forward to termes and conditions page:
 * <ul>
 * <li>Request URI is not a login URI. In this case we need to execute T&C process after login process</li>
 * <li>T&C is not checked</li>
 * </ul>
 * 
 * @author Clement
 *
 */
public class TermsAndConditionsFilter implements Filter {
  private static Log logger = ExoLogger.getLogger(TermsAndConditionsFilter.class);
  
  private TermsAndConditionsService termsAndConditionsService;

  private static final String PLF_WELCOME_SCREENS_SERVLET_CTX = "/trial";
  private static final String TC_SERVLET_URL = "/terms-and-conditions";
  private static final String INITIAL_URI_PARAM_NAME = "initialURI";
  private static final String LOGIN_URI = "/login";
  private static final String DOLOGIN_URI = "/dologin";
  private static String REST_URI;

  public TermsAndConditionsFilter() {
      REST_URI = ExoContainerContext.getCurrentContainer().getContext().getRestContextName();
  }

  public TermsAndConditionsService getTermsAndConditionsService() {
    if(termsAndConditionsService == null) {
      termsAndConditionsService = (TermsAndConditionsService)PortalContainer.getInstance().getComponentInstanceOfType(TermsAndConditionsService.class);
    }
    return termsAndConditionsService;
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    HttpServletResponse httpServletResponse = (HttpServletResponse)response;
    boolean tcChecked = getTermsAndConditionsService().isTermsAndConditionsChecked();
    
    String requestUri = httpServletRequest.getRequestURI();
    String loginRequestUri = httpServletRequest.getContextPath() + LOGIN_URI;
    String dologinRequestUri = httpServletRequest.getContextPath() + DOLOGIN_URI;
    boolean isLoginUri = (requestUri.contains(loginRequestUri) || requestUri.contains(dologinRequestUri));
    boolean isRestUri = (requestUri.contains(REST_URI));
    boolean isDevMod = PropertyManager.isDevelopping();
    if(! isLoginUri && ! isRestUri && ! tcChecked && !isDevMod && UnlockService.showTermsAndConditions()) {
      // Get full url
      String reqUri = httpServletRequest.getRequestURI().toString();
      String queryString = httpServletRequest.getQueryString();
      if (queryString != null) {
          reqUri += "?"+queryString;
      }
      
      // Get plf extension servlet context (because TermsAndConditionsFilter and terms-and-conditions servlet declaration are not on same context (webapp))
      ServletContext welcomrScreensContext = httpServletRequest.getSession().getServletContext().getContext(PLF_WELCOME_SCREENS_SERVLET_CTX);
      // Forward to resource from this context: 
      String uriTarget = (new StringBuilder()).append(TC_SERVLET_URL + "?" + INITIAL_URI_PARAM_NAME + "=").append(reqUri).toString();
      welcomrScreensContext.getRequestDispatcher(uriTarget).forward(httpServletRequest, httpServletResponse);
      return;
    }
    chain.doFilter(request, response);
  }
}
