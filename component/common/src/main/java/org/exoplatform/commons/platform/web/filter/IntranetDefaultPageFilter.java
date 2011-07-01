package org.exoplatform.commons.platform.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.filter.Filter;

public class IntranetDefaultPageFilter implements Filter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
    }
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    if (!httpServletRequest.getRequestURI().contains("/portal/public/intranet")
        || httpServletRequest.getRequestURI().contains("/portal/public/intranet/welcome")) {
      chain.doFilter(request, response);
    }
    if (httpServletRequest.getRemoteUser() != null) {
      chain.doFilter(request, response);
    } else {
      ((HttpServletResponse) response).sendRedirect("/portal/public/intranet/welcome");
    }
  }
}
