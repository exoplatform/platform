/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIGrid;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 *          tung.pham@exoplatform.com
 * May 25, 2007  
 */
@ComponentConfigs({
  @ComponentConfig(
    template = "app:/groovy/portal/webui/portal/UIChangePortal.gtmpl",
    events = @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class) 
  ),
  @ComponentConfig(
    id = "PortalSelector",
    type = UIGrid.class,
    template = "app:/groovy/portal/webui/portal/UIPortalSelector.gtmpl"
  )
})

public class UIPortalSelector extends UIContainer {
  
  public static String[] BEAN_FEILD = {"creator", "name", "skin", "factoryId"} ;
  public static String[] SELECT_ACTIONS = {"SelectPortal"} ;
  
  public UIPortalSelector() throws Exception {
    setName("UIChangePortal") ;
    UIGrid uiGrid = addChild(UIGrid.class, "PortalSelector", null) ;
    uiGrid.configure("name", BEAN_FEILD, SELECT_ACTIONS) ;
  
    DataStorage dataService = getApplicationComponent(DataStorage.class) ;
    //TODO: Tung.Pham modified
    //----------------------------------------------------------------
    //List<PortalConfig> configs = dataService.getAllPortalConfig() ;
    //PageList pageList = new ObjectPageList(configs, 10) ;
    //uiGrid.getUIPageIterator().setPageList(pageList) ;
    UserACL userACL = getApplicationComponent(UserACL.class) ;
    String accessUser = Util.getPortalRequestContext().getRemoteUser() ;
    Query<PortalConfig> query = new Query<PortalConfig>(null, null, null, PortalConfig.class) ;
    PageList pageList = dataService.find(query) ;
    pageList.setPageSize(10) ;
    int i = 1 ;
    while(i <= pageList.getAvailablePage()) {
      List<?> list = pageList.getPage(i) ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()) {
        PortalConfig config = (PortalConfig)itr.next() ;
        if(!userACL.hasViewPermission(config.getCreator(), accessUser, config.getAccessPermissions())) itr.remove() ;
      }
      i ++ ;
    }
    uiGrid.getUIPageIterator().setPageList(pageList) ;
    
    //----------------------------------------------------------------   
    //TODO check view permission for portal
  }
  
}