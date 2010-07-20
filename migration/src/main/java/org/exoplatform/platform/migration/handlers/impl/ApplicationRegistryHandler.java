/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.platform.migration.handlers.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.migration.handlers.ComponentHandler;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.OrganizationService;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform
 * haikel.thamri@exoplatform.com 19 juil. 2010
 */
public class ApplicationRegistryHandler implements ComponentHandler {

  private PortalContainer            portalContainer;

  private ApplicationRegistryService applicationRegistryService;
  
  private Log                 log                         = ExoLogger.getLogger(this.getClass());


  public void invoke(Component component, String rootConfDir) {
    try {
      portalContainer = PortalContainer.getInstance();
      applicationRegistryService = (ApplicationRegistryService) portalContainer.getComponentInstanceOfType(ApplicationRegistryService.class);
      preMarshallComponent(component,rootConfDir);
      Configuration configuration = new Configuration();
      configuration.addComponent(component);
      marshall(configuration, rootConfDir + File.separator + "portal" + File.separator
               + component.getKey());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void preMarshallComponent(Component component, String rootConfDir) {
    try {
      List<ComponentPlugin> componentPlugins = component.getComponentPlugins();
      for (ComponentPlugin componentPlugin : componentPlugins) {
        if (componentPlugin.getType()
                           .equals("org.exoplatform.application.registry.ApplicationCategoriesPlugins")) {
          componentPlugin.getInitParams().clear();
          List<ApplicationCategory> applicationCategories = applicationRegistryService.getApplicationCategories();
          for (ApplicationCategory applicationCategory : applicationCategories) {
            ObjectParameter objectParameter = new ObjectParameter();
            objectParameter.setDescription(applicationCategory.getDescription());
            objectParameter.setName(applicationCategory.getName());
            objectParameter.setObject(applicationCategory);
            componentPlugin.getInitParams().addParameter(objectParameter);
            if(applicationCategory.getApplications()!=null){
              System.out.println("rrrr");

            System.out.println(applicationCategory.getApplications().size());
            }
          }
        }
      }

    } catch (Exception ie) {
      log.error("problem in the preMarshall Process", ie);
      }
  }

  private void marshall(Object obj, String xmlPath) {
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(obj.getClass());
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      mctx.marshalDocument(obj, "UTF-8", null, new FileOutputStream(xmlPath));
    } catch (Exception ie) {
      log.error("Cannot convert the object to xml", ie);
    }
  }

}
