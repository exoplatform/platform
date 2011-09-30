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
