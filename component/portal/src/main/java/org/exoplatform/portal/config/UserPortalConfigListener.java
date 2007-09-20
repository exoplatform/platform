/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortletPreferences;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * Created by The eXo Platform SAS
 * May 29, 2007  
 */
public class UserPortalConfigListener extends UserEventListener {
  
  public void preDelete(User user) throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    UserPortalConfigService portalConfigService = 
      (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
    DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String userName = user.getUserName() ;
    
    Query<Page> query = new Query<Page>(PortalConfig.USER_TYPE, userName, Page.class) ;
    PageList pageList = dataStorage.find(query) ;
    pageList.setPageSize(10) ;
    int i =  1;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> iterator = list.iterator() ;
      while(iterator.hasNext()) portalConfigService.remove((Page) iterator.next()) ;
      i++;
    }
    
    Query<PortletPreferences> portletPrefQuery = 
      new Query<PortletPreferences>(PortalConfig.USER_TYPE, userName, PortletPreferences.class) ;
    pageList = dataStorage.find(portletPrefQuery) ;
    i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> iterator = list.iterator() ;
      while(iterator.hasNext()) dataStorage.remove((PortletPreferences)iterator.next()) ;
      i++ ;
    }
   
    String id = PortalConfig.USER_TYPE + "::" + userName ;
    PageNavigation navigation = dataStorage.getPageNavigation(id) ;
    if (navigation != null) portalConfigService.remove(navigation) ;

    Widgets widgets = dataStorage.getWidgets(id) ;
    if (widgets != null) portalConfigService.remove(widgets);
  }
  
  public void preSave(User user, boolean isNew) throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    UserPortalConfigService portalConfigService = 
      (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
    DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String userName = user.getUserName() ;
    String id = PortalConfig.USER_TYPE + "::" + userName ;
    PageNavigation navigation = dataStorage.getPageNavigation(id) ;
    if (navigation != null) return;
    PageNavigation pageNav = new PageNavigation();
    pageNav.setOwnerType(PortalConfig.USER_TYPE);
    pageNav.setOwnerId(userName);
    pageNav.setPriority(5);
    pageNav.setNodes(new ArrayList<PageNode>());
    portalConfigService.create(pageNav);
  }
}
