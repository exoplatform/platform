/************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.                            *
 *                                                                      *
 * This program is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Affero General Public License   *
 * as published by the Free Software Foundation; either version 3       *
 * of the License, or (at your option) any later version.               *
 *                                                                      *
 * This program is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of       *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        *
 * GNU General Public License for more details.                         *
 *                                                                      *
 * You should have received a copy of the GNU General Public License    *
 * along with this program; if not, see<http://www.gnu.org/licenses/>.  *
 ************************************************************************/
package org.exoplatform.applicationregistry.webui.component;

import java.util.ArrayList;
import java.util.Date;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector.EmptyIteratorValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Hoa Nguyen
 *          hoa.nguyen@exoplatform.com
 * Jul 4, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIFormTabPane.gtmpl",
    events = {
      @EventConfig(listeners = UICategoryForm.SaveActionListener.class),
      @EventConfig(listeners = UICategoryForm.CancelActionListener.class, phase = Phase.DECODE)
    }
)
public class UICategoryForm extends UIFormTabPane { 

  final static private String FIELD_NAME = "name" ;
  final static private String FIELD_DISPLAY_NAME = "displayName" ;
  final static private String FIELD_DESCRIPTION = "description" ;
  final static private String FIELD_SETTING= "categorySetting" ;
  final static private String FIELD_PERMISSION="categoryPermission" ;

  private ApplicationCategory category_ = null ;

  public UICategoryForm() throws Exception {
    super("UICategoryForm") ;
    UIFormInputSet uiCategorySetting = new UIFormInputSet(FIELD_SETTING) ;
    uiCategorySetting.addUIFormInput(new UIFormStringInput(FIELD_NAME, FIELD_NAME, null).
                     addValidator(MandatoryValidator.class).
                     addValidator(StringLengthValidator.class, 3, 30).
                     addValidator(IdentifierValidator.class));
    uiCategorySetting.addUIFormInput(new UIFormStringInput(FIELD_DISPLAY_NAME, FIELD_DISPLAY_NAME, null).
                     addValidator(StringLengthValidator.class, 3, 30));
    uiCategorySetting.addUIFormInput(new UIFormTextAreaInput(FIELD_DESCRIPTION, FIELD_DESCRIPTION, null).
                     addValidator(StringLengthValidator.class, 0, 255));
    addChild(uiCategorySetting) ;
    setSelectedTab(uiCategorySetting.getId()) ;
    
    UIFormInputSet uiPermissionSetting = new UIFormInputSet(FIELD_PERMISSION) ;
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiListPermissionSelector.addValidator(EmptyIteratorValidator.class) ;
    uiPermissionSetting.addChild(uiListPermissionSelector);
    addUIComponentInput(uiPermissionSetting) ;
  } 

  public void setValue(ApplicationCategory category) throws Exception {
    UIFormInputSet uiSetting = getChildById(FIELD_SETTING) ;
    UIFormInputSet uiPermission = getChildById(FIELD_PERMISSION) ;
    uiSetting.reset() ;
    uiPermission.getChild(UIListPermissionSelector.class).setValue(new String[] {}) ;
    setSelectedTab(uiSetting.getId()) ;
    if(category == null) {
      category_ = null;
      uiSetting.getUIStringInput(FIELD_NAME).setEditable(true) ;
      return ;
    }
    category_ = category ;
    uiSetting.getUIStringInput(FIELD_NAME).setEditable(false).setValue(category_.getName()) ;
    uiSetting.getUIStringInput(FIELD_DISPLAY_NAME).setValue(category_.getDisplayName()) ;
    uiSetting.getUIStringInput(FIELD_DESCRIPTION).setValue(category_.getDescription()) ;
    ArrayList<String> accessPermissions = category_.getAccessPermissions() ;
    String[] per = new String[accessPermissions.size()];
    if (accessPermissions != null && accessPermissions.size() > 0) {
      uiPermission.getChild(UIListPermissionSelector.class).setValue(accessPermissions.toArray(per)) ;
    }
  }

  public ApplicationCategory getCategory() { return category_ ; }

  static public class SaveActionListener extends EventListener<UICategoryForm> {
    public void execute(Event<UICategoryForm> event) throws Exception{
      UICategoryForm uiForm = event.getSource() ;
      UIApplicationOrganizer uiOrganizer = uiForm.getParent() ;
      UIFormInputSet uiSetting = uiForm.getChildById(FIELD_SETTING) ;
      UIFormInputSet uiPermission = uiForm.getChildById(FIELD_PERMISSION) ;
      ApplicationRegistryService service = uiForm.getApplicationComponent(ApplicationRegistryService.class);

      ApplicationCategory category = uiForm.getCategory() ;
      if(category == null) category = new ApplicationCategory();
      category.setName(uiSetting.getUIStringInput(FIELD_NAME).getValue()) ;
      String displayName = uiSetting.getUIStringInput(FIELD_DISPLAY_NAME).getValue();
      if(displayName == null || displayName.length() < 1 ) category.setDisplayName(category.getName());
      else category.setDisplayName(displayName) ;
      category.setDescription(uiSetting.getUIStringInput(FIELD_DESCRIPTION).getValue()) ;
      
      UIListPermissionSelector uiListPermissionSelector = uiPermission.getChild(UIListPermissionSelector.class) ;
      ArrayList<String> pers = new ArrayList<String>();
      if(uiListPermissionSelector.getValue()!= null)
      for(String per: uiListPermissionSelector.getValue()) pers.add(per);
      category.setAccessPermissions(pers) ;
      if(category == uiForm.getCategory()) {
        category.setModifiedDate(new Date()) ;
      }else {
        ApplicationCategory existCategory = service.getApplicationCategory(category.getName()); 
        if(existCategory != null) {
          UIApplication uiApp = event.getRequestContext().getUIApplication() ;
          uiApp.addMessage(new ApplicationMessage("UICategoryForm.msg.SameName", null)) ;
          return ;
        }
        category.setModifiedDate(new Date()) ;
        category.setCreatedDate(new Date()) ;
      }
      service.save(category) ;
      uiForm.setValue(null) ;
      uiOrganizer.initApplicationCategories();
      uiOrganizer.setSelectedCategory(category.getName());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer) ;
    }
  }
  
  static public class CancelActionListener extends EventListener<UICategoryForm> {

    public void execute(Event<UICategoryForm> event) throws Exception {
      UICategoryForm uiForm = event.getSource();
      UIApplicationOrganizer uiOrganizer = uiForm.getParent();
      Application application = uiOrganizer.getSelectedApplication();
      if(application != null) {
        uiOrganizer.setSelectedApplication(application);
      } else uiOrganizer.reload();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer);
    }
    
  }

}
