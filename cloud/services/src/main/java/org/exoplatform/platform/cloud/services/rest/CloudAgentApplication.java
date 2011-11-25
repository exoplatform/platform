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
package org.exoplatform.platform.cloud.services.rest;

import org.exoplatform.cloudmanagement.mail.CloudMailService;
import org.exoplatform.cloudmanagement.rest.CloudAgentInfoService;
import org.exoplatform.cloudmanagement.rest.TenantService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class CloudAgentApplication extends Application
{

   @Override
   public Set<Class<?>> getClasses()
   {
      Set<Class<?>> cls = new HashSet<Class<?>>(3);
      cls.add(CloudMailService.class);
      cls.add(CloudAgentInfoService.class);
      cls.add(TenantService.class);
      cls.add(IntranetRESTOrganizationServiceImpl.class);
      return cls;
   }

}
