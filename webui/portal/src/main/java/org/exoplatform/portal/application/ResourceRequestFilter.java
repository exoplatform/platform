/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

public class ResourceRequestFilter implements Filter  {
  
  protected static Log log = ExoLogger.getLogger("portal:ResourceRequestFilter");
 
  private boolean cacheResource_ = false ;
  
  @SuppressWarnings("unused")
  public void init(FilterConfig filterConfig) {
    cacheResource_ =  !"true".equals(System.getProperty("exo.product.developing")) ;
    log.info("Cache eXo Resource at client: " + cacheResource_);
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if(cacheResource_) {
      HttpServletResponse httpResponse = (HttpServletResponse)  response ;
      httpResponse.addHeader("Cache-Control", "max-age=2592000,s-maxage=2592000") ;
    } else {
      HttpServletRequest httpRequest = (HttpServletRequest) request ;
      String uri = httpRequest.getRequestURI();
      if(uri.endsWith(".jstmpl") || uri.endsWith(".css") || uri.endsWith(".js")) {
        HttpServletResponse httpResponse = (HttpServletResponse)  response ;
        httpResponse.setHeader("Cache-Control", "no-cache");
      }
      if(log.isDebugEnabled())
        log.debug(" Load Resource: " + uri);
    }
    chain.doFilter(request, response) ;
  }

  public void destroy() { }
}  