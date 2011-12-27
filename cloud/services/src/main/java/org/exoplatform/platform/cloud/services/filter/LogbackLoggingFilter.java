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

import org.exoplatform.web.filter.Filter;
import org.exoplatform.container.web.AbstractFilter;
import org.exoplatform.services.security.ConversationState;
import org.slf4j.MDC;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:natasha.vakulenko@gmail.com">Natasha Vakulenko</a>
 * @version $Id$
 */
public class LogbackLoggingFilter extends AbstractFilter implements Filter
{

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      try
      {
         if (ConversationState.getCurrent() != null)
         {
            if (ConversationState.getCurrent().getAttribute("currentTenant") != null)
            {
               MDC.put("currentTenant", (String)ConversationState.getCurrent().getAttribute("currentTenant"));
            }
         }
         chain.doFilter(request, response);
      }
      finally
      {
         if (ConversationState.getCurrent() != null)
         {
            if (ConversationState.getCurrent().getAttribute("currentTenant") != null)
            {
               MDC.remove("currentTenant");
            }
         }
      }
   }

   public void destroy()
   {
   }
} 