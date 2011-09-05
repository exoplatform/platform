/*
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
package org.exoplatform.platform.cloud.services.filter;

import org.exoplatform.cloudmanagement.multitenancy.TenantNameResolver;
import org.exoplatform.web.filter.Filter;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.security.ConversationState;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id$
 */
public class SetTenantRepositoryFilter extends AbstractFilter implements Filter
{

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      try
      {
         HttpServletRequest httpRequest = (HttpServletRequest)request;
         String requestUrl = httpRequest.getRequestURL().toString();
         String tenant = TenantNameResolver.getTenantName(requestUrl);
         if (tenant != null)
         {
            if (ConversationState.getCurrent() != null)
            {
               ConversationState.getCurrent().setAttribute("currentTenant", tenant);
            }
         }
         doFilterInternal(request, response, chain);
      }

      finally
      {
         if (ConversationState.getCurrent() != null)
         {
            if (ConversationState.getCurrent().getAttribute("currentTenant") != null)
            {
               ConversationState.getCurrent().removeAttribute("currentTenant");
            }
         }
      }
   }

   /**
    * 
    * @param request
    * @param response
    * @param chain
    * @throws IOException
    * @throws ServletException
    */
   private void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException
   {
      chain.doFilter(request, response);
   }

   public void destroy()
   {
   }
} 