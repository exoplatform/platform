package org.exoplatform.platform.common.software.register.web;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.software.register.UnlockService;
import org.exoplatform.web.filter.Filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UnlockFilter implements Filter {
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    boolean isIgnoringRequest = isIgnoredRequest(httpServletRequest.getSession(true).getServletContext(),
                                                 httpServletRequest.getRequestURI());
    if (!isIgnoringRequest) {
      UnlockService unlockService = PortalContainer.getInstance().getComponentInstanceOfType(UnlockService.class);
      unlockService.setCalledUrl(httpServletRequest.getRequestURI());
    }
    chain.doFilter(request, response);
  }

  private boolean isIgnoredRequest(ServletContext context, String url) {
    String fileName = url.substring(url.indexOf("/"));
    String mimeType = context.getMimeType(fileName);
    return (mimeType != null || url.contains(CommonsUtils.getRestContextName()));
  }
}
