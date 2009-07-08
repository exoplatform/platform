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
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Oct 8, 2007  
 */
@ComponentConfig(
    template = "app:/groovy/portal/webui/application/UIGadgetContainerManagement.gtmpl",
    events = {
        @EventConfig(listeners = UIGadgetContainerManagement.AddContainerActionListener.class),
        @EventConfig(listeners = UIGadgetContainerManagement.EditContainerActionListener.class),
        @EventConfig(listeners = UIGadgetContainerManagement.DeleteContainerActionListener.class, confirm = "UIGadgetContainerManagement.confirm.DeleteContainer"),
        @EventConfig(listeners = UIGadgetContainerManagement.ChangeContainerActionListener.class),
        @EventConfig(listeners = UIGadgetContainerManagement.SaveActionListener.class),
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UIGadgetContainerManagement extends UIContainer {
  
  private List<Container> containers_ ;
  private Container selectedContainer_ = null ;
  
  public UIGadgetContainerManagement() throws Exception {
    UIPopupWindow uiPopup = addChild(UIPopupWindow.class, null, "GadgetContainerPopup");
    UIGadgetContainerForm uiForm = createUIComponent(UIGadgetContainerForm.class, null, null) ;
    uiPopup.setUIComponent(uiForm) ;
    uiPopup.setWindowSize(400, 0) ;
    uiPopup.setShowMask(true) ;
    loadContainer() ;
  }
  
  @SuppressWarnings("unchecked")
  private void loadContainer() throws Exception {
    WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance() ;
    UIPortalApplication uiPortalApp = (UIPortalApplication)rcontext.getUIApplication() ;
    UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID) ;
    UIGadgets uiGadgets = uiControl.findFirstComponentOfType(UIGadgets.class) ;
    List<UIComponent> uiChildren = uiGadgets.getChildren() ;
    if(uiChildren == null) return ;
    containers_ = new ArrayList<Container> () ;
    for(UIComponent ele : uiChildren) {
      containers_.add(PortalDataMapper.toContainer((org.exoplatform.portal.webui.container.UIContainer)ele)) ;
    }
    loadSelectedContainer() ;
  }
  
  public void loadSelectedContainer() {
    if(containers_ == null  || containers_.size() < 1) setSelectedContainer(null) ;
    else setSelectedContainer(containers_.get(0)) ; 
  }
  
  public List<Container> getContainers() {
    if(containers_ == null) containers_ = new ArrayList<Container>() ;
    return containers_;
  }

  public Container getSelectedContainer() {
    return selectedContainer_;
  }

  public void setSelectedContainer(Container selectedContainer) {
    selectedContainer_ = selectedContainer;
  }
  
  public void addContainer(Container container) {
    getContainers().add(container) ;
  }
  
  public void deletedContainer(Container container) {
    if(containers_ == null || containers_.size() < 1) return ;
    containers_.remove(container) ;
  }
  
  static public class AddContainerActionListener extends EventListener<UIGadgetContainerManagement> {

    public void execute(Event<UIGadgetContainerManagement> event) throws Exception {
      UIGadgetContainerManagement uiManagement = event.getSource() ;
      UIPopupWindow uiPopup = uiManagement.getChild(UIPopupWindow.class) ;
      UIGadgetContainerForm uiForm = (UIGadgetContainerForm)uiPopup.getUIComponent() ;
      uiForm.setValue(null) ;
      uiPopup.setShow(true) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup) ;
    }
    
  }

  static public class EditContainerActionListener extends EventListener<UIGadgetContainerManagement> {

    public void execute(Event<UIGadgetContainerManagement> event) throws Exception {
      UIGadgetContainerManagement uiManagement = event.getSource() ;
      UIPopupWindow uiPopup = uiManagement.getChild(UIPopupWindow.class) ;
      Container selectedContainer = uiManagement.getSelectedContainer() ; 
      if(selectedContainer == null) return ;
      UIGadgetContainerForm uiForm = (UIGadgetContainerForm)uiPopup.getUIComponent() ;
      uiForm.setValue(selectedContainer) ;
      uiPopup.setShow(true) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup) ;
    }
    
  }
  
  static public class DeleteContainerActionListener extends EventListener<UIGadgetContainerManagement> {

    public void execute(Event<UIGadgetContainerManagement> event) throws Exception {
      UIGadgetContainerManagement uiManagement = event.getSource() ;
      Container selectedContainer = uiManagement.getSelectedContainer() ;
      if(selectedContainer == null) return ;
      uiManagement.deletedContainer(selectedContainer) ;
      uiManagement.loadSelectedContainer() ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }

  static public class ChangeContainerActionListener extends EventListener<UIGadgetContainerManagement> {

    public void execute(Event<UIGadgetContainerManagement> event) throws Exception {
      WebuiRequestContext rcontext = event.getRequestContext() ; 
      String containerId = rcontext.getRequestParameter(OBJECTID) ;
      UIGadgetContainerManagement uiManagement = event.getSource() ;
      for(Container ele : uiManagement.getContainers()) {
        if(ele.getId().equals(containerId)) {
          uiManagement.setSelectedContainer(ele) ;
          break ;
        }
      }
      rcontext.addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
  static public class SaveActionListener extends EventListener<UIGadgetContainerManagement> {

    public void execute(Event<UIGadgetContainerManagement> event) throws Exception {
      UIGadgetContainerManagement uiManagement = event.getSource() ;
      WebuiRequestContext rcontext = event.getRequestContext() ;
      List<Container> containers = uiManagement.getContainers() ;
      
      UserPortalConfigService configService = uiManagement.getApplicationComponent(UserPortalConfigService.class) ;
      UIPortalApplication uiPortalApp = uiManagement.getAncestorOfType(UIPortalApplication.class) ;
      Gadgets gadgets = uiPortalApp.getUserPortalConfig().getGadgets() ;
      gadgets.setChildren((ArrayList<Container>)containers) ;
      configService.update(gadgets) ;
      
      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID) ;
      UIGadgets uiGadgets = uiControl.findFirstComponentOfType(UIGadgets.class) ;
      PortalDataMapper.toUIGadgets(uiGadgets, gadgets) ;
      
//      rcontext.addUIComponentToUpdateByAjax(uiControl) ;
      UIMaskWorkspace uiMaskWorkspace = uiManagement.getParent() ;
      uiMaskWorkspace.createEvent("Close", Phase.PROCESS, rcontext).broadcast() ;
    }
    
  }

}