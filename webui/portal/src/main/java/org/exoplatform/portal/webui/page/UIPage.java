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
package org.exoplatform.portal.webui.page;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.WindowState;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.page.UIPageActionListener.DeleteWidgetActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.DeleteGadgetActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.EditPageActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.RemoveChildActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.SaveWidgetPropertiesActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.SaveGadgetPropertiesActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.SaveWindowPropertiesActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.ShowAddNewApplicationActionListener;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * May 19, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/page/UIPage.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class)
      }
  ), //save desktop setting
  @ComponentConfig(
      id = "Desktop",
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/page/UIPageDesktop.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class),
        @EventConfig(listeners = ShowLoginFormActionListener.class),
        @EventConfig(listeners = DeleteWidgetActionListener.class),
        @EventConfig(listeners = DeleteGadgetActionListener.class),
        @EventConfig(listeners = RemoveChildActionListener.class),
        @EventConfig(listeners = SaveWidgetPropertiesActionListener.class),
        @EventConfig(listeners = SaveGadgetPropertiesActionListener.class),
        @EventConfig(listeners = SaveWindowPropertiesActionListener.class),
        @EventConfig(listeners = ShowAddNewApplicationActionListener.class),
        @EventConfig(listeners = UIPage.ChangePageActionListener.class)
      }
  )
})
public class UIPage extends UIContainer {
  
  private String pageId;
  private String ownerId ;
  private String ownerType ;  
  
  private String [] accessPermissions;
  private String editPermission;
  
  private boolean showMaxWindow = false ;
  private UIPortlet maximizedUIPortlet;
  
  public String getOwnerId() { return ownerId ; }
  public void   setOwnerId(String s) { ownerId = s ; } 
  
  public boolean isShowMaxWindow() { return showMaxWindow; }
  public void setShowMaxWindow(Boolean showMaxWindow) { this.showMaxWindow = showMaxWindow; }
  
  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessGroups) { this.accessPermissions = accessGroups; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }
  
  public String getPageId() { return pageId; }
  public void setPageId(String id) { pageId = id; }
  
  public UIPortlet getMaximizedUIPortlet() { return maximizedUIPortlet;  }
  
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
  public void setMaximizedUIPortlet(UIPortlet maximizedUIPortlet) {
    this.maximizedUIPortlet = maximizedUIPortlet;
  }
  
  //TODO: dang.tung -> page navigation
  //-----------------------------------------------------------------
  public List<PageNavigation> getNavigations() throws Exception {
    List<PageNavigation> allNav =Util.getUIPortal().getNavigations() ;
    String removeUser = Util.getPortalRequestContext().getRemoteUser();
    List<PageNavigation> result = new ArrayList<PageNavigation>();
    for(PageNavigation nav: allNav){
      result.add(PageNavigationUtils.filter(nav, removeUser));
    }
    return result;
  }
  
  static  public class ChangePageActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet currentPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          currentPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
    }
  }
  //------------------------------------------------------------------
}