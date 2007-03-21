/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.customization.UIContainerConfigOptions;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.customization.UIPortletOptions;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortalComponent;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 14, 2006
 */
public class UIPortalComponentActionListener {
  
  static public class ViewChildActionListener  extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      UIContainer uiContainer = event.getSource();     
      String id = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID) ;
      uiContainer.setRenderedChild(id);      
    }
  }  
  
  static public class DeleteComponentActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);      
      UIComponent uiComponent = event.getSource();
      UIPortalComponent uiParent = (UIPortalComponent)uiComponent.getParent();
      uiParent.removeChildById(id);
      
      UIPortalApplication uiPortalApp = uiParent.getAncestorOfType(UIPortalApplication.class);
      UIPortal uiPortal = uiPortalApp.findFirstComponentOfType(UIPortal.class);   
      
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      UIComponentDecorator uiWorkingArea = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingArea);
      
      if(uiPortal.isRendered()){        
        Util.showPortalComponentLayoutMode(uiPortalApp);
        return;
      }     
      Util.showPageComponentLayoutMode(uiPortalApp);
    }
  }
  
  static public class MoveChildActionListener  extends EventListener<UIContainer> {    
    public void execute(Event<UIContainer> event) throws Exception {
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      String insertPosition = pcontext.getRequestParameter("insertPosition");
      int position = -1;
      try{
        position = Integer.parseInt(insertPosition);
      }catch(Exception exp){
        position = -1;
      }
      
      boolean newComponent = false;
      String paramNewComponent = pcontext.getRequestParameter("newComponent");
      if(paramNewComponent != null) newComponent = Boolean.valueOf(paramNewComponent).booleanValue();
      
      if(newComponent){
        UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
        UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
        pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;        
        UIControlWorkspace uiControl = uiPortalApp.findFirstComponentOfType(UIControlWorkspace.class) ;
        UIComponentDecorator uiWorkingArea = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
        pcontext.addUIComponentToUpdateByAjax(uiWorkingArea);
        pcontext.setForceFullUpdate(true);        
      }
      
      String sourceId = pcontext.getRequestParameter("srcID");
      String [] split  = sourceId.split("-");
      if(split.length > 1) sourceId = split[1];
      else sourceId = split[0];
      String targetId = pcontext.getRequestParameter("targetID");
      split  = targetId.split("-");
      if(split.length > 1) targetId = split[1];
      else targetId = split[0];

      UIPortalApplication uiApp = (UIPortalApplication)Util.getPortalRequestContext().getUIApplication() ;
      UIComponent uiWorking = uiApp.findFirstComponentOfType(UIPortal.class);   
      if(!uiWorking.isRendered()) uiWorking = uiApp.findFirstComponentOfType(UIPortalToolPanel.class);
      UIContainer uiTarget = uiWorking.findComponentById(targetId);
      
      if(position < 0 && uiTarget.getChildren().size() > 0) {
        position = uiTarget.getChildren().size() ;
      }else if(position < 0){
        position = 0;
      }

      UIComponent uiSource = uiWorking.findComponentById(sourceId);  
      
      if(uiSource == null){        
        UIContainerConfigOptions uiContainerConfig = uiApp.findFirstComponentOfType(UIContainerConfigOptions.class);
        if(uiContainerConfig != null && uiContainerConfig.isRendered()){
          org.exoplatform.portal.component.view.UIContainer uiContainer =  
            uiTarget.createUIComponent(org.exoplatform.portal.component.view.UIContainer.class, null, null);
          Container container = uiContainerConfig.getContainer(sourceId); 
          container.setId(String.valueOf(container.hashCode()));
          PortalDataModelUtil.toUIContainer(uiContainer, container, true);      
          uiSource = uiContainer;         
        }else {
          UIPortletOptions uiPortletOptions = uiApp.findFirstComponentOfType(UIPortletOptions.class);
          org.exoplatform.services.portletregistery.Portlet portlet = uiPortletOptions.getPortlet(sourceId);
          UIPortlet uiPortlet =  uiTarget.createUIComponent(UIPortlet.class, null, null);
          if(portlet.getDisplayName() != null) {
            uiPortlet.setTitle(portlet.getDisplayName());
          } else if(portlet.getPortletName() != null) {
            uiPortlet.setTitle(portlet.getPortletName());
          }
          uiPortlet.setDescription(portlet.getDescription());
          StringBuilder windowId = new StringBuilder();
          windowId.append(Util.getUIPortal().getOwner()).append(":/");
          windowId.append(sourceId).append('/');
          windowId.append(uiPortlet.hashCode());
          uiPortlet.setWindowId(windowId.toString());
          uiPortlet.setShowEditControl(true);
          uiSource = uiPortlet;        
        }
        List<UIComponent> children = uiTarget.getChildren();
        uiSource.setParent(uiTarget);
        children.add(position, uiSource);
        return;
      }

      UIContainer  uiParent = uiSource.getParent();
      if(uiParent == uiTarget){
        int currentIdx = uiTarget.getChildren().indexOf(uiSource);        
        if(position <= currentIdx){
          uiTarget.getChildren().add(position, uiSource);
          currentIdx++ ;
          uiTarget.getChildren().remove(currentIdx);
          return;
        }
        uiTarget.getChildren().remove(currentIdx);
        if(position >= uiTarget.getChildren().size()){
          position = uiTarget.getChildren().size(); 
        }
        uiTarget.getChildren().add(position, uiSource);
        return;
      }
      uiParent.getChildren().remove(uiSource);
      uiTarget.getChildren().add(position, uiSource);
      uiSource.setParent(uiTarget);
    }
   
  }
  
}
