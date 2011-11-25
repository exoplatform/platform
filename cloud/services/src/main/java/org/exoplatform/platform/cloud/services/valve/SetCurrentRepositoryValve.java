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

  @Override
   public void invoke(Request request, Response response) throws IOException, ServletException {
      
      try
      {
         String masterHost = System.getProperty(TenantNameResolver.MASTER_HOST_VARIABLE_NAME);
         URI uri = new URI(request.getRequestURL().toString());
         if (uri.getHost().contains(masterHost))
            super.invoke(request, response);
         else
            getNext().invoke(request, response);
      }
      catch (Exception ex)
      {
         getNext().invoke(request, response);
      }

   }

}
