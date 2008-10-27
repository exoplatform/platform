/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.application;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.webui.skin.SkinService;
import org.exoplatform.services.log.ExoLogger;

public class ResourceRequestFilter implements Filter  {
  
  protected static Log log = ExoLogger.getLogger("portal:ResourceRequestFilter");
 
  private boolean isDeveloping_ = false ;
  
  public void init(FilterConfig filterConfig) {
    isDeveloping_ = "true".equals(System.getProperty("exo.product.developing")) ;
    log.info("Cache eXo Resource at client: " + !isDeveloping_);
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request ;
    String uri = URLDecoder.decode(httpRequest.getRequestURI(),"UTF-8");
    HttpServletResponse httpResponse = (HttpServletResponse)  response ;
    if(isDeveloping_ && (uri.endsWith(".jstmpl") || uri.endsWith(".css") || uri.endsWith(".js"))) {
      httpResponse.setHeader("Cache-Control", "no-cache");
    } else if(uri.endsWith(".css")) {
    	ExoContainer portalContainer = ExoContainerContext.getCurrentContainer();
    	SkinService skinService = (SkinService) portalContainer.getComponentInstanceOfType(SkinService.class);
    	String mergedCSS = skinService.getMergedCSS(uri);
    	if(mergedCSS != null) {
    		log.info("Use a merged CSS: " + uri);
    		response.getWriter().print(mergedCSS);
    		return;
    	}
    }
    chain.doFilter(request, response) ;
  }

  public void destroy() { }
}  