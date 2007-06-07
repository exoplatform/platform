/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.portlet.PortletPreferences;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupEventListener;

import com.sun.mail.util.QEncoderStream;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 29, 2007  
 */
public class GroupPortalConfigListener extends GroupEventListener {
  
//  private DataStorage dataStorage_;  
  
  public GroupPortalConfigListener() throws Exception {
//    dataStorage_ = dataStorage; 
  }

  //TODO: Tung.Pham implement
  public void preDelete(Group group) throws Exception {
    //System.out.println("\n\n == > prepare remove group "+group.getId()+"\n\n");
    // user data Storage get navigation and page then remove it
    PortalContainer container  = PortalContainer.getInstance() ;
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String ownerId = group.getId() ;
    
    //Delete Pages
    Query<Page> pageQuery = new Query<Page>(null, null, null, Page.class) ;
    pageQuery.setOwnerType(PortalConfig.GROUP_TYPE) ;
    pageQuery.setOwnerId(ownerId) ;
    PageList pageList = dataService.find(pageQuery) ;
    int i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        Page page = (Page)itr.next() ;
        System.out.println("\n\n\n\npage: " + page.getPageId());
        dataService.remove(page) ;
      }
      i ++ ;
    }
//    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
//    query.setOwnerType(PortalConfig.GROUP_TYPE) ;
//    query.setOwnerId(ownerId) ;
//    PageList pageList = dataService.find(query) ;
//    for (Object page : pageList.getAll()) {
//     dataService.remove(((Page)page)) ; 
//    }
    
    
    //Delete Navigation
    PageNavigation navigation = dataService.getPageNavigation(PortalConfig.GROUP_TYPE + "::" + ownerId) ;
    if (navigation != null) dataService.remove(navigation) ;
    
    //Delete PortletPreferences
    Query<PortletPreferences> portletPrefQuery = new Query<PortletPreferences>(null, null, null, PortletPreferences.class) ;
    portletPrefQuery.setOwnerType(PortalConfig.GROUP_TYPE) ;
    portletPrefQuery.setOwnerId(ownerId) ;
    pageList = dataService.find(portletPrefQuery) ;
    int j = 1 ;
    while(j <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(j) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        PortletPreferences portletPref = (PortletPreferences)itr.next() ;
        System.out.println("\n\n\n\n\nportlet: " + portletPref.getWindowId());
        dataService.remove(portletPref) ;
      }
      j ++ ;
    }
  }
}
