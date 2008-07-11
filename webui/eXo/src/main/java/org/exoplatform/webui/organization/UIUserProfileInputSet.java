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
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.organization.UserProfileHandler;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 28, 2006
 */
@ComponentConfig( template = "system:/groovy/webui/form/UIVTabInputSet.gtmpl" )
public class UIUserProfileInputSet extends UIFormInputSet {
  
  private String user_ ;
  public static String MALE = "male";
  public static String FEMALE = "female";
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
        ls.add(new SelectItemOption<String>(MALE, MALE)) ;
        ls.add(new SelectItemOption<String>(FEMALE, FEMALE)) ;;
        UIFormSelectBox genderSelectBox = new UIFormSelectBox(key, key, ls);
        set.addUIFormInput(genderSelectBox);  
        continue;
      }else if(key.equalsIgnoreCase("user.language")){
        List<SelectItemOption<String>> lang = new ArrayList<SelectItemOption<String>>() ;
        LocaleConfigService localeService = getApplicationComponent(LocaleConfigService.class) ;
        Iterator i = localeService.getLocalConfigs().iterator() ;
        while (i.hasNext()) {
          LocaleConfig config = (LocaleConfig) i.next() ;
          if(config.getLanguage().equals("en")) {
            lang.add(0,new SelectItemOption<String>(config.getLocale().getDisplayLanguage(), config.getLanguage()));
            continue;
          }
          lang.add(new SelectItemOption<String>(config.getLocale().getDisplayLanguage(), config.getLanguage()))  ;
        }
        UIFormSelectBox langSelectBox = new UIFormSelectBox(key, key, lang);
        set.addUIFormInput(langSelectBox);  
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
