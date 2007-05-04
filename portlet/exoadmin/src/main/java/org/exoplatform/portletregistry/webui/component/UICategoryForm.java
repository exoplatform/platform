/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.application.registery.ApplicationCategory;
import org.exoplatform.application.registery.ApplicationRegisteryService;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Hoa Nguyen
 *          hoa.nguyen@exoplatform.com
 * Jul 4, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
    events = {
      @EventConfig(phase = Phase.DECODE, listeners = UICategoryForm.BackActionListener.class),
      @EventConfig(listeners = UICategoryForm.SaveActionListener.class)
    }
)
public class UICategoryForm extends UIForm { 
  
  final static private String FIELD_NAME = "name" ;
  final static private String FIELD_DISPLAY_NAME = "displayName" ;
  final static private String FIELD_DESCRIPTION = "description" ;
  
  private ApplicationCategory category_ = null ;
  
  public UICategoryForm() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_NAME, FIELD_NAME, null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormStringInput(FIELD_DISPLAY_NAME, FIELD_DISPLAY_NAME, null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormTextAreaInput(FIELD_DESCRIPTION, FIELD_DESCRIPTION, null)); 
  } 
  
  public void setValue(ApplicationCategory category) throws Exception {
    if(category == null) {      
      getUIStringInput(FIELD_NAME).setEditable(true).setValue(null);
      getUIStringInput(FIELD_DISPLAY_NAME).setEditable(true).setValue(null);
      getUIStringInput(FIELD_DESCRIPTION).setValue(null);
      category_ = null;
      return ;
    }
    getUIStringInput(FIELD_NAME).setEditable(false);    
    category_ = category ;
    invokeGetBindingBean(category);
  }
  
  public ApplicationCategory getCategory() { return category_ ; }
  
  static public class SaveActionListener extends EventListener<UICategoryForm> {
    public void execute(Event<UICategoryForm> event) throws Exception{
      UICategoryForm uiForm = event.getSource() ;
      ApplicationRegistryWorkingArea workingArea = uiForm.getParent() ;
      UIPortletRegistryPortlet uiRegistryPortlet = workingArea.getParent() ;
      ApplicationRegistryControlArea uiRegistryCategory = uiRegistryPortlet.getChild(ApplicationRegistryControlArea.class) ;

      ApplicationRegisteryService service = uiForm.getApplicationComponent(ApplicationRegisteryService.class);
      ApplicationCategory category = uiForm.getCategory() ;
      uiRegistryCategory.initValues(null) ;

      if(category == null) {
        category = new ApplicationCategory();
        uiRegistryCategory.setSelectedCategory(category);
      }
      uiForm.invokeSetBindingBean(category) ;
      service.save(category) ;      
      uiForm.reset();
      uiRegistryCategory.setSelectedCategory(category);
    }
  }
  
  static public class BackActionListener extends EventListener<UICategoryForm>{
    public void execute(Event<UICategoryForm> event) throws Exception{
      UICategoryForm uiForm = event.getSource() ;
      uiForm.setRenderSibbling(UIDescription.class) ;
    }
  }
  
}
