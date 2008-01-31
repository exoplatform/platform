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
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

/**
 * Created by The eXo Platform SARL
 * Author : dang.tung
 *          tungcnw@gmail.com
 * Jun 25, 2008
 */
public class UIAccountEditInputSet extends UIFormInputSet {
  
  final static String USERNAME = "userName" ;
  final static String PASSWORD1X = "newPassword" ;
  final static String PASSWORD2X = "confirmPassword" ;
  final static String CHANGEPASS = "changePassword" ;
  public UIAccountEditInputSet(String name) throws Exception {
    super(name);
    addUIFormInput(new UIFormStringInput(USERNAME, "userName", null).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(IdentifierValidator.class));
    addUIFormInput(new UIFormStringInput(PASSWORD1X, "password", null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(StringLengthValidator.class, 6,30)) ;
    addUIFormInput(new UIFormStringInput(PASSWORD2X, "password", null).
                  setType(UIFormStringInput.PASSWORD_TYPE).
                  addValidator(EmptyFieldValidator.class)) ;
    addUIFormInput(new UIFormStringInput("firstName", "firstName", null).
                   addValidator(EmptyFieldValidator.class)) ;
    addUIFormInput(new UIFormStringInput("lastName", "lastName", null).
                   addValidator(EmptyFieldValidator.class)) ;
    addUIFormInput(new UIFormStringInput("email", "email", null). 
                   addValidator(EmptyFieldValidator.class).
                   addValidator(EmailAddressValidator.class));    
    UIFormCheckBoxInput<Boolean> uiCheckbox = new UIFormCheckBoxInput<Boolean>("changePassword",null,false) ;
    uiCheckbox.setOnChange("ChangePassword", "UIUserInfo") ;
    addUIFormInput(uiCheckbox) ;
  }
  
  public String getUserName(){ return getUIStringInput(USERNAME).getValue(); }
  
  public String getPropertyPrefix() { return "UIAccountForm" ; }
  
  public void setValue(User user) throws Exception  {
    if(user == null) return ;    
    invokeGetBindingField(user);
    getUIFormCheckBoxInput(CHANGEPASS).setChecked(false) ;
    getUIStringInput(USERNAME).setEditable(false) ;
    getUIStringInput(PASSWORD1X).setRendered(false) ;
    getUIStringInput(PASSWORD2X).setRendered(false) ;
  }
  
  public boolean save(OrganizationService service, boolean newUser) throws Exception { 
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    UIApplication uiApp = context.getUIApplication() ;
    String pass1x = getUIStringInput(PASSWORD1X).getValue();
    String pass2x = getUIStringInput(PASSWORD2X).getValue();
    if (!pass1x.equals(pass2x)){
      uiApp.addMessage(new ApplicationMessage("UIAccountForm.msg.password-is-not-match", null)) ;
      return false ;
    }
    String username = getUIStringInput(USERNAME).getValue() ;
    if(newUser) {
      User user = service.getUserHandler().createUserInstance(username) ;
      invokeSetBindingField(user) ;
      //user.setPassword(Util.encodeMD5(pass1x)) ;
      if(service.getUserHandler().findUserByName(user.getUserName()) != null) {
        Object[] args = {user.getUserName()} ;
        uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.user-exist", args)) ;
        return false;
      }      
      
      service.getUserHandler().createUser(user, true);
      reset();
      return true;
    }     
    User user = service.getUserHandler().findUserByName(username) ;
    invokeSetBindingField(user) ;
//    user.setPassword(Util.encodeMD5(pass1x)) ;
    service.getUserHandler().saveUser(user, true) ;
    getUIFormCheckBoxInput("changePassword").setChecked(false) ;
    setValue(user) ;
    return true;
  }
}
