/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
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
    PortalContainer container  = PortalContainer.getInstance() ;
    UserPortalConfigService portalConfigService = 
      (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
    DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String groupId = group.getId().substring(1) ;
    Query<Page> pageQuery = new Query<Page>(PortalConfig.GROUP_TYPE, groupId,  Page.class) ;
    PageList pageList = dataStorage.find(pageQuery) ;
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
    
    PageNavigation navigation = dataStorage.getPageNavigation(PortalConfig.GROUP_TYPE + "::" + groupId) ;
    if (navigation != null) portalConfigService.remove(navigation) ;
  }

}
