/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
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
/**
 * Fri, May 30, 2003 @
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: PublicRequestFilter.java,v 1.28 2004/11/03 01:19:46 tuan08 Exp $
 */
public class ResourceRequestFilter implements Filter  {
 
  private boolean cacheResource_ = false ;
  
  @SuppressWarnings("unused")
  public void init(FilterConfig filterConfig) {
    cacheResource_ =  !"true".equals(System.getProperty("exo.product.developing")) ;
    System.out.println("===> CACHE EXO RESOURCE AT CLIENT = " + cacheResource_ ) ;
 
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if(cacheResource_) {
      HttpServletResponse httpResponse = (HttpServletResponse)  response ;
      httpResponse.addHeader("Cache-Control", "max-age=2592000,s-maxage=2592000") ;
    } else {
      HttpServletRequest httpRequest = (HttpServletRequest) request ;
      String uri = httpRequest.getRequestURI();
      if(uri.endsWith(".jstmpl") || uri.endsWith(".css")) {
        HttpServletResponse httpResponse = (HttpServletResponse)  response ;
        httpResponse.setHeader("Cache-Control", "no-cache");
      }
      System.out.println("  Load Resource: " + uri);
    }
    chain.doFilter(request, response) ;
  }

  public void destroy() { }
}  