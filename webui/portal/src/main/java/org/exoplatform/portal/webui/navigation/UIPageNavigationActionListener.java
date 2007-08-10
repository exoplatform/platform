/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 1, 2007  
 */
public class UIPageNavigationActionListener {
  
  static public class CreateNavigationActionListener extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception { 
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
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
        event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());  
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
      
      uiPageNodeSelector.deletePageNavigation(selectedNavigation) ;
      //TODO: Tung.Pham added
      //------------------------------------
      if(uiPageNodeSelector.getPageNavigations().size() < 1) {
        UIPageManagement uiManagement = uiPageNodeSelector.getParent() ;
        Class<?> [] childrenToRender = {UIPageNodeSelector.class, UIPageNavigationControlBar.class };      
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
        return;
      }
      //------------------------------------
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
        rcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());  
        return ;
      }
      
      PageNavigation navigation = uiNodeSelector.getSelectedNavigation();
      if(navigation == null) return;
      UserPortalConfigService dataService = uiManagement.getApplicationComponent(UserPortalConfigService.class);
      String remoteUser = rcontext.getRemoteUser();
      PageNavigation oldNavigation = dataService.getPageNavigation(navigation.getId(), remoteUser);
      if(oldNavigation == null) dataService.create(navigation); else dataService.update(navigation);
      rcontext.addUIComponentToUpdateByAjax(uiManagement);   
      List<PageNavigation> pnavigations = Util.getUIPortal().getNavigations();
      for(int i = 0; i < pnavigations.size(); i++) {
        if(pnavigations.get(i).getId().equals(navigation.getId())) {
          pnavigations.set(i, navigation);
          return;
        }
      }
      pnavigations.add(navigation) ;
    }
  }
}
