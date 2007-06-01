/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.customization.UIPortalForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
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
  
 
  //TODO: Rename this class to RemoveApplicationActionListener,  if the listener can remove only
  //the application in the page , then it should be UIPageActionListener class
  /*static public class RemoveJSApplicationToDesktopActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      String instanceId  = event.getRequestContext().getRequestParameter("jsInstanceId");
      UIPage uiPage  = event.getSource().findFirstComponentOfType(UIPage.class);
      uiPage.removeChildById(instanceId);
    }
  }*/
  
}