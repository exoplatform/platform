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
package org.exoplatform.portal.webui.workspace;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.UIManagement.ManagementMode;
import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.page.UIPageCreationWizard;
import org.exoplatform.portal.webui.page.UIPageEditWizard;
import org.exoplatform.portal.webui.page.UIWizardPageCreationBar;
import org.exoplatform.portal.webui.page.UIWizardPageSetInfo;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalForm;
import org.exoplatform.portal.webui.portal.UIPortalManagement;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * May 5, 2009  
 */
public class UIMainActionListener {

  static public <T extends UIComponent> void setUIControlWSWorkingComponent(Class<T> clazz) throws Exception {
    UIPortalApplication uiApp = Util.getUIPortalApplication();
    UIControlWorkspace uiControl =  uiApp.getChild(UIControlWorkspace.class) ;
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
    uiWorking.setUIComponent(uiWorking.createUIComponent(clazz, null, null)) ;
  }

  @SuppressWarnings("unchecked")
  static public <T extends UIComponent> T getUIControlWSWorkingComponent() throws Exception {
    UIPortalApplication uiApp = Util.getUIPortalApplication();
    UIControlWorkspace uiControl =  uiApp.getChild(UIControlWorkspace.class) ;
    UIControlWSWorkingArea uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
    return (T)uiWorking.getUIComponent();
  }
    
  static public class EditPageActionListener extends EventListener<UIWorkingWorkspace> {    
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIMainActionListener.setUIControlWSWorkingComponent(UIPageManagement.class) ;
      Util.getUIPortalApplication().setEditting(true) ;
      UIPageManagement uiManagement = UIMainActionListener.getUIControlWSWorkingComponent();      
      uiManagement.setMode(ManagementMode.EDIT, event);
    }
  }
  
  static  public class EditCurrentPageActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true) ;
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class) ;
      uiToolPanel.setShowMaskLayer(false);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
      uiToolPanel.setWorkingComponent(UIPageEditWizard.class, null);
      UIPageEditWizard uiWizard = (UIPageEditWizard)uiToolPanel.getUIComponent();
      uiWizard.setDescriptionWizard(1);
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setEditMode();
      uiPageSetInfo.createEvent("ChangeNode", Event.Phase.DECODE, event.getRequestContext()).broadcast();   
    }
  }  

  static public class PageCreationWizardActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIMainActionListener.setUIControlWSWorkingComponent(UIWizardPageCreationBar.class);
      UIPortalApplication uiApp = Util.getUIPortalApplication();
      uiApp.setEditting(true) ;
      UIWorkingWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);      
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class) ;
      uiToolPanel.setShowMaskLayer(false);
      uiToolPanel.setWorkingComponent(UIPageCreationWizard.class, null) ;
      UIPageCreationWizard uiWizard = (UIPageCreationWizard) uiToolPanel.getUIComponent() ;
      UIWizardPageSetInfo uiPageSetInfo = uiWizard.getChild(UIWizardPageSetInfo.class);
      uiPageSetInfo.setShowPublicationDate(false) ;
      UIMainActionListener.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
    }
  }

  static public class EditPortalActionListener extends EventListener<UIWorkingWorkspace> {    
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal() ;
      if(!uiPortal.isModifiable()) {
        UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalManagement.msg.Invalid-editPermission", new String[]{uiPortal.getName()})) ;;
        return ;
      }
      UIMainActionListener.setUIControlWSWorkingComponent(UIPortalManagement.class) ;
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      ((UIPortalApplication)pcontext.getUIApplication()).setEditting(true) ;
      UIPortalManagement uiManagement = UIMainActionListener.getUIControlWSWorkingComponent();      
      uiManagement.setMode(ManagementMode.EDIT, event);
    }
  }
  
  public static class CreatePortalActionListener extends EventListener<UIWorkingWorkspace> {
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);  
      UserACL userACL = uiApp.getApplicationComponent(UserACL.class) ;
      if(!userACL.hasCreatePortalPermission(prContext.getRemoteUser())){
        uiApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-createPermission", null)) ;;  
        return;
      }
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPortalForm uiNewPortal = uiMaskWS.createUIComponent(UIPortalForm.class, "CreatePortal", "UIPortalForm");
      uiMaskWS.setUIComponent(uiNewPortal);
      uiMaskWS.setShow(true);
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class BrowsePortalActionListener extends EventListener<UIWorkingWorkspace> {    
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIMainActionListener.setUIControlWSWorkingComponent(UIPortalManagement.class) ;
      UIPortalManagement uiManagement = UIMainActionListener.getUIControlWSWorkingComponent();      
      uiManagement.setMode(ManagementMode.BROWSE, event);
    }
  }

  static public class BrowsePageActionListener extends EventListener<UIWorkingWorkspace> {    
    public void execute(Event<UIWorkingWorkspace> event) throws Exception {
      UIMainActionListener.setUIControlWSWorkingComponent(UIPageManagement.class) ;
      UIPageManagement uiManagement = UIMainActionListener.getUIControlWSWorkingComponent();      
      uiManagement.setMode(ManagementMode.BROWSE, event);
    }
  }
  
}