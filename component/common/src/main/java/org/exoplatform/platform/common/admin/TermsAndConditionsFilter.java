package org.exoplatform.platform.common.admin;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.filter.Filter;

/**
 * Filter responsible of Terms and conditions displaying.
 * Call T&C service to know if T&C are checked, if not, forward to T&C page
 * 
 * @author Clement
 *
 */
public class TermsAndConditionsFilter implements Filter {
  
  private TermsAndConditionsService termsAndConditionsService;

  private static final String PLF_EXTENSION_SERVLET_CTX = "/platform-extension";
  private static final String TC_SERVLET_URL = "/terms-and-conditions";
  private static final String INITIAL_URI_PARAM_NAME = "initialURI";

  public TermsAndConditionsFilter() {}

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
    
    if(! tcChecked) {
      // Get full url
      String reqUri = httpServletRequest.getRequestURI().toString();
      String queryString = httpServletRequest.getQueryString();
      if (queryString != null) {
          reqUri += "?"+queryString;
      }
      
      // Get plf extension servlet context (because TermsAndConditionsFilter and terms-and-conditions servlet declaration are not on same context (webapp))
      ServletContext plfExtensionContext = httpServletRequest.getSession().getServletContext().getContext(PLF_EXTENSION_SERVLET_CTX);
      // Forward to resource from this context: 
      String uriTarget = (new StringBuilder()).append(TC_SERVLET_URL + "?" + INITIAL_URI_PARAM_NAME + "=").append(reqUri).toString();
      plfExtensionContext.getRequestDispatcher(uriTarget).forward(httpServletRequest, httpServletResponse);
      
    }
    chain.doFilter(request, response);
  }
}
