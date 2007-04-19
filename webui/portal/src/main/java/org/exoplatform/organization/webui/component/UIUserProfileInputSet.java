/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormInput;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 28, 2006
 */
@ComponentConfig( template = "system:/groovy/webui/component/UIVTabInputSet.gtmpl" )
public class UIUserProfileInputSet extends UIFormInputSet {
  
  private String user_ ;
  
  public UIUserProfileInputSet(String name) throws Exception {
    super(name);
    setComponentConfig(UIUserProfileInputSet.class, null) ;
    
    UIFormInputSet personalInputSet = new UIFormInputSet("Profile") ;
    addInput(personalInputSet, UserProfile.PERSONAL_INFO_KEYS) ;
    addUIFormInput(personalInputSet);
        
    UIFormInputSet homeInputSet = new UIFormInputSet("HomeInfo") ;
    addInput(homeInputSet, UserProfile.HOME_INFO_KEYS) ;
    homeInputSet.setRendered(false) ;
    addUIFormInput(homeInputSet);
    
    UIFormInputSet businessInputSet = new UIFormInputSet("BusinessInfo") ;
    addInput(businessInputSet, UserProfile.BUSINESE_INFO_KEYS) ;
    businessInputSet.setRendered(false) ;
    addUIFormInput(businessInputSet);
  }
  
  public void reset(){
    for(UIComponent uiChild : getChildren()){
      if(uiChild instanceof UIFormInputSet || uiChild instanceof UIFormInput){
        ((UIFormInputSet)uiChild).reset();
      }
    }
  }
  
  private void addInput(UIFormInputSet set, String[] keys) {
    
    for(String key : keys) {
      if(key.equalsIgnoreCase("user.gender")){
        List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
        ls.add(new SelectItemOption<String>("Male", "male")) ;
        ls.add(new SelectItemOption<String>("Female", "female")) ;;
        UIFormSelectBox genderSelectBox = new UIFormSelectBox(key, key, ls);
        set.addUIFormInput(genderSelectBox);  
        continue;
      }
      set.addUIFormInput( new UIFormStringInput(key, null, null)) ;
    }
  }  
  
  @SuppressWarnings("deprecation")
  public void setUserProfile(String user) throws Exception { 
    user_ = user ;
    if(user == null) return ;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    UserProfile userProfile = service.getUserProfileHandler().findUserProfileByName(user) ;
    if(userProfile == null) {
      userProfile = service.getUserProfileHandler().createUserProfileInstance() ;
      userProfile.setUserName(user);
    }    
    
    if(userProfile.getUserInfoMap() == null) return;
    for(UIComponent set : getChildren())  {
      UIFormInputSet inputSet = (UIFormInputSet) set ;
      for(UIComponent uiComp : inputSet.getChildren()) {
        UIFormStringInput uiInput = (UIFormStringInput) uiComp ;        
        uiInput.setValue(userProfile.getAttribute(uiInput.getName())) ;
      }
    }
  }
  
  @SuppressWarnings("deprecation")
  public void save(OrganizationService service, String user, boolean isnewUser) throws Exception {
    user_ = user;
    UserProfileHandler hanlder = service.getUserProfileHandler();    
    UserProfile userProfile = hanlder.findUserProfileByName(user_);   
    
    if(userProfile == null){
      userProfile = hanlder.createUserProfileInstance();
      userProfile.setUserName(user_);
    }
    
    for(UIComponent set : getChildren())  {
      UIFormInputSet inputSet = (UIFormInputSet) set ;
      for(UIComponent uiComp : inputSet.getChildren()) {
        UIFormStringInput uiInput = (UIFormStringInput) uiComp ;
        if(uiInput.getValue() == null || uiInput.getValue().length() < 1) continue;
        userProfile.getUserInfoMap().put(uiInput.getName(),uiInput.getValue());
      }
    }
    
    hanlder.saveUserProfile(userProfile, true) ;
    
    Object[] args = {"UserProfile", user_} ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    UIApplication uiApp = context.getUIApplication() ;
    if(isnewUser) {
      uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.successful.create.user", args)) ;
      return ;
    }
    uiApp.addMessage(new ApplicationMessage("UIUserProfileInputSet.msg.sucsesful.update.userprofile", args)) ;
  }
  
}
