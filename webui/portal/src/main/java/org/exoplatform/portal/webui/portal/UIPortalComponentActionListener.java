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
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.UILoginForm;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
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
  
  static public class ShowLoginFormActionListener  extends EventListener<UIPortalComponent> {    
    public void execute(Event<UIPortalComponent> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UILoginForm uiLoginForm = uiMaskWS.createUIComponent(UILoginForm.class, null, "UIPortalComponentLogin");
      uiMaskWS.setUIComponent(uiLoginForm);
      uiMaskWS.setWindowSize(630, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class RemoveJSApplicationToDesktopActionListener  extends EventListener<UIPortalComponent> {    
    public void execute(Event<UIPortalComponent> event) throws Exception {
     UIPortal uiPortal = Util.getUIPortal();
     UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
     UIPage uiPage = uiApp.findFirstComponentOfType(UIPage.class);
     String id  = event.getRequestContext().getRequestParameter("jsInstanceId"); 
     uiPage.removeChildById(id);
     
     Page page = PortalDataMapper.toPageModel(uiPage); 
     UserPortalConfigService configService = uiPortal.getApplicationComponent(UserPortalConfigService.class);     
     if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
     configService.update(page);
    }
  }
  
  static public class DeleteComponentActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);      
      UIComponent uiComponent = event.getSource();
      UIPortalComponent uiParent = (UIPortalComponent)uiComponent.getParent();
      UIComponent uiRemoveComponent = uiParent.removeChildById(id);
      Util.showComponentLayoutMode(uiRemoveComponent.getClass());
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = uiParent.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
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
        pcontext.setFullRender(true);        
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
          org.exoplatform.portal.webui.container.UIContainer uiContainer =  
            uiTarget.createUIComponent(org.exoplatform.portal.webui.container.UIContainer.class, null, null);
          Container container = uiContainerConfig.getContainer(sourceId); 
          container.setId(String.valueOf(container.hashCode()));
          PortalDataMapper.toUIContainer(uiContainer, container);      
          uiSource = uiContainer;   
        }else {
          UIPortletOptions uiPortletOptions = uiApp.findFirstComponentOfType(UIPortletOptions.class);
          org.exoplatform.application.registry.Application portlet = uiPortletOptions.getPortlet(sourceId);
          UIPortlet uiPortlet =  uiTarget.createUIComponent(UIPortlet.class, null, null);
          if(portlet.getDisplayName() != null) {
            uiPortlet.setTitle(portlet.getDisplayName());
          } else if(portlet.getApplicationName() != null) {
            uiPortlet.setTitle(portlet.getApplicationName());
          }
          uiPortlet.setDescription(portlet.getDescription());
          StringBuilder windowId = new StringBuilder();
          UIPage uiPage  = uiTarget.getAncestorOfType(UIPage.class);
          if(uiPage != null) windowId.append(uiPage.getOwnerType()); 
          else windowId.append(PortalConfig.PORTAL_TYPE);
          windowId.append('#').append(Util.getUIPortal().getOwner()).append(":/");
//        TODO review code in next line. It was changed by Le Bien Thuy
          windowId.append(portlet.getApplicationGroup() + "/" + portlet.getApplicationName()).append('/');
          windowId.append(uiPortlet.hashCode());
          uiPortlet.setWindowId(windowId.toString());
          uiPortlet.setShowEditControl(true);
          uiSource = uiPortlet;
        }
        List<UIComponent> children = uiTarget.getChildren();
        uiSource.setParent(uiTarget);
        children.add(position, uiSource);
        Util.showComponentLayoutMode(uiSource.getClass());   
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
  
  public static class ChangeLanguageActionListener extends EventListener<UIPortal> {

    @Override
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource() ;
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class) ;
      UIMaskWorkspace uiMaskWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWorkspace.createUIComponent(UILanguageSelector.class, null, null) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
    }
    
  }
  
}
