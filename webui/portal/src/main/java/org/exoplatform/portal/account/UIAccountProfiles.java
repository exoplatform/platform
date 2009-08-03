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
package org.exoplatform.portal.account;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.ExpressionValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.ResourceValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 */

@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",

    events = {
        @EventConfig(listeners = UIAccountProfiles.SaveActionListener.class),
        @EventConfig(listeners = UIAccountProfiles.ResetActionListener.class, phase = Phase.DECODE)
      }
)

public class UIAccountProfiles extends UIForm {
  
  public UIAccountProfiles() throws Exception {
    super();
    String username = Util.getPortalRequestContext().getRemoteUser() ;
    OrganizationService service = this.getApplicationComponent(OrganizationService.class);
    User useraccount = service.getUserHandler().findUserByName(username);
    
    UIFormStringInput userName  = new UIFormStringInput("userName","userName", username) ;
    userName.setEditable(false) ;
    addUIFormInput(userName.
                   addValidator(MandatoryValidator.class).
                   addValidator(StringLengthValidator.class, 3, 30).
                   addValidator(ResourceValidator.class).
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\-\\d]+$", "ResourceValidator.msg.Invalid-char"));
    addUIFormInput(new UIFormStringInput("firstName", "firstName", useraccount.getFirstName()).
                   addValidator(StringLengthValidator.class, 3, 45).
                   addValidator(MandatoryValidator.class).
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{ASCII}]+$", "FirstCharacterNameValidator.msg").
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\- \\d]+$", "ResourceValidator.msg.Invalid-char")) ;
    addUIFormInput(new UIFormStringInput("lastName", "lastName", useraccount.getLastName()).
                   addValidator(StringLengthValidator.class, 3, 45).
                   addValidator(MandatoryValidator.class).
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{ASCII}]+$", "FirstCharacterNameValidator.msg").
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\- \\d]+$", "ResourceValidator.msg.Invalid-char")) ;
    addUIFormInput(new UIFormStringInput("email", "email", useraccount.getEmail()). 
            addValidator(MandatoryValidator.class).
            addValidator(EmailAddressValidator.class)) ;
  }
  
  static  public class ResetActionListener extends EventListener<UIAccountProfiles> {
    public void execute(Event<UIAccountProfiles> event) throws Exception {
      UIAccountProfiles uiForm = event.getSource() ;
      String userName = uiForm.getUIStringInput("userName").getValue() ;
      OrganizationService service =  uiForm.getApplicationComponent(OrganizationService.class);
      User user = service.getUserHandler().findUserByName(userName) ; 
      uiForm.getUIStringInput("firstName").setValue(user.getFirstName()) ;
      uiForm.getUIStringInput("lastName").setValue(user.getLastName()) ;
      uiForm.getUIStringInput("email").setValue(user.getEmail()) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
    }
  }
  
  static  public class SaveActionListener extends EventListener<UIAccountProfiles> {
    public void execute(Event<UIAccountProfiles> event) throws Exception {
      UIAccountProfiles uiForm = event.getSource();
      OrganizationService service =  uiForm.getApplicationComponent(OrganizationService.class);
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
      UIApplication uiApp = context.getUIApplication() ;
     
      String userName = uiForm.getUIStringInput("userName").getValue() ;
      User user = service.getUserHandler().findUserByName(userName) ;    
      user.setFirstName(uiForm.getUIStringInput("firstName").getValue()) ;
      user.setLastName(uiForm.getUIStringInput("lastName").getValue()) ;
      user.setEmail(uiForm.getUIStringInput("email").getValue()) ;
      uiApp.addMessage(new ApplicationMessage("UIAccountProfiles.msg.update.success", null)) ;
      service.getUserHandler().saveUser(user, true) ;
      return;
    }
  }
}
