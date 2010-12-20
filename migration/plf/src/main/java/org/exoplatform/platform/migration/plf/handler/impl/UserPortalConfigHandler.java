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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.commons.utils.LazyList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.migration.common.constants.Constants;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;

public class UserPortalConfigHandler extends ComponentHandler {

  public UserPortalConfigHandler(InitParams initParams) {
    super.setTargetComponentName(UserPortalConfigService.class.getName());
  }

  public Entry invoke(Component component, ExoContainer container) throws Exception {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(out);

      writePortalConfigs(component, zos, container);

      Configuration configuration = new Configuration();
      configuration.addComponent(component);
      zos.putNextEntry(new ZipEntry(component.getKey() + ".xml"));
      zos.write(toXML(configuration));
      zos.closeEntry();
      zos.close();

      Entry entry = new Entry(component.getKey());
      entry.setType(EntryType.ZIP);
      entry.setContent(out.toByteArray());
      return entry;
    } catch (Exception ie) {
      throw ie;
    }
  }

  private void writePortalConfigs(Component component, ZipOutputStream zos, ExoContainer container) throws Exception {
    try {
      DataStorage dataStorage = (DataStorage) container.getComponentInstanceOfType(DataStorage.class);
      Query<PageNavigation> pageNavigationQuery = new Query<PageNavigation>(null, null, PageNavigation.class);
      LazyList<PageNavigation> findedPageNavigations = (LazyList<PageNavigation>) dataStorage.find(pageNavigationQuery).getAll();
      for (PageNavigation pageNavigation : findedPageNavigations) {
        String ownerType = pageNavigation.getOwnerType();
        String ownerId = pageNavigation.getOwnerId();
        String portalConfigForlder = ownerType + "/" + ownerId + "/";
        if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
          zos.putNextEntry(new ZipEntry(portalConfigForlder + Constants.PORTAL_FILE_NAME));
          PortalConfig portalConfig = dataStorage.getPortalConfig(ownerId);
          byte[] bytes = toXML(portalConfig);
          zos.write(bytes);
          zos.closeEntry();
        }
        {/* Pages marshalling */
          zos.putNextEntry(new ZipEntry(portalConfigForlder + Constants.PAGES_FILE_NAME));
          Query<Page> portalConfigQuery = new Query<Page>(ownerType, ownerId, Page.class);
          LazyList<Page> findedPages = (LazyList<Page>) dataStorage.find(portalConfigQuery).getAll();
          PageSet pageSet = new PageSet();
          pageSet.setPages(new ArrayList<Page>(findedPages));
          // Marshalling of Application, is unsupported
          byte[] bytes = toXML(pageSet);
          zos.write(bytes);
          zos.closeEntry();
        }
        {/* Navigation marshalling */
          zos.putNextEntry(new ZipEntry(portalConfigForlder + Constants.NAVIGATION_FILE_NAME));
          byte[] bytes = toXML(pageNavigation);
          zos.write(bytes);
          zos.closeEntry();
        }
      }
    } catch (Exception ie) {
      throw ie;
    }
  }
}
