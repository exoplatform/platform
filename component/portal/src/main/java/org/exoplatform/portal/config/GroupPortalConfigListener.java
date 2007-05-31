/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupEventListener;

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
//    PortalContainer container  = PortalContainer.getInstance() ;
//    UserPortalConfigService configService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
//    String ownerId = group.getId() ;
//    configService.printTree() ;
//    
//    //Delete Pages
//    List<Page> pages = configService.getPages(PortalConfig.GROUP_TYPE, ownerId) ;
//    for (Page ele : pages) {
//      System.out.println("\n\n\n\ngroup: " + ele.getPageId());
//      configService.remove(ele) ;
//    }
    PortalContainer container  = PortalContainer.getInstance() ;
    DataStorage dataService = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    String ownerId = group.getId() ;
    //Delete Pages
    Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
    query.setOwnerType(PortalConfig.GROUP_TYPE) ;
    query.setOwnerId(ownerId) ;
    PageList pageList = dataService.find(query) ;
    for (Object page : pageList.getAll()) {
     dataService.remove(((Page)page)) ; 
    }
    
    //Delete Navigation
    PageNavigation navigation = dataService.getPageNavigation(PortalConfig.GROUP_TYPE + "::" + ownerId) ;
    if (navigation != null) dataService.remove(navigation) ;
  }

}
