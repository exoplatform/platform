/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * Created by The eXo Platform SAS May 29, 2007
 */
public class UserPortalConfigListener extends UserEventListener {

  public void preDelete(User user) throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    UserPortalConfigService portalConfigService = (UserPortalConfigService) container.getComponentInstanceOfType(UserPortalConfigService.class);
    DataStorage dataStorage = (DataStorage) container.getComponentInstanceOfType(DataStorage.class);
    String userName = user.getUserName();

    Query<Page> query = new Query<Page>(PortalConfig.USER_TYPE, userName, Page.class);
    PageList pageList = dataStorage.find(query);
    pageList.setPageSize(10);
    int i = 1;
    while (i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i);
      Iterator<?> iterator = list.iterator();
      while (iterator.hasNext())
        portalConfigService.remove((Page) iterator.next());
      i++;
    }

    Query<PortletPreferences> portletPrefQuery = new Query<PortletPreferences>(PortalConfig.USER_TYPE,
                                                                               userName,
                                                                               PortletPreferences.class);
    pageList = dataStorage.find(portletPrefQuery);
    i = 1;
    while (i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i);
      Iterator<?> iterator = list.iterator();
      while (iterator.hasNext())
        dataStorage.remove((PortletPreferences) iterator.next());
      i++;
    }

    PageNavigation navigation = dataStorage.getPageNavigation(PortalConfig.USER_TYPE, userName);
    if (navigation != null)
      portalConfigService.remove(navigation);
  }

  public void preSave(User user, boolean isNew) throws Exception {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    /*
     * TODO Call start method on RegistryService to allow ecm, ultimate can run
     * with JDK6. This is uncommon behavior. We need find other way to fix it I
     * hope that this issues will be fixed when we use the lastest version of
     * PicoContainer Comment by Hoa Pham.
     */
    RegistryService registryService = (RegistryService) container.getComponentInstanceOfType(RegistryService.class);
    registryService.start();
    UserPortalConfigService portalConfigService = (UserPortalConfigService) container.getComponentInstanceOfType(UserPortalConfigService.class);
    DataStorage dataStorage = (DataStorage) container.getComponentInstanceOfType(DataStorage.class);
    String userName = user.getUserName();
    PageNavigation navigation = dataStorage.getPageNavigation(PortalConfig.USER_TYPE, userName);
    if (navigation != null)
      return;
    PageNavigation pageNav = new PageNavigation();
    pageNav.setOwnerType(PortalConfig.USER_TYPE);
    pageNav.setOwnerId(userName);
    pageNav.setPriority(5);
    pageNav.setNodes(new ArrayList<PageNode>());
    portalConfigService.create(pageNav);
  }
}
