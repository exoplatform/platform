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
package org.exoplatform.portal.webui.portal;

import java.util.Iterator;

import org.exoplatform.commons.utils.ObjectPageList;
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
  
  public static String[] BEAN_FEILD = {"creator", "name", "skin"} ;
  public static String[] SELECT_ACTIONS = {"SelectPortal"} ;
  
  public UIPortalSelector() throws Exception {
    setName("UIChangePortal") ;
    UIGrid uiGrid = addChild(UIGrid.class, "PortalSelector", null) ;
    uiGrid.configure("name", BEAN_FEILD, SELECT_ACTIONS) ;
    uiGrid.getUIPageIterator().setId("ChangePortalPageInterator");
    addChild(uiGrid.getUIPageIterator()) ;
    uiGrid.getUIPageIterator().setRendered(false) ;
    DataStorage dataService = getApplicationComponent(DataStorage.class) ;
    Query<PortalConfig> query = new Query<PortalConfig>(null, null, null, null, PortalConfig.class) ;
    PageList pageList = dataService.find(query) ;
    pageList.setPageSize(10) ;
    pageList = extractPermissedPortal(pageList) ;
    uiGrid.getUIPageIterator().setPageList(pageList) ;
  }
  
  private PageList extractPermissedPortal(PageList pageList) throws Exception {
    UserACL userACL = getApplicationComponent(UserACL.class) ;
    Iterator<?> itr = pageList.getAll().iterator();
    while(itr.hasNext()) {
      PortalConfig pConfig = (PortalConfig)itr.next() ;
      if(!userACL.hasPermission(pConfig) )itr.remove() ;
    }
    return new ObjectPageList(pageList.getAll(), 10) ;
  }
  
}