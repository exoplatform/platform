package org.exoplatform.commons.platform.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IntranetDefaultPageFilter implements Filter, org.exoplatform.web.filter.Filter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    if (httpServletRequest.getRemoteUser() != null) {
      chain.doFilter(request, response);
      return;
    }
    if (!httpServletRequest.getRequestURI().contains("/portal/public/intranet")
        || httpServletRequest.getRequestURI().contains("/portal/public/intranet/welcome") || httpServletRequest.getRequestURI().contains("/portal/public/intranet/Register")) {
      chain.doFilter(request, response);
      return;
    } else {
      ((HttpServletResponse) response).sendRedirect("/portal/public/intranet/welcome");
    }
  }

  public void destroy() {}

  public void init(FilterConfig arg0) throws ServletException {}
}
