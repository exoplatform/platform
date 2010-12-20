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
package org.exoplatform.platform.migration.aio.handler.impl;

import java.util.List;

import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;

public class ApplicationRegistryHandler extends ComponentHandler {

  public ApplicationRegistryHandler(InitParams initParams) {
    super.setTargetComponentName(ApplicationRegistryService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) throws Exception {
    preMarshallComponent(component, container);
    Configuration configuration = new Configuration();
    configuration.addComponent(component);
    byte[] bytes = toXML(configuration);
    Entry entry = new Entry(component.getKey());
    entry.setType(EntryType.XML);
    entry.setContent(bytes);
    return entry;
  }

  private void preMarshallComponent(Component component, ExoContainer container) throws Exception {
    ApplicationRegistryService applicationRegistryService = (ApplicationRegistryService) container.getComponentInstanceOfType(ApplicationRegistryService.class);
    List<ComponentPlugin> componentPlugins = component.getComponentPlugins();
    for (ComponentPlugin componentPlugin : componentPlugins) {
      if (componentPlugin.getType().equals("org.exoplatform.application.registry.ApplicationCategoriesPlugins")) {
        componentPlugin.getInitParams().clear();
        List<ApplicationCategory> applicationCategories = applicationRegistryService.getApplicationCategories();
        for (ApplicationCategory applicationCategory : applicationCategories) {
          ObjectParameter objectParameter = new ObjectParameter();
          String[] appTypes = { Constants.APP_TYPE_PORTLET, Constants.APP_TYPE_GADGET };
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
