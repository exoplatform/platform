/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.HashMap;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIPortletForm;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPageBody;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ActionInput;
import org.exoplatform.services.portletcontainer.pci.ActionOutput;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 29, 2006
 */
public class UIPortletActionListener   {
  
  static public class ProcessActionActionListener  extends EventListener<UIPortlet> {
    public void execute(Event<UIPortlet> event) throws Exception {
      UIPortlet uiPortlet = event.getSource() ;
      PortalRequestContext prcontext = (PortalRequestContext) event.getRequestContext();
      ExoContainer container = 
        event.getRequestContext().getApplication().getApplicationServiceContainer() ;
      UIPortalApplication uiPortalApp = uiPortlet.getAncestorOfType(UIPortalApplication.class);
      PortletContainerService portletContainer = 
        (PortletContainerService) container.getComponentInstanceOfType(PortletContainerService.class);
      ActionInput actionInput = new ActionInput();
      OrganizationService service = uiPortlet.getApplicationComponent(OrganizationService.class);
      UserProfile userProfile = service.getUserProfileHandler().findUserProfileByName(uiPortalApp.getOwner()) ;
      actionInput.setWindowID(uiPortlet.getExoWindowID());
      if(userProfile != null) actionInput.setUserAttributes(userProfile.getUserInfoMap());
      else actionInput.setUserAttributes(new HashMap());
      //TODO: Need to maintain the current portlet mode in the UIPortlet
      actionInput.setPortletMode(uiPortlet.getCurrentPortletMode());
      //TODO: Need to maintain the current portlet state in the UIPortlet
      actionInput.setWindowState(uiPortlet.getCurrentWindowState());
      actionInput.setMarkup("text/html");
      actionInput.setStateChangeAuthorized(true);
      ActionOutput output = 
        portletContainer.processAction(prcontext.getRequest(), prcontext.getResponse(), actionInput);
      
      WindowState state = output.getNextState() ;
      if (state != null ) {
        UIPage uiPage =  uiPortlet.getAncestorOfType(UIPage.class) ;
        if (state == WindowState.MAXIMIZED) {
          uiPortlet.setCurrentWindowState(WindowState.MAXIMIZED) ;
          if (uiPage != null) uiPage.setMaximizedUIPortlet(uiPortlet) ;
        } else if (state == WindowState.MINIMIZED ) {
          uiPortlet.setCurrentWindowState(WindowState.MINIMIZED) ;
          if(uiPage != null) uiPage.setMaximizedUIPortlet(null) ;
        } else {
          uiPortlet.setCurrentWindowState(WindowState.NORMAL) ;
          if(uiPage != null) uiPage.setMaximizedUIPortlet(null) ;
        }

      }

      uiPortlet.setRenderParametersMap(output.getRenderParameters()) ;
    }
  }
  
  static public class RenderActionListener  extends EventListener<UIPortlet> {
    public void execute(Event<UIPortlet> event) throws Exception {      
      UIPortlet uiPortlet = event.getSource() ;
      uiPortlet.setRenderParametersMap(null) ;
    }
  }
  
  static public class ChangeWindowStateActionListener extends EventListener<UIPortlet> {
    public void execute(Event<UIPortlet> event) throws Exception {      
      UIPortlet uiPortlet = event.getSource();
      
      UIPortalApplication uiPortalApp = uiPortlet.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      
      String windowState = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID).trim();
      UIPageBody uiPageBody = uiPortlet.getAncestorOfType(UIPageBody.class);
      if(windowState.equals(WindowState.MAXIMIZED.toString())){ 
        if(uiPageBody !=  null){
          uiPortlet.setCurrentWindowState(WindowState.MAXIMIZED) ;
          uiPageBody.setMaximizedUIComponent(uiPortlet);
        }else{
          uiPortlet.setCurrentWindowState(WindowState.NORMAL);
        }
        return;
      }
      if(uiPageBody != null){
        UIPortlet maxPortlet = (UIPortlet)uiPageBody.getMaximizedUIComponent(); 
        if(maxPortlet == uiPortlet) uiPageBody.setMaximizedUIComponent(null);
      }
      if(windowState.equals(WindowState.MINIMIZED.toString())) { 
        uiPortlet.setCurrentWindowState(WindowState.MINIMIZED) ;
        return ;
      } 
      uiPortlet.setCurrentWindowState(WindowState.NORMAL);
      
    }
  }
    
  static public class ChangePortletModeActionListener extends EventListener<UIPortlet> {
    public void execute(Event<UIPortlet> event) throws Exception {   
      UIPortlet uiPortlet = event.getSource();
      String portletMode = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      if(portletMode.equals(PortletMode.HELP.toString())) {
        uiPortlet.setCurrentPortletMode(PortletMode.HELP) ;     
      }else if(portletMode.equals(PortletMode.EDIT.toString())){ 
        uiPortlet.setCurrentPortletMode(PortletMode.EDIT) ;
      } else {
        uiPortlet.setCurrentPortletMode(PortletMode.VIEW) ;
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortlet);
    }
  }
  
  static public class EditPortletActionListener extends EventListener<UIPortlet> {
    public void execute(Event<UIPortlet> event) throws Exception {  
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;       
    
      UIPortlet uiPortlet = event.getSource();
      UIPortletForm uiPortletForm = uiMaskWS.createUIComponent(UIPortletForm.class, null, null); 
      uiPortletForm.setValues(uiPortlet);
      uiMaskWS.setUIComponent(uiPortletForm);
      uiMaskWS.setWindowSize(800, -1);
      uiMaskWS.setShow(true);
      
      //event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      Util.updateUIApplication(event);  
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
}
