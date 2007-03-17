/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.application.ApplicationMessage;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.validator.EmailAddressValidator;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.IdentifierValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 28, 2006
 */
public class UIAccountInputSet extends UIFormInputSet {
  
  final static String USERNAME = "username" ;
  final static String PASSWORD1X = "password1x" ;
  final static String PASSWORD2X = "password2x" ;
  
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
                   addValidator(EmailAddressValidator.class));    
    
  }
  
  public String getUserName(){ return getUIStringInput(USERNAME).getValue(); }
  
  public String getPropertyPrefix() { return "UIAccountForm" ; }
  
  public void setValue(User user) throws Exception  {
    if(user == null) return ;    
    invokeGetBindingField(user);
    getUIStringInput(USERNAME).setEditable(false) ;
  }
  
  public void save(OrganizationService service, boolean newUser) throws Exception { 
    RequestContext context = RequestContext.getCurrentInstance() ;
    UIApplication uiApp = context.getUIApplication() ;
    String pass1x = getUIStringInput(PASSWORD1X).getValue();
    String pass2x = getUIStringInput(PASSWORD2X).getValue();
    if (!pass1x.equals(pass2x)){
      uiApp.addMessage(new ApplicationMessage("UIAccountForm.msg.password-is-not-match", null)) ;
      return ;
    }
    
    String username = getUIStringInput(USERNAME).getValue() ;
    if(newUser) {
      User user = service.getUserHandler().createUserInstance(username) ;
      invokeSetBindingField(user) ;
      if(service.getUserHandler().findUserByName(user.getUserName()) != null) {
        Object[] args = {user.getUserName()} ;
        uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.user-exist", args)) ;
        return ;
      }      
      
      service.getUserHandler().createUser(user, true);
      Object[] args = {user.getUserName()} ;
      uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.successful.create.user", args)) ;
      reset();
      return ;
    }     
    User user = service.getUserHandler().findUserByName(username) ;    
    invokeSetBindingField(user) ;   
    service.getUserHandler().saveUser(user, true) ;
    Object[] args = {user.getUserName()} ;
    uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.successful.update.user", args)) ;
  }

}
