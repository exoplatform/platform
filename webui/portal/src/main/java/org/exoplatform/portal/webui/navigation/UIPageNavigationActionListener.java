/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
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
      UIPageManagement uiPManagement = uiControlBar.getAncestorOfType(UIPageManagement.class);
      UIPageNodeSelector uiNavigationSelector = uiPManagement.findFirstComponentOfType(UIPageNodeSelector.class);
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
    }
  }
  
  static public class DeleteNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception { 
      UIRightClickPopupMenu uiPopup = event.getSource();
      UIPageNodeSelector uiPageNodeSelector = uiPopup.getAncestorOfType(UIPageNodeSelector.class);
      PageNavigation selectedNavigation = uiPageNodeSelector.getSelectedNavigation();
      
      //TODO: Tung.Pham modified
      //---------------------------------------------------------
//      UserPortalConfigService configService = pageNodeSelector.getApplicationComponent(UserPortalConfigService.class);
//      configService.remove(selectedNavigation);
//      Util.getUIPortal().getNavigations().remove(selectedNavigation);
//      List<PageNavigation> oldList = Util.getUIPortal().getNavigations();
//      int i = 0;
//      for(i = 0; i< oldList.size(); i ++) {
//        if(oldList.get(i).getId().equals(selectedNavigation.getId())) break;
//      }
//      if( i< oldList.size()) oldList.remove(i);
//      pageNodeSelector.loadNavigations();
      List<PageNavigation> oldList = Util.getUIPortal().getNavigations() ;
      Iterator<PageNavigation> itr = oldList.iterator() ;
      while(itr.hasNext()) {
        PageNavigation navi = itr.next() ;
        if(navi.getId().equals(selectedNavigation.getId())) itr.remove() ;
      }
      uiPageNodeSelector.loadNavigations() ;
      //----------------------------------------------------
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNodeSelector.getAncestorOfType(UIPageManagement.class));      
    }
  }
  
  static public class SaveNavigationActionListener extends EventListener<UIRightClickPopupMenu> {
    public void execute(Event<UIRightClickPopupMenu> event) throws Exception {
      UIRightClickPopupMenu uiPopup = event.getSource();
      UIPageNodeSelector uiNodeSelector = uiPopup.getAncestorOfType(UIPageNodeSelector.class);
      WebuiRequestContext rcontext = event.getRequestContext();  
      
      UIPageManagement uiManagement = uiNodeSelector.getParent();
      List<PageNavigation> navs = uiNodeSelector.getNavigations();
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
    }
  }
}
