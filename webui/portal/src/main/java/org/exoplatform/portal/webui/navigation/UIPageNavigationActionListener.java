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

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Sep 4, 2007  
 */
public class UIPageNavigationActionListener {
  
  static public class CreateNavigationActionListener extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception { 
      UIPortal uiPortal = Util.getUIPortal();
      
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);  
      UserPortalConfigService service = uiPortal.getApplicationComponent(UserPortalConfigService.class);
      if(service.getMakableNavigations(event.getRequestContext().getRemoteUser()).size() < 1) {
        uiApp.addMessage(new ApplicationMessage("UIPageNavigation.msg.noMakablePageNavigation", new String[]{})) ;;
        return ;
      }
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     
      
      UIPageNavigationForm uiNavigationForm = uiMaskWS.createUIComponent(UIPageNavigationForm.class, null, null);
      uiMaskWS.setUIComponent(uiNavigationForm);      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class EditNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      UIRightClickPopupMenu uiControlBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;     

      UIPageNavigationForm uiNavigationForm = uiMaskWS.createUIComponent(UIPageNavigationForm.class, null, null);
      UIPageManagement uiManagement = uiControlBar.getAncestorOfType(UIPageManagement.class);
      UIPageNodeSelector uiNavigationSelector = uiManagement.findFirstComponentOfType(UIPageNodeSelector.class);
      PageNavigation nav = uiNavigationSelector.getSelectedNavigation();
      if(nav == null) {
        uiApp.addMessage(new ApplicationMessage("UIPageNavigationControlBar.msg.noEditablePageNavigation", new String[]{})) ;; 
        return ;
      }
      uiNavigationForm.setValues(nav);
      uiMaskWS.setUIComponent(uiNavigationForm);      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement);     
    }
  }
  
  static public class DeleteNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception { 
      UIRightClickPopupMenu uiPopup = event.getSource();
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      UIPageNodeSelector uiPageNodeSelector = uiPopup.getAncestorOfType(UIPageNodeSelector.class);
      PageNavigation selectedNavigation = uiPageNodeSelector.getSelectedNavigation();
      if(!selectedNavigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)){
        UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPageNodeSelector.msg.deleteNav", null ,ApplicationMessage.ERROR)) ;

        return;
      }
      uiPageNodeSelector.deletePageNavigation(selectedNavigation) ;
      if(uiPageNodeSelector.getPageNavigations().size() < 1) {
        UIPageManagement uiManagement = uiPageNodeSelector.getParent() ;
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        pcontext.addUIComponentToUpdateByAjax(uiManagement) ;
        return;
      }
      UITree uiTree = uiPageNodeSelector.getChild(UITree.class);
      uiTree.createEvent("ChangeNode", event.getExecutionPhase(), pcontext).broadcast();
    }
  }
  
  static public class SaveNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      UIRightClickPopupMenu uiPopup = event.getSource();
      UIPageNodeSelector uiNodeSelector = uiPopup.getAncestorOfType(UIPageNodeSelector.class);
      WebuiRequestContext rcontext = event.getRequestContext();  
      
      UIPageManagement uiManagement = uiNodeSelector.getParent();
      List<PageNavigation> navs = uiNodeSelector.getPageNavigations();
      if(navs == null || navs.size() < 1) {
        UIPortalApplication uiApp = uiManagement.getAncestorOfType(UIPortalApplication.class);
        uiApp.addMessage(new ApplicationMessage("UIPageNavigationControlBar.msg.noEditablePageNavigation", new String[]{})) ;; 
        return ;
      }
      
      PageNavigation navigation = uiNodeSelector.getSelectedNavigation();
      if(navigation == null) return;
      UserPortalConfigService dataService = uiManagement.getApplicationComponent(UserPortalConfigService.class);
      PageNavigation oldNavigation = dataService.getPageNavigation(navigation.getOwnerType(), navigation.getOwnerId());
      if(oldNavigation == null) dataService.create(navigation); else dataService.update(navigation);
      rcontext.addUIComponentToUpdateByAjax(uiManagement);   
      List<PageNavigation> pnavigations = Util.getUIPortal().getNavigations();
      for(int i = 0; i < pnavigations.size(); i++) {
        if(pnavigations.get(i).getId() == navigation.getId()) {
          pnavigations.set(i, navigation);
          return;
        }
      }
      pnavigations.add(navigation) ;
    }
  }
}