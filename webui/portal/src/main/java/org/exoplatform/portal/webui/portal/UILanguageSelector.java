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
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIItemSelector;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
    template = "system:/groovy/portal/webui/portal/UILanguageSelector.gtmpl",        
    events = {
      @EventConfig(listeners = UILanguageSelector.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UILanguageSelector extends UIContainer {
  private String name_;

  @SuppressWarnings("unchecked")
  public UILanguageSelector() throws Exception  { 
    name_ = "UIChangeLanguage";    
    LocaleConfigService configService = getApplicationComponent(LocaleConfigService.class) ;
    Locale currentLocale = Util.getUIPortal().getAncestorOfType(UIPortalApplication.class).getLocale() ; ;    
    SelectItemOption localeItem = null ;
    List<SelectItemOption> optionsList = new ArrayList<SelectItemOption>() ;
    
    for(Object object:configService.getLocalConfigs()) {      
      LocaleConfig localeConfig = (LocaleConfig)object ;
      Locale locale = localeConfig.getLocale() ;
      String displayName = locale.getDisplayLanguage(currentLocale) ;
      String lang = locale.getLanguage() ;
      String localedName = locale.getDisplayName(locale) ;            
      if(localedName == null || localedName.length() == 0 ) localedName = "???" ;           
      if(locale.getDisplayName().equalsIgnoreCase(currentLocale.getDisplayName())) {
        localeItem = new SelectItemOption(displayName,lang,localedName,"",true) ;         
      }else {
        localeItem = new SelectItemOption(displayName,lang,localedName,"") ;
      }
      optionsList.add(localeItem) ;      
    }
    //TODO need use other UIComponent here
    Collections.sort(optionsList, new LanguagesComparator()) ;    
    List<SelectItemCategory> contientsCategories = new ArrayList<SelectItemCategory>() ;
    SelectItemCategory category = new SelectItemCategory("Languages") ;
    category.setSelectItemOptions(optionsList) ;
    contientsCategories.add(category) ;    
    UIItemSelector selector = new UIItemSelector("Language");    
    selector.setItemCategories(contientsCategories );   
    selector.setRendered(true);
    addChild(selector);
  }
    
  public String getName() { return name_; }
  
  private class LanguagesComparator implements Comparator<SelectItemOption> {      
    public int compare(SelectItemOption item0, SelectItemOption item1) {
      return item0.getLabel().compareToIgnoreCase(item1.getLabel()) ;      
    }    
  }  
  
  static public class SaveActionListener  extends EventListener<UILanguageSelector> {
    public void execute(Event<UILanguageSelector> event) throws Exception {
      String language  = event.getRequestContext().getRequestParameter("language");

      UIPortalApplication uiApp = Util.getUIPortalApplication() ;
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      //event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS) ;
      Util.getPortalRequestContext().setFullRender(false) ;
      if(language == null || language.trim().length() < 1) return;       
      //if(!uiPortal.isModifiable()) return;

      LocaleConfigService localeConfigService  = event.getSource().getApplicationComponent(LocaleConfigService.class) ;
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(language);
      if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
      uiApp.setLocale(localeConfig.getLocale());
      UIPortal uiPortal = uiApp.findFirstComponentOfType(UIPortal.class) ;
      uiPortal.setLocale(localeConfig.getLanguage()) ;
      uiPortal.refreshNavigation() ;
      OrganizationService orgService = event.getSource().getApplicationComponent(OrganizationService.class) ;
      String remoteUser = event.getRequestContext().getRemoteUser() ;
      if(remoteUser != null) {
        UserProfile userProfile = orgService.getUserProfileHandler().findUserProfileByName(remoteUser) ;
        userProfile.getUserInfoMap().put("user.language", language) ;
      }
    }
 }
}