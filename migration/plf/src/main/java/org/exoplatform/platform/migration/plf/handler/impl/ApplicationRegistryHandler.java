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
package org.exoplatform.platform.migration.plf.handler.impl;

import java.util.List;

import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.portal.config.model.ApplicationType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ApplicationRegistryHandler extends ComponentHandler {
  
  private Log log = ExoLogger.getLogger(this.getClass());

  public ApplicationRegistryHandler(InitParams initParams) {
    super.setTargetComponentName(ApplicationRegistryService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    try{
      preMarshallComponent(component, container);
      Configuration configuration = new Configuration();
      configuration.addComponent(component);
      byte[] bytes = toXML(configuration);
      Entry entry = new Entry(component.getKey());
      entry.setType(EntryType.XML);
      entry.setContent(bytes);
      return entry;
    }catch (Exception e) {
      log.error("Error while invoking handler for component: " + component.getKey(), e);
      return null;
    }
  }

  private void preMarshallComponent(Component component, ExoContainer container) throws Exception {
    ApplicationRegistryService applicationRegistryService = (ApplicationRegistryService) container.getComponentInstanceOfType(ApplicationRegistryService.class);
    if(log.isDebugEnabled()){
      log.debug("Handler invoked for component: " + component.getKey() + " of type: " + applicationRegistryService.getClass().getName());
    }
    List<ComponentPlugin> componentPlugins = component.getComponentPlugins();
    for (ComponentPlugin componentPlugin : componentPlugins) {
      if (componentPlugin.getType().equals("org.exoplatform.application.registry.ApplicationCategoriesPlugins")) {
        componentPlugin.getInitParams().clear();
        List<ApplicationCategory> applicationCategories = applicationRegistryService.getApplicationCategories();
        for (ApplicationCategory applicationCategory : applicationCategories) {
          ObjectParameter objectParameter = new ObjectParameter();
          ApplicationType<?>[] appTypes = { ApplicationType.PORTLET, ApplicationType.GADGET };
          applicationCategory.setApplications(applicationRegistryService.getApplications(applicationCategory, appTypes));
          objectParameter.setDescription(applicationCategory.getDescription());
          objectParameter.setName(applicationCategory.getName());
          objectParameter.setObject(applicationCategory);
          componentPlugin.getInitParams().addParameter(objectParameter);
        }
      }
    }
  }
}
