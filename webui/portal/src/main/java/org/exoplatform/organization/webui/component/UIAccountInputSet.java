/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmailAddressValidator;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 28, 2006
 */
public class UIAccountInputSet extends UIFormInputSet {
  
  final static String USERNAME = "username" ;
  final static String PASSWORD1X = "password" ;
  final static String PASSWORD2X = "Confirmpassword" ;
  
  public UIAccountInputSet(String name) throws Exception {
    super(name);
    addUIFormInput(new UIFormStringInput(USERNAME, "userName", null).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(IdentifierValidator.class));
    addUIFormInput(new UIFormStringInput(PASSWORD1X, "password", null).
                   setType(UIFormStringInput.PASSWORD_TYPE).
                   addValidator(EmptyFieldValidator.class)) ;
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
  }
  
  public String getUserName(){ return getUIStringInput(USERNAME).getValue(); }
  
  public String getPropertyPrefix() { return "UIAccountForm" ; }
  
  public void setValue(User user) throws Exception  {
    if(user == null) return ;    
    invokeGetBindingField(user);
    getUIStringInput(USERNAME).setEditable(false) ;
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
    service.getUserHandler().saveUser(user, true) ;
    return true;
  }

}
