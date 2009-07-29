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
package org.exoplatform.navigation.webui.component;

import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template = "app:/groovy/navigation/webui/component/UINavigationManagement.gtmpl",
  events = {
      @EventConfig(listeners = UINavigationManagement.SaveActionListener.class),
      @EventConfig(listeners = UINavigationManagement.AddRootNodeActionListener.class)
  }
)
public class UINavigationManagement extends UIContainer {
  
  private String owner;  

  @SuppressWarnings("unused")
  public UINavigationManagement() throws Exception {    
    addChild(UINavigationNodeSelector.class, null, null);
  }
  
  public void loadNavigation(Query<PageNavigation> query) throws Exception {
    DataStorage service = getApplicationComponent(DataStorage.class);
    LazyPageList navis = service.find(query);
    UINavigationNodeSelector nodeSelector = getChild(UINavigationNodeSelector.class);
    nodeSelector.initNavigations(navis.getAll());
  }
  
  public void setOwner(String owner) {
    this.owner = owner;
  }
  
  public String getOwner() { 
    return this.owner; 
  }
  
  public <T extends UIComponent> T setRendered(boolean b) {
    return super.<T> setRendered(b);
  }

  public void loadView(Event<? extends UIComponent> event) throws Exception {
    UINavigationNodeSelector uiNodeSelector = getChild(UINavigationNodeSelector.class);
    UITree uiTree = uiNodeSelector.getChild(UITree.class);
    uiTree.createEvent("ChangeNode", event.getExecutionPhase(), event.getRequestContext()).broadcast();
  }
  
  static public class SaveActionListener extends EventListener<UINavigationManagement> {

    public void execute(Event<UINavigationManagement> event) throws Exception {
      UINavigationManagement uiManagement = event.getSource();
      UINavigationNodeSelector uiNodeSelector = uiManagement.getChild(UINavigationNodeSelector.class);
      UserPortalConfigService portalConfigService = uiManagement.getApplicationComponent(UserPortalConfigService.class);
      PageNavigation navigation = uiNodeSelector.getSelectedNavigation();
      portalConfigService.update(navigation) ;
      UIPortal uiPortal = Util.getUIPortal();
      setNavigation(uiPortal.getNavigations(), navigation);
      UIPopupWindow uiPopup = uiManagement.getParent();
      uiPopup.setShow(false);
      UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;   
      Util.getPortalRequestContext().setFullRender(true);
    }
    
    private void setNavigation(List<PageNavigation> navs, PageNavigation nav) {
      for (int i = 0; i < navs.size(); i++) {
        if (navs.get(i).getId() == nav.getId()) {
          navs.set(i, nav);
          return;
        }
      }
    }
    
  }
  
  static public class AddRootNodeActionListener extends EventListener<UINavigationManagement> {

    @Override
    public void execute(Event<UINavigationManagement> event) throws Exception {
      UINavigationManagement uiManagement = event.getSource();
      UINavigationNodeSelector uiNodeSelector = uiManagement.getChild(UINavigationNodeSelector.class);
      UIPopupWindow uiManagementPopup = uiNodeSelector.getAncestorOfType(UIPopupWindow.class);
      UIPageNodeForm2 uiNodeForm = uiManagementPopup.createUIComponent(UIPageNodeForm2.class,
                                                                       null,
                                                                       null);
      uiNodeForm.setValues(null);
      uiManagementPopup.setUIComponent(uiNodeForm);
      uiNodeForm.setSelectedParent(uiNodeSelector.getSelectedNavigation());
      uiManagementPopup.setWindowSize(800, 500);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagementPopup);      
    }
    
  }
}