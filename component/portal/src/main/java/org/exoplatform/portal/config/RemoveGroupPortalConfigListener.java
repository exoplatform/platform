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

import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.jdbc.GroupDAOImpl;

/**
 * Created by The eXo Platform SARL
 * Author : Tung.Pham
 *          tung.pham@exoplatform.com
 * Jul 31, 2007  
 */
public class RemoveGroupPortalConfigListener extends Listener<GroupDAOImpl, Group> {

  @Override
  public void onEvent(Event<GroupDAOImpl, Group> event) throws Exception {
    Group group = event.getData() ;
    ExoContainer container  = ExoContainerContext.getCurrentContainer();
    UserPortalConfigService portalConfigService = 
      (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
    DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String groupId = group.getId().substring(1) ;
    Query<Page> pageQuery = new Query<Page>(PortalConfig.GROUP_TYPE, groupId,  Page.class) ;
    LazyPageList pageList = dataStorage.find(pageQuery) ;
    int i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> iterator = list.iterator() ;
      while(iterator.hasNext()) portalConfigService.remove((Page)iterator.next()) ;
      i++ ;
    }

    Query<PortletPreferences> portletPrefQuery = 
      new Query<PortletPreferences>(PortalConfig.GROUP_TYPE, groupId, PortletPreferences.class) ;
    pageList = dataStorage.find(portletPrefQuery) ;
    i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> iterator = list.iterator() ;
      while(iterator.hasNext()) dataStorage.remove((PortletPreferences)iterator.next()) ;
      i++ ;
    }
    
    PageNavigation navigation = dataStorage.getPageNavigation(PortalConfig.GROUP_TYPE, groupId) ;
    if (navigation != null) portalConfigService.remove(navigation) ;
  }

}
