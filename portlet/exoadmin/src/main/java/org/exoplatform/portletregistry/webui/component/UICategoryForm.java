/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
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
  
  final static public String FIELD_NAME = "name" ;
  final static public String FIELD_DESCRIPTION = "description" ;
  
  private PortletCategory selectedCategory = null ;
  
  public UICategoryForm() throws Exception {
    addUIFormInput(new UIFormStringInput(FIELD_NAME, "portletCategoryName", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormTextAreaInput(FIELD_DESCRIPTION, "description", null)); 
  } 
  
  public void setValue(PortletCategory category) throws Exception {
    if(category == null) {      
      getUIStringInput(FIELD_NAME).setEditable(true).setValue(null);
      getUIStringInput(FIELD_DESCRIPTION).setValue(null);
      return ;
    }
    getUIStringInput(FIELD_NAME).setEditable(false).setValue(category.getPortletCategoryName());
    getUIStringInput(FIELD_DESCRIPTION).setValue(category.getDescription());
    selectedCategory = category ;
  }
  
  public PortletCategory getSelectedCategory() { return selectedCategory ; }
  
  static public class SaveActionListener extends EventListener<UICategoryForm> {
    public void execute(Event<UICategoryForm> event) throws Exception{
      UICategoryForm uiForm = event.getSource() ;
      UIWorkingArea workingArea = uiForm.getParent() ;
      UIPortletRegistryPortlet uiRegistryPortlet = workingArea.getParent() ;
      UIPortletRegistryCategory uiRegistryCategory = uiRegistryPortlet.getChild(UIPortletRegistryCategory.class) ;

      PortletRegisteryService service = uiForm.getApplicationComponent(PortletRegisteryService.class);
      String description = uiForm.getUIStringInput(UICategoryForm.FIELD_DESCRIPTION).getValue() ;
      
      PortletCategory category = uiForm.getSelectedCategory() ;
      uiRegistryCategory.initValues(null) ;

      if(category != null) {
        category.setDescription(description);
        service.updatePortletCategory(category);
        return;
      } 
      category = service.createPortletCategoryInstance() ;
      uiForm.invokeSetBindingBean(category) ;
      String name = category.getPortletCategoryName();
      try{
        PortletCategory oldCategory = service.findPortletCategoryByName(name);
        if(oldCategory != null){
          oldCategory.setDescription(description);
          service.updatePortletCategory(oldCategory);
          return;
        }
      }catch (Exception e) {
      }
      service.addPortletCategory(category) ;
      uiRegistryCategory.getPortletCategory().add(category);
      uiRegistryCategory.setSelectedCategory(category);
      uiForm.reset();
    }
  }
  
  static public class BackActionListener extends EventListener<UICategoryForm>{
    public void execute(Event<UICategoryForm> event) throws Exception{
      UICategoryForm uiForm = event.getSource() ;
      uiForm.setRenderSibbling(UIDescription.class) ;
    }
  }
  
}
