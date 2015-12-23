package org.exoplatform.platform.common.software.register.web;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.PropertyManager;
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
 * Filter platform registration screen displaying.
 * <p>
 * Conditions to forward to platform registration page:
 * <ul>
 * <li>eXo Community is not reachable</li>
 * <li>User can skip registration</li>
 * <li>User is not register local PLF with community</li>
 * </ul>
 *
 * @author ToanNH
 *
 */
public class SoftwareRegisterFilter implements Filter {

  public static final String NOT_REACHABLE = "NOT_REACHABLE";
  private static final Log logger = ExoLogger.getLogger(SoftwareRegisterFilter.class);
  private static final String PLF_COMMUNITY_SERVLET_CTX = "/registration";
  private static final String SR_SERVLET_URL = "/software-register";
  private static final String INITIAL_URI_PARAM_NAME = "initialURI";
  private static String REST_URI;
  private SoftwareRegistrationService plfRegisterService;

  public SoftwareRegisterFilter() {
    REST_URI = ExoContainerContext.getCurrentContainer().getContext().getRestContextName();
  }

  private boolean checkRequest(boolean requestSkip){
    plfRegisterService = PortalContainer.getInstance().getComponentInstanceOfType(SoftwareRegistrationService.class);
    if(!requestSkip) return true;
    if(plfRegisterService.canSkipRegister() || (UnlockService.isUnlocked())){
      return false;
    }
    return !plfRegisterService.canSkipRegister();
  }
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    HttpServletResponse httpServletResponse = (HttpServletResponse)response;
    plfRegisterService = PortalContainer.getInstance().getComponentInstanceOfType(SoftwareRegistrationService.class);
    String requestUri = httpServletRequest.getRequestURI();
    boolean isRestUri = (requestUri.contains(REST_URI));
    boolean requestSkip = plfRegisterService.isRequestSkip();
    String notReachable = (String)httpServletRequest.getSession().getAttribute("notReachable");
    boolean isDevMod = PropertyManager.isDevelopping();
    if(notReachable==null) {
      notReachable = httpServletRequest.getQueryString();
      if (StringUtils.equals(notReachable, this.NOT_REACHABLE)) {
        notReachable="true";
        httpServletRequest.getSession().setAttribute("notReachable", notReachable);
      }
    }
    if(!isRestUri && !plfRegisterService.isSoftwareRegistered() && !isDevMod
            && !StringUtils.equals(notReachable, "true") && checkRequest(requestSkip)
            && !plfRegisterService.isSkipPlatformRegistration()) {
      // Get full url
      String reqUri = httpServletRequest.getRequestURI().toString();
      String queryString = httpServletRequest.getQueryString();
      if (queryString != null) {
          reqUri =new StringBuffer(reqUri).append("?").append(queryString).toString();
      }
      ServletContext platformRegisterContext = httpServletRequest.getSession().getServletContext().getContext(PLF_COMMUNITY_SERVLET_CTX);
      String uriTarget = (new StringBuilder()).append(SR_SERVLET_URL).append("?").append(INITIAL_URI_PARAM_NAME)
          .append("=").append(reqUri).toString();
      platformRegisterContext.getRequestDispatcher(uriTarget).forward(httpServletRequest, httpServletResponse);
      return;
    }
    chain.doFilter(request, response);
  }
}
