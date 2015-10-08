package org.exoplatform.platform.common.software.register.web;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.software.register.UnlockService;
import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.filter.Filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
public class SoftwareRegisterFilter implements Filter {

  private static final Log logger = ExoLogger.getLogger(SoftwareRegisterFilter.class);
  private static final String PLF_COMMUNITY_SERVLET_CTX = "/registrationPLF";
  private static final String SR_SERVLET_URL = "/software-register";
  private static final String INITIAL_URI_PARAM_NAME = "initialURI";
  private static final String LOGIN_URI = "/login";
  private static final String DOLOGIN_URI = "/dologin";
  private static String REST_URI;
  private SoftwareRegistrationService plfRegisterService;

  public SoftwareRegisterFilter() {
    REST_URI = ExoContainerContext.getCurrentContainer().getContext().getRestContextName();
  }

  private boolean checkRequest(boolean requestSkip){
    plfRegisterService = PortalContainer.getInstance().getComponentInstanceOfType(SoftwareRegistrationService.class);
    if(!requestSkip) return true;
    if(!(UnlockService.isUnlocked() && plfRegisterService.canSkipRegister()) || (UnlockService.isUnlocked())){
      return false;
    }
    return true;
  }
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    HttpServletResponse httpServletResponse = (HttpServletResponse)response;
    plfRegisterService = PortalContainer.getInstance().getComponentInstanceOfType(SoftwareRegistrationService.class);
    String requestUri = httpServletRequest.getRequestURI();
    boolean isRestUri = (requestUri.contains(REST_URI));
    boolean requestSkip = plfRegisterService.isRequestSkip();
    String notReacheble = (String)httpServletRequest.getAttribute("notReacheble");

    if(!isRestUri && !plfRegisterService.isSoftwareRegistered()
            && !StringUtils.equals(notReacheble, "true") && checkRequest(requestSkip)
            && !plfRegisterService.isSkipPlatformRegistration()) {
      // Get full url
      String reqUri = httpServletRequest.getRequestURI().toString();
      String queryString = httpServletRequest.getQueryString();
      if (queryString != null) {
          reqUri =new StringBuffer(reqUri).append("?").append(queryString).toString();
      }
      ServletContext welcomrScreensContext = httpServletRequest.getSession().getServletContext().getContext(PLF_COMMUNITY_SERVLET_CTX);
      String uriTarget = (new StringBuilder()).append(SR_SERVLET_URL + "?" + INITIAL_URI_PARAM_NAME + "=").append(reqUri).toString();
      welcomrScreensContext.getRequestDispatcher(uriTarget).forward(httpServletRequest, httpServletResponse);
      return;
    }
    chain.doFilter(request, response);
  }
}
