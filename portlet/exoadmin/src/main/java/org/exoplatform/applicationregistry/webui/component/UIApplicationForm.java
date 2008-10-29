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
package org.exoplatform.applicationregistry.webui.component;

import java.util.Calendar;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.NameValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Jul 28, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
    events = {
      @EventConfig(listeners = UIApplicationForm.SaveActionListener.class),
      @EventConfig(phase = Phase.DECODE, listeners = UIApplicationForm.CancelActionListener.class)
    }
)
public class UIApplicationForm extends UIForm {  

  private Application application_ ;
    
  public UIApplicationForm() throws Exception {
    addUIFormInput(new UIFormStringInput("applicationName", "applicationName", null).
                   addValidator(MandatoryValidator.class).
                   addValidator(StringLengthValidator.class, 3, 30).
                   addValidator(NameValidator.class)) ;
    addUIFormInput(new UIFormStringInput("displayName", "displayName", null).
                   addValidator(StringLengthValidator.class, 3, 30));
    addUIFormInput(new UIFormTextAreaInput("description", "description", null).
                   addValidator(StringLengthValidator.class, 0, 255));
  }
  
  public void setValues(Application app) throws Exception {
    application_ = app;
    if(application_ == null) {
      getUIStringInput("applicationName").setEditable(true);
      return ;
    }
    getUIStringInput("applicationName").setEditable(false);
    invokeGetBindingBean(app) ;
  }

  public Application getApplication() { return application_ ; }

  static public class SaveActionListener extends EventListener<UIApplicationForm> {
    public void execute(Event<UIApplicationForm> event) throws Exception{
      UIApplicationForm uiForm = event.getSource() ;
      UIApplicationOrganizer uiOrganizer = uiForm.getParent();
      ApplicationRegistryService service = uiForm.getApplicationComponent(ApplicationRegistryService.class) ;
      Application application = uiForm.getApplication() ;
      uiForm.invokeSetBindingBean(application);
      application.setModifiedDate(Calendar.getInstance().getTime());
      service.update(application) ;
      uiForm.setValues(null) ;
      uiOrganizer.setSelectedApplication(uiOrganizer.getSelectedApplication());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer);
    }
  }

  static public class CancelActionListener extends EventListener<UIApplicationForm>{
    public void execute(Event<UIApplicationForm> event) throws Exception{
      UIApplicationForm uiForm = event.getSource() ;
      UIApplicationOrganizer uiOrganizer = uiForm.getParent();
      uiForm.setValues(null) ;
      uiOrganizer.setSelectedApplication(uiOrganizer.getSelectedApplication());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiOrganizer);
    }
  }
  
}


