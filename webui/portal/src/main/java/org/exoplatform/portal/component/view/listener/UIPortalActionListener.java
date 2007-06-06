/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.portal.component.customization.UIPortalForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.UIPageBody;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 20, 2006
 */
public class UIPortalActionListener { 
  
  static public class ChangeWindowStateActionListener extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal  = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter("portletId");
      UIPortlet uiPortlet = uiPortal.findComponentById(portletId);
      WebuiRequestContext context = event.getRequestContext();
      uiPortlet.createEvent("ChangeWindowState", event.getExecutionPhase(), context).broadcast();
    }
  }
  
  static public class EditPortalActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource();
      UIPortalForm uiForm = uiPortal.createUIComponent(UIPortalForm.class, null, null);
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      uiForm.setValues(uiPortalApp.getUserPortalConfig().getPortalConfig());
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setUIComponent(uiForm);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;      
    }
  }
  
 
  static public class LoadPageActionListener extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal  = event.getSource();
      String pageId = event.getRequestContext().getRequestParameter("pageId");
      
      System.out.println("\n\n\n == > ta thay co page "+pageId +"\n\n\n");
      
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);          
      uiPageBody.setPage(pageId, uiPortal);
      
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = Util.getPortalRequestContext();     
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);      
      uiPortal.setRenderSibbling(UIPortal.class);
      pcontext.setFullRender(true);

      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      if(uiControl == null) return;
      UIControlWSWorkingArea uiWorking = uiControl.getChild(UIControlWSWorkingArea.class);
      if(uiControl != null) pcontext.addUIComponentToUpdateByAjax(uiControl);      
      if(UIWelcomeComponent.class.isInstance(uiWorking.getUIComponent())) return;
      uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null)) ;
    }
  }
  
}