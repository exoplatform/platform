/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.toolbar.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * May 26, 2009  
 */
@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class,
                 template = "app:/groovy/admintoolbar/webui/component/UIUserToolBarPortlet.gtmpl"
)
public class UIUserToolBarPortlet extends UIPortletApplication {

  public UIUserToolBarPortlet() throws Exception {
  }
  
  public List<String> getAllDashboards() throws Exception {
    List<String> list = new ArrayList<String>();
    list.add("Sales BI Reports");
    list.add("Development Reports");
    list.add("Scrum Reports");
    list.add("Marketing Results");
    
    return list;
  }

  @SuppressWarnings({ "unchecked", "deprecation" })
  public List<String> getAllPortalNames() throws Exception {
    List<String> list = new ArrayList<String>();
    DataStorage dataStorage = getApplicationComponent(DataStorage.class);
    Query<PortalConfig> query = new Query<PortalConfig>(null, null, null, null, PortalConfig.class) ;
    PageList pageList = dataStorage.find(query) ;
    String userId = Util.getPortalRequestContext().getRemoteUser();
    UserACL userACL = getApplicationComponent(UserACL.class) ;
    List<PortalConfig> configs = pageList.getAll();    
    for(PortalConfig ele : configs) {
      if(userACL.hasPermission(ele, userId)) {
        list.add(ele.getName());                
      }
    }         
    return list;
  }

  public List<PageNavigation> getGroupNavigations() throws Exception {    
    String remoteUser = Util.getPortalRequestContext().getRemoteUser();
    List<PageNavigation> allNavigations = Util.getUIPortal().getNavigations();
    List<PageNavigation> navigations = new ArrayList<PageNavigation>();
    for (PageNavigation navigation : allNavigations) {      
      if (navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) {
        navigations.add(PageNavigationUtils.filter(navigation, remoteUser));
      }
    }
    return navigations;
  }

  public String getCurrentPortal() {
    return Util.getUIPortal().getName();
  }
  
  public PageNavigation getCurrentPortalNavigation() throws Exception {
    return getPageNavigation(PortalConfig.PORTAL_TYPE + "::" + Util.getUIPortal().getName());
  }
  
  public String getPortalURI(String portalName) {
    return Util.getPortalRequestContext().getPortalURI().replace(getCurrentPortal(), portalName);
  }
  
  private PageNavigation getPageNavigation(String owner){
    List<PageNavigation> allNavigations = Util.getUIPortal().getNavigations();
    for(PageNavigation nav: allNavigations){
      if(nav.getOwner().equals(owner)) return nav;
    }
    return null;
  }
  
}