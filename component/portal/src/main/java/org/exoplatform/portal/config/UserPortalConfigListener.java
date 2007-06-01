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
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 29, 2007  
 */
public class UserPortalConfigListener extends UserEventListener {
  
  public UserPortalConfigListener() throws Exception {
  }

  public void preDelete(User user) throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    //TODO: Tung.Pham modified
    //----------------------------------------------------------------------------------------------------
    //DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    //System.out.println("\n\n == > prepare remove user "+dataStorage+" : "+user.getUserName()+"\n\n");
    // user data Storage get navigation and page then remove it
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String userName = user.getUserName() ;
    
    //Delete pages
    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
    query.setOwnerType(PortalConfig.USER_TYPE) ;
    query.setOwnerId(userName) ;
    PageList pageList = dataService.find(query) ;
    pageList.setPageSize(10) ;
    int i =  1;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        Page page = (Page) itr.next() ;
        dataService.remove(page) ;
      }
      
      i++;
    }
   
    //Delete Navigation
    String id = PortalConfig.USER_TYPE + "::" + userName ;
    PageNavigation navigation = dataService.getPageNavigation(id) ;
    if (navigation != null) dataService.remove(navigation) ;

    //Delete Widgets
    Widgets widgets = dataService.getWidgets(id) ;
    if (widgets != null) dataService.remove(widgets);
    //----------------------------------------------------------------------------------------------------
  }

}
