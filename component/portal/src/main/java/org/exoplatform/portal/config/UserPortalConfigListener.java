/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.content.ContentDAO;
import org.exoplatform.portal.content.model.ContentNavigation;
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
    UserPortalConfigService configService = (UserPortalConfigService)container.getComponentInstanceOfType(UserPortalConfigService.class) ;
    ContentDAO contentService = (ContentDAO)container.getComponentInstanceOfType(ContentDAO.class) ;
    String userName = user.getUserName() ;
    
    //Delete pages
    List<Page> pages = configService.getPages(PortalConfig.USER_TYPE, userName) ;
    for (Page ele : pages) {
      configService.remove(ele) ;
    }
    
    //Delete Navigation
    String id = PortalConfig.USER_TYPE + "::" + userName ;
    PageNavigation navigation = configService.getPageNavigation(id, userName) ;
    if (navigation != null) configService.remove(navigation) ;

    //Delete Widgets
    Widgets widgets = configService.getWidgets(id) ;
    if (widgets != null) configService.remove(widgets);
    
    //Delete Content
    ContentNavigation contentNavigation = contentService.get(userName) ;
    if (contentNavigation != null) contentService.remove(userName) ;
    //----------------------------------------------------------------------------------------------------
  }

}
