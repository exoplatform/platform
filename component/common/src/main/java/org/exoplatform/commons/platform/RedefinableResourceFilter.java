/**
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.commons.platform;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.web.filter.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This filter is used to access to a resource 
 * <p>
 * Created by The eXo Platform SAS<br/>
 * Author : Nicolas Filotto <br/>
 *          nicolas.filotto@exoplatform.com<br/>
 * 18 juin 2010  
 * </p>
 */
public class RedefinableResourceFilter implements Filter {

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                           ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    // Comment the line below if the Filter "GenericFilter" is called after the
    // filter "SetCurrentIdentityFilter" which is not the
    // case in GateIn 3.0 according to the web.xml
    PortalContainer pContainer = PortalContainer.getInstance(req.getSession().getServletContext());
    // Uncomment the line below if the Filter "GenericFilter" is called after
    // the filter "SetCurrentIdentityFilter"
    // PortalContainer pContainer = PortalContainer.getInstance();
    ServletContext context = pContainer.getPortalContext();
    String path = req.getRequestURI();
    String ctx = req.getContextPath();
    if (ctx != null && ctx.length() > 1 && path.startsWith(ctx)) {
      path = path.substring(ctx.length());
    }
    context.getRequestDispatcher(path).include(request, response);
  }

}
