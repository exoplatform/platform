/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.List;

import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.NameValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Tung Pham
 *          tung.pham@exoplatform.com
 * Oct 9, 2007  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
      @EventConfig(listeners = UIWidgetContainerForm.SaveActionListener.class),
      @EventConfig(listeners = UIWidgetContainerForm.CloseActionListener.class, phase =Phase.DECODE)
    }
)
public class UIWidgetContainerForm extends UIForm {

  final static private String FIELD_ID = "id" ;
  final static private String FIELD_NAME = "name" ;
  final static private String FIELD_DESC = "description" ;
  
  private Container container_ ;
  
  public UIWidgetContainerForm() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_ID, FIELD_ID, null).addValidator(NameValidator.class)) ;
    addUIFormInput(new UIFormStringInput(FIELD_NAME, FIELD_NAME, null)) ;
    addUIFormInput(new UIFormTextAreaInput(FIELD_DESC, FIELD_DESC, null)) ;
  }
  
  public void setValue(Container container) throws Exception {
    container_ = container ;
    reset() ;
    if(container_ == null) {
      getUIStringInput(FIELD_ID).setEditable(UIFormStringInput.ENABLE) ;
      return ;
    }
    
    getUIStringInput(FIELD_ID).setEditable(UIFormStringInput.DISABLE) ;
    invokeGetBindingBean(container_) ;
  }
  
  public Container getContainer() { return container_ ; }
  
  public static class SaveActionListener extends EventListener<UIWidgetContainerForm> {

    public void execute(Event<UIWidgetContainerForm> event) throws Exception {
      UIWidgetContainerForm uiForm = event.getSource() ;
      WebuiRequestContext rcontext = event.getRequestContext() ;
      UIPopupWindow uiPopup = uiForm.getParent() ;
      UIWidgetContainerManagement uiManagement = uiPopup.getParent() ;
      Container container = uiForm.getContainer() ;
      if(container == null) container = new Container() ;
      uiForm.invokeSetBindingBean(container) ;
      String cName = container.getName() ; 
      if(cName == null || cName.trim().length() < 1) container.setName(container.getId()) ;
      
      if(container != uiForm.getContainer()) {
        List<Container> existingContainers = uiManagement.getContainers() ;
        if(existingContainers != null) {
          for(Container ele : existingContainers) {
            if(ele.getId().equals(container.getId())) {
              UIPortalApplication uiPortalApp = uiManagement.getAncestorOfType(UIPortalApplication.class) ;
              uiPortalApp.addMessage(new ApplicationMessage("UIWidgetContainerForm.msg.exist", null)) ;
              rcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
              return ;
            }
          }          
        }
        uiManagement.addContainer(container) ;
      }
      uiForm.setValue(null);
      uiPopup.setShow(false) ;
      uiManagement.setSelectedContainer(container) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
    }
    
  }
  
  public static class CloseActionListener extends EventListener<UIWidgetContainerForm> {

    public void execute(Event<UIWidgetContainerForm> event) throws Exception {
      UIWidgetContainerForm uiForm = event.getSource() ;
      uiForm.setValue(null) ;
      UIPopupWindow uiPopup = uiForm.getParent() ;
      uiPopup.setShow(false) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup) ;
    }
    
  }

}
