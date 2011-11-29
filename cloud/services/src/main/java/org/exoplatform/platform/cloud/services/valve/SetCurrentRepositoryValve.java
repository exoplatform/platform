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
package org.exoplatform.platform.cloud.services.valve;


import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.exoplatform.cloudmanagement.multitenancy.TenantNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;


/**
 * Valve for initializing current repository name.
 * 
 * @author <a href="mailto:mshaposhnik@exoplatform.com>Max Shaposhnik</a>   
 * @version $Id: SetCurrentRepositoryValve
 *
 */
public class SetCurrentRepositoryValve extends org.exoplatform.cloud.tomcat.SetCurrentRepositoryValve
{
   
   private static final Logger LOG = LoggerFactory.getLogger(SetCurrentRepositoryValve.class);

  @Override
   public void invoke(Request request, Response response) throws IOException, ServletException {
     
     String masterHost = System.getProperty(TenantNameResolver.MASTER_HOST_VARIABLE_NAME);
     URI requestUri;
      try
      {
         requestUri  = new URI(request.getRequestURL().toString());
      }
      catch (Exception ex)
      {
         LOG.warn("Cannot read request URI", ex);
         requestUri = null;
      }
         
      if (masterHost != null && masterHost.length()>0 && requestUri != null && requestUri.getHost().contains(masterHost)) 
      {
         super.invoke(request, response);
      }
      else
      {
         getNext().invoke(request, response);
      }
   }

}
