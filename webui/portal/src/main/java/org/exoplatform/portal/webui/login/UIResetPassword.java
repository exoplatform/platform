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
package org.exoplatform.portal.webui.login;

import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
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
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Jul 09, 2008
 */
@ComponentConfig(  
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = UIResetPassword.SaveActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class)
  }
)
public class UIResetPassword extends UIForm {
  final static String USER_NAME = "username";
  final static String PASSWORD = "password";
  final static String NEW_PASSWORD = "newpassword";
  final static String CONFIRM_NEW_PASSWORD = "confirmnewpassword";
  static User user_;

  public UIResetPassword() throws Exception{
    addUIFormInput(new UIFormStringInput(USER_NAME,USER_NAME,null).setEditable(false));
    addUIFormInput(new UIFormStringInput(PASSWORD,PASSWORD,null).setType(UIFormStringInput.PASSWORD_TYPE)    					
                        .addValidator(MandatoryValidator.class));                        
    addUIFormInput(((UIFormStringInput)new UIFormStringInput(NEW_PASSWORD,NEW_PASSWORD,null)).setType(UIFormStringInput.PASSWORD_TYPE)    					
                        .addValidator(MandatoryValidator.class)
                        .addValidator(StringLengthValidator.class, 6, 30));
    addUIFormInput(((UIFormStringInput)new UIFormStringInput(CONFIRM_NEW_PASSWORD,CONFIRM_NEW_PASSWORD,null)).setType(UIFormStringInput.PASSWORD_TYPE)
                        .addValidator(MandatoryValidator.class)
                        .addValidator(StringLengthValidator.class, 6, 30));                        
  }
  
  public void setData(User user) {
    user_ = user;
    getUIStringInput(USER_NAME).setValue(user.getUserName());
  }
  
  @Override
  public void reset() {
    UIFormStringInput passwordForm = getUIStringInput(PASSWORD);
    passwordForm.reset();
    UIFormStringInput newPasswordForm = getUIStringInput(NEW_PASSWORD);
    newPasswordForm.reset();
    UIFormStringInput confirmPasswordForm = getUIStringInput(CONFIRM_NEW_PASSWORD);
    confirmPasswordForm.reset();
  }
  
  static public class SaveActionListener  extends EventListener<UIResetPassword> {
    public void execute(Event<UIResetPassword> event) throws Exception {
      UIResetPassword uiForm = event.getSource();
      String password = uiForm.getUIStringInput(PASSWORD).getValue();
      String newpassword = uiForm.getUIStringInput(NEW_PASSWORD).getValue();
      String confirmnewpassword = uiForm.getUIStringInput(CONFIRM_NEW_PASSWORD).getValue();
      WebuiRequestContext request = event.getRequestContext();
      UIApplication uiApp = request.getUIApplication();
      UIMaskWorkspace uiMaskWorkspace = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      OrganizationService orgService = uiForm.getApplicationComponent(OrganizationService.class);
      uiForm.reset();
      boolean isNew = true;
      if(!password.equals(user_.getPassword())) {
        uiApp.addMessage(new ApplicationMessage("UIResetPassword.msg.Invalid-account", null));
        isNew = false;
      }
      if(!newpassword.equals(confirmnewpassword)) {
        uiApp.addMessage(new ApplicationMessage("UIResetPassword.msg.password-is-not-match", null));
        isNew = false;
      }
      if(isNew){
        user_.setPassword(newpassword);
        orgService.getUserHandler().saveUser(user_,true);
        uiMaskWorkspace.setUIComponent(null);
        uiMaskWorkspace.setWindowSize(-1, -1);
        uiApp.addMessage(new ApplicationMessage("UIResetPassword.msg.change-password-successfully", null));
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace) ;
   }
  }
}
