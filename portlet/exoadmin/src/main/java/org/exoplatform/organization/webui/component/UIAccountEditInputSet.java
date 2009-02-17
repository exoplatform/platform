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
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.ExpressionValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.ResourceValidator;
import org.exoplatform.webui.form.validator.SpecialCharacterValidator;
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
                   setEditable(false).
                   addValidator(MandatoryValidator.class).
                   addValidator(StringLengthValidator.class, 3, 30).
                   addValidator(ResourceValidator.class).
                   addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\-\\d]+$", "ResourceValidator.msg.Invalid-char"));
    addUIFormInput(new UIFormStringInput("firstName", "firstName", null).setMaxLength(45).
						    	 addValidator(StringLengthValidator.class, 3, 30).
						       addValidator(MandatoryValidator.class).
						       addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{ASCII}]+$", "FirstCharacterNameValidator.msg").
						       addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\- \\d]+$", "ResourceValidator.msg.Invalid-char")) ;
    addUIFormInput(new UIFormStringInput("lastName", "lastName", null).setMaxLength(45).
    							 addValidator(StringLengthValidator.class, 3, 30).
    							 addValidator(MandatoryValidator.class).
    							 addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{ASCII}]+$", "FirstCharacterNameValidator.msg").
    							 addValidator(ExpressionValidator.class, "^[\\p{L}][\\p{L}._\\- \\d]+$", "ResourceValidator.msg.Invalid-char")) ;
    addUIFormInput(new UIFormStringInput("email", "email", null). 
                   addValidator(MandatoryValidator.class).
                   addValidator(EmailAddressValidator.class));    
    UIFormCheckBoxInput<Boolean> uiCheckbox = new UIFormCheckBoxInput<Boolean>(CHANGEPASS,null,false) ;
                   uiCheckbox.setOnChange("ToggleChangePassword", "UIUserInfo") ;
    addUIFormInput(uiCheckbox) ;
    UIFormInputBase<String> uiInput = new UIFormStringInput(PASSWORD1X, null, null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(StringLengthValidator.class, 6,30).
                   addValidator(MandatoryValidator.class) ;
                   uiInput.setRendered(false) ;
    addUIFormInput(uiInput) ;
    uiInput = new UIFormStringInput(PASSWORD2X, null, null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(MandatoryValidator.class).
                   addValidator(StringLengthValidator.class, 6, 30);
                   uiInput.setRendered(false) ;
    addUIFormInput(uiInput) ;
  }
  
  public String getUserName(){ return getUIStringInput(USERNAME).getValue(); }
  
  public String getPropertyPrefix() { return "UIAccountForm" ; }
  
  public void setValue(User user) throws Exception  {
    if(user == null) return ;    
    invokeGetBindingField(user);
  }
  
  public boolean save(OrganizationService service) throws Exception { 
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    UIApplication uiApp = context.getUIApplication() ;
    String username = getUIStringInput(USERNAME).getValue() ;
    User user = service.getUserHandler().findUserByName(username) ;
    invokeSetBindingField(user) ;
    if(isChangePassword()) {
      String pass1x = getUIStringInput(PASSWORD1X).getValue();
      String pass2x = getUIStringInput(PASSWORD2X).getValue();
      if (!pass1x.equals(pass2x)){
        uiApp.addMessage(new ApplicationMessage("UIAccountForm.msg.password-is-not-match", null)) ;
        return false ;
      }      
      user.setPassword(pass1x) ;
    }
    service.getUserHandler().saveUser(user, true) ;
    enableChangePassword(false) ;
    return true;
  }
  
  public boolean isChangePassword() {
    return getUIFormCheckBoxInput(UIAccountEditInputSet.CHANGEPASS).isChecked() ; 
  }
  
  public void enableChangePassword(boolean enable) {
    getUIFormCheckBoxInput(UIAccountEditInputSet.CHANGEPASS).setChecked(enable) ;
    checkChangePassword() ;
  }
  
  public void checkChangePassword() {
    UIFormStringInput password1 = getUIStringInput(UIAccountEditInputSet.PASSWORD1X) ;
    UIFormStringInput password2 = getUIStringInput(UIAccountEditInputSet.PASSWORD2X) ;
    boolean isChange = isChangePassword() ;
    ((UIFormStringInput)password1.setValue(null)).setRendered(isChange);
    ((UIFormStringInput)password2.setValue(null)).setRendered(isChange);
  }
  
}
