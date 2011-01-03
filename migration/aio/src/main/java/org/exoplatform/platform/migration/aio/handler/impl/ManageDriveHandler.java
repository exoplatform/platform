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

import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.ComponentPlugin;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.services.cms.drives.DriveData;
import org.exoplatform.services.cms.drives.ManageDriveService;
import org.exoplatform.services.cms.drives.impl.ManageDrivePlugin;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;

public class ManageDriveHandler extends ComponentHandler {

  private Log log = ExoLogger.getLogger(this.getClass());

  public ManageDriveHandler() {
    super.setTargetComponentName(ManageDriveService.class.getName());
  }

  @Override
  public Entry invoke(Component component, ExoContainer container) {
    Session dmsWorkspaceSession = null;
    try {
      List<ComponentPlugin> componentPluginsList = cleanComponentPlugins(component);

      ComponentPlugin templatesComponentPlugin = new ComponentPlugin();
      templatesComponentPlugin.setName("manage.drive.plugin");
      templatesComponentPlugin.setSetMethod("setManageDrivePlugin");
      templatesComponentPlugin.setType(ManageDrivePlugin.class.getName());

      InitParams templatesPluginInitParams = new InitParams();
      templatesComponentPlugin.setInitParams(templatesPluginInitParams);
      componentPluginsList.add(templatesComponentPlugin);

      RepositoryService repositoryService = ((RepositoryService) container.getComponentInstanceOfType(RepositoryService.class));
      ManageableRepository repository = repositoryService.getDefaultRepository();
      String defaumtRepositoryName = repository.getConfiguration().getName();

      ManageDriveService driveService = ((ManageDriveService) container.getComponentInstanceOfType(ManageDriveService.class));
      List<DriveData> driveDataList = driveService.getAllDrives(defaumtRepositoryName);
      for (DriveData driveData : driveDataList) {
        driveData.setRepository(defaumtRepositoryName);
        ObjectParameter objectParam = new ObjectParameter();
        objectParam.setName(driveData.getName());
        objectParam.setObject(driveData);
        templatesPluginInitParams.addParam(objectParam);
      }

      Configuration configuration = new Configuration();
      configuration.addComponent(component);

      Entry entry = new Entry(component.getKey() + ".xml");
      entry.setType(EntryType.XML);
      entry.setContent(toXML(configuration));
      return entry;
    } catch (Exception ie) {
      log.error("Error while invoking handler for component: " + component.getKey(), ie);
      return null;
    } finally {
      if (dmsWorkspaceSession != null)
        dmsWorkspaceSession.logout();
    }
  }

  @SuppressWarnings("unchecked")
  private List<ComponentPlugin> cleanComponentPlugins(Component component) {
    List<ComponentPlugin> componentPluginsList = component.getComponentPlugins();
    int i = 0;
    while (i < componentPluginsList.size()) {
      ComponentPlugin componentPlugin = componentPluginsList.get(i);
      if (componentPlugin.getType().equals(ManageDrivePlugin.class.getName())) {
        componentPluginsList.remove(i);
      } else {
        i++;
      }
    }
    return componentPluginsList;
  }

}
