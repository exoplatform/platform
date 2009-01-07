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
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.UIManagement.ManagementMode;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          lebienthuy@gmail.com
 * Mar 16, 2007  
 */
@ComponentConfig(
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = {   
        @EventConfig(listeners = UIPageNavigationControlBar.BackActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.RollbackActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.AbortActionListener.class),
        @EventConfig(listeners = UIPageNavigationControlBar.FinishActionListener.class)        
    }
)

public class UIPageNavigationControlBar extends UIToolbar {

  public UIPageNavigationControlBar() throws Exception {
    setToolbarStyle("ControlToolbar") ;
    setJavascript("Preview","onclick='eXo.portal.UIPortal.switchMode(this);'") ;
  }
  
  static public class RollbackActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageNav = event.getSource();
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
      UIPageNodeSelector uiPageNodeSelector = uiPageNav.<UIContainer>getParent().findFirstComponentOfType(UIPageNodeSelector.class);
      uiPageNodeSelector.loadNavigations();
      
      UIPageManagement uiManagement = uiPageNav.getParent();
      Class<?> [] classes = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      UIPageEditBar uiEditBar = uiManagement.getChild(UIPageEditBar.class) ;
      uiEditBar.setUIPage(null) ;
      uiManagement.setRenderedChildrenOfTypes(classes);
      uiManagement.setMode(ManagementMode.EDIT, event) ;
    }
  }
  
  static public class BackActionListener extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiPageNav = event.getSource();
      UIPageManagement uiManagement = uiPageNav.getParent();
      PortalRequestContext pContext = (PortalRequestContext) event.getRequestContext();
      
      UIPageEditBar uiPageEditBar = uiManagement.getChild(UIPageEditBar.class);
      Class<?> [] childrenToRender = null;
      if(uiPageEditBar.isRendered()) {
        childrenToRender = new Class<?>[]{UIPageEditBar.class, UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      } else {
        childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class};
      }
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
  
  static public class FinishActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageManagement uiPageManagement = event.getSource().getParent(); 
      uiPageManagement.getChild(UIPageEditBar.class).savePage();
      
      UIPageNodeSelector uiNodeSelector = uiPageManagement.getChild(UIPageNodeSelector.class);
      UserPortalConfigService dataService = uiPageManagement.getApplicationComponent(UserPortalConfigService.class);
      List<PageNavigation> deleteNavigations = uiNodeSelector.getDeleteNavigations();
      for(PageNavigation nav : deleteNavigations) {
        if(dataService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) != null) dataService.remove(nav) ;
      }
      
      List<PageNavigation> navigations = uiNodeSelector.getPageNavigations();
      String accessUser = event.getRequestContext().getRemoteUser();
      for(PageNavigation nav : navigations) {       
        if(dataService.getPageNavigation(nav.getOwnerType(), nav.getOwnerId()) != null) {
          dataService.update(nav) ;
          continue;
        }
        dataService.create(nav) ;
      }
      Util.getUIPortalApplication().setEditting(false) ;
      UIPortal uiPortal = Util.getUIPortal();
      UserPortalConfig portalConfig  = dataService.getUserPortalConfig(uiPortal.getName(), accessUser);
      uiPortal.setNavigation(portalConfig.getNavigations());
      event.getSource().abort(event);
    }

  }

  static public class AbortActionListener  extends EventListener<UIPageNavigationControlBar> {
    public void execute(Event<UIPageNavigationControlBar> event) throws Exception {
      UIPageNavigationControlBar uiControlBar = event.getSource(); 
      uiControlBar.abort(event);
    }
  }

 
  public void abort(Event<UIPageNavigationControlBar> event) throws Exception {
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    uiPortalApp.setEditting(false) ;
    PortalRequestContext prContext = Util.getPortalRequestContext();  
    UIPortal portal = Util.getUIPortal();
    UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
    prContext.addUIComponentToUpdateByAjax(uiControl);    
    
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
    UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);
    
    //  TODO tam.nguyen: remove maximize component in order to return home page
    UIPageBody uiPageBody = portal.findFirstComponentOfType(UIPageBody.class);
    uiPageBody.setMaximizedUIComponent(null);
    
    portal.setNavigation(configService.getUserPortalConfig(portal.getName(), prContext.getRemoteUser()).getNavigations());
    uiWorkingWS.setRenderedChild(UIPortal.class) ;
    prContext.addUIComponentToUpdateByAjax(uiWorkingWS) ;      
    prContext.setFullRender(true);
  }
    
}