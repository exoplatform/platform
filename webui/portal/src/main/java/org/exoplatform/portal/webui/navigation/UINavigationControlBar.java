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
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : liem_nguyen  
 *          ncliam@gmail.com
 * May 28, 2009  
 */
@ComponentConfig(
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = {   
        @EventConfig(listeners = UINavigationControlBar.BackActionListener.class),
        @EventConfig(listeners = UINavigationControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UINavigationControlBar.AbortActionListener.class),
        @EventConfig(listeners = UINavigationControlBar.FinishActionListener.class)        
    }
)

public class UINavigationControlBar extends UIToolbar {

  public UINavigationControlBar() throws Exception {
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onclick='eXo.portal.UIPortal.switchMode(this);'") ;
  }  

  public boolean isRendered() { return false; }
  
  static public class RollbackActionListener extends EventListener<UINavigationControlBar> {
    public void execute(Event<UINavigationControlBar> event) throws Exception {
      UINavigationControlBar uiPageNav = event.getSource();
      UIPortalApplication uiPortalApp = uiPageNav.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);

      UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prcontext = Util.getPortalRequestContext();
      
      UIPortal oldUIPortal = Util.getUIPortal();
      PageNavigation oldSelectedNavi = oldUIPortal.getSelectedNavigation() ;
      PageNode oldSelectedNode = oldUIPortal.getSelectedNode() ; 
      String remoteUser = prcontext.getRemoteUser();
      String ownerUser = oldUIPortal.getOwner();

      UserPortalConfig userPortalConfig = configService.getUserPortalConfig(ownerUser, remoteUser);
      UIPortal uiPortal = uiWorkingWS.createUIComponent(prcontext, UIPortal.class, null, null);
      PortalDataMapper.toUIPortal(uiPortal, userPortalConfig);
      oldUIPortal.setNavigation(uiPortal.getNavigations());
      oldUIPortal.setSelectedNavigation(oldSelectedNavi) ;
      oldUIPortal.setSelectedNode(oldSelectedNode) ;
      List<PageNode> selectedPaths = new ArrayList<PageNode>() ;
      selectedPaths.add(oldSelectedNode) ;
      oldUIPortal.setSelectedPaths(selectedPaths) ;
      UINavigationNodeSelector uiNodeSelector = uiPageNav.<UIContainer>getParent().findFirstComponentOfType(UINavigationNodeSelector.class);
      String portalName = uiNodeSelector.getNavigations().get(0).getOwnerId();      
      UINavigationManagement uiManagement = uiPageNav.getParent();
      uiManagement.loadNavigation(new Query<PageNavigation>(PortalConfig.PORTAL_TYPE, portalName, PageNavigation.class));
      
      Class<?> [] classes = new Class<?>[]{UINavigationNodeSelector.class, UINavigationControlBar.class};      
      uiManagement.setRenderedChildrenOfTypes(classes);
      uiManagement.loadView(event);
    }
  }
  
  static public class BackActionListener extends EventListener<UINavigationControlBar> {
    public void execute(Event<UINavigationControlBar> event) throws Exception {
      UINavigationControlBar uiPageNav = event.getSource();
      UIPageManagement uiManagement = uiPageNav.getParent();
      PortalRequestContext pContext = (PortalRequestContext) event.getRequestContext();
            
      Class<?> [] childrenToRender = new Class<?>[]{UINavigationNodeSelector.class, UINavigationControlBar.class};      
      uiManagement.setRenderedChildrenOfTypes(childrenToRender);
      pContext.addUIComponentToUpdateByAjax(uiManagement);

      UIPortalApplication uiPortalApp = uiPageNav.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPortalToolPanel toolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
      toolPanel.setShowMaskLayer(true);
      pContext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pContext.setFullRender(true);
    }
  }
  
  static public class FinishActionListener  extends EventListener<UINavigationControlBar> {
    public void execute(Event<UINavigationControlBar> event) throws Exception {
      UINavigationManagement uiPageManagement = event.getSource().getParent();
      
      UINavigationNodeSelector uiNodeSelector = uiPageManagement.getChild(UINavigationNodeSelector.class);
      UserPortalConfigService dataService = uiPageManagement.getApplicationComponent(UserPortalConfigService.class);
      List<PageNavigation> deleteNavigations = uiNodeSelector.getDeleteNavigations();
      for(PageNavigation nav : deleteNavigations) {
        if(dataService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) != null) dataService.remove(nav) ;
      }
      
      List<PageNavigation> navigations = uiNodeSelector.getPageNavigations();      
      for(PageNavigation nav : navigations) {       
        if(dataService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) != null) {
          dataService.update(nav) ;
          continue;
        }
        dataService.create(nav) ;
      }
      
      UIPortalApplication uiPortalApp = uiPageManagement.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace workingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);      
      UIPopupWindow popUp = uiPageManagement.getParent();
      if (popUp != null) {
        popUp.setRendered(false);
      }
      PortalRequestContext prContext = Util.getPortalRequestContext();
      prContext.addUIComponentToUpdateByAjax(workingWS);
    }

  }

  static public class AbortActionListener  extends EventListener<UINavigationControlBar> {
    public void execute(Event<UINavigationControlBar> event) throws Exception {
      UINavigationControlBar uiControlBar = event.getSource(); 
      uiControlBar.abort(event);
    }
  }

 
  public void abort(Event<UINavigationControlBar> event) throws Exception {
    UINavigationManagement uiPageManagement = event.getSource().getParent();
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    uiPortalApp.setEditting(false);
    PortalRequestContext prContext = Util.getPortalRequestContext();
    UIPopupWindow popUp = uiPageManagement.getParent();
    if (popUp != null) {
      popUp.setRendered(false);
    }
    UIPortal portal = Util.getUIPortal();
        
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
    UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);    
    
    portal.setNavigation(configService.getUserPortalConfig(portal.getName(), prContext.getRemoteUser()).getNavigations());
    prContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
  }
    
}