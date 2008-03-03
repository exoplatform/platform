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

import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.exoplatform.webui.form.validator.SpecialCharacterValidator;

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

  final static private String FIELD_NAME = "name" ;
  final static private String FIELD_DESC = "description" ;
  
  private Container container_ ;
  
  public UIWidgetContainerForm() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_NAME, FIELD_NAME, null).addValidator(SpecialCharacterValidator.class)) ;
    addUIFormInput(new UIFormTextAreaInput(FIELD_DESC, FIELD_DESC, null)) ;
  }
  
  public void setValue(Container container) throws Exception {
    container_ = container ;
    reset() ;
    if(container_ == null) return ;
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
      
      if(container != uiForm.getContainer()) {
        container.setId(StringUtils.deleteWhitespace(container.getName())) ;
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
