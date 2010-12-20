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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.xml.Component;
import org.exoplatform.container.xml.Configuration;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.migration.common.handler.ComponentHandler;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.application.PortletPreferences.PortletPreferencesSet;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Page.PageSet;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform haikel.thamri@exoplatform.com 15 juil. 2010
 */
public class UserPortalConfigHandler extends ComponentHandler {
  final private static String PORTAL_FILE_NAME = "portal.xml";

  final private static String PAGES_FILE_NAME = "pages.xml";

  final private static String NAVIGATION_FILE_NAME = "navigation.xml";

  final private static String GADGET_FILE_NAME = "gadgets.xml";

  final private static String PORTLET_PREFERENCES_FILE_NAME = "portlet-preferences.xml";

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
      List<PageNavigation> findedPageNavigations = (List<PageNavigation>)dataStorage.find(pageNavigationQuery).getAll();
      for (PageNavigation pageNavigation : findedPageNavigations) {
        String ownerType = pageNavigation.getOwnerType();
        String ownerId = pageNavigation.getOwnerId();
        String portalConfigForlder = ownerType + "/" + ownerId + "/";
        if (PortalConfig.PORTAL_TYPE.equals(ownerType)) {
          zos.putNextEntry(new ZipEntry(portalConfigForlder + PORTAL_FILE_NAME));
          PortalConfig portalConfig = dataStorage.getPortalConfig(ownerId);
          byte[] bytes = toXML(portalConfig);
          zos.write(bytes);
          zos.closeEntry();
        }
        {/* Pages marshalling */
          zos.putNextEntry(new ZipEntry(portalConfigForlder + PAGES_FILE_NAME));
          Query<Page> portalConfigQuery = new Query<Page>(ownerType, ownerId, Page.class);
          List<Page> findedPages = dataStorage.find(portalConfigQuery).getAll();
          PageSet pageSet = new PageSet();
          pageSet.setPages((ArrayList<Page>) findedPages);
          byte[] bytes = toXML(pageSet);
          zos.write(bytes);
          zos.closeEntry();
        }
        {/* Navigation marshalling */
          zos.putNextEntry(new ZipEntry(portalConfigForlder + NAVIGATION_FILE_NAME));
          byte[] bytes = toXML(pageNavigation);
          zos.write(bytes);
          zos.closeEntry();
        }
        {/* PortletPreferences marshalling */
          zos.putNextEntry(new ZipEntry(portalConfigForlder + PORTLET_PREFERENCES_FILE_NAME));
          Query<PortletPreferences> portletPreferencesQuery = new Query<PortletPreferences>(ownerType, ownerId, PortletPreferences.class);
          List<PortletPreferences> findedPortletPreferences = dataStorage.find(portletPreferencesQuery).getAll();
          PortletPreferencesSet portletPreferencesSet = new PortletPreferencesSet();
          portletPreferencesSet.setPortlets((ArrayList<PortletPreferences>) findedPortletPreferences);
          byte[] bytes = toXML(portletPreferencesSet);
          zos.write(bytes);
          zos.closeEntry();
        }
//        {/* Gadgets marshalling */
//          Gadgets gadgets = dataStorage.getGadgets(ownerType + "::" + ownerId);
//          if (gadgets != null && gadgets.getChildren() != null && gadgets.getChildren().size() > 0) {
//            zos.putNextEntry(new ZipEntry(portalConfigForlder + GADGET_FILE_NAME));
//            byte[] bytes = toXML(gadgets);
//            zos.write(bytes);
//            zos.closeEntry();
//          }
//        }
      }
    } catch (Exception ie) {
      throw ie;
    }
  }
}
