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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputItemSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIPermissionSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector.EmptyIteratorValidator;
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",     
    events = {
      @EventConfig(listeners = UIPortalForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
  ),
  @ComponentConfig(
    id = "CreatePortal",
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",
    initParams = @ParamConfig(
        name = "PortalTemplateConfigOption", 
        value = "system:/WEB-INF/conf/uiconf/portal/webui/portal/PortalTemplateConfigOption.groovy"
    ),
    events = {
      @EventConfig(name  = "Save", listeners = UIPortalForm.CreateActionListener.class),
      @EventConfig(listeners = UIPortalForm.SelectItemOptionActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PermissionSetting",
      template = "system:/groovy/webui/core/UITabSelector.gtmpl"
  )
})
public class UIPortalForm extends UIFormTabPane {

  private static final String FIELD_NAME = "name";
  private static final String FIELD_SKIN = "skin";
  private static final String FIELD_LOCALE = "locale";
  
  private List<SelectItemOption<String>> languages = new ArrayList<SelectItemOption<String>>() ;
  
  @SuppressWarnings("unchecked")
  public UIPortalForm(InitParams initParams) throws Exception {
    super("UIPortalForm");
    UIFormInputItemSelector uiTemplateInput = new  UIFormInputItemSelector("PortalTemplate", null);
    addUIFormInput(uiTemplateInput) ;
    setSelectedTab(uiTemplateInput.getId()) ;
    createDefaultItem();
    
    UIFormInputSet uiPortalSetting = this.<UIFormInputSet>getChildById("PortalSetting");
    UIFormStringInput uiNameInput = uiPortalSetting.getUIStringInput(FIELD_NAME);
    uiNameInput.setEditable(true);
    
    setActions(new String[]{"Save", "Close"});
    
    if(initParams == null) return;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("PortalTemplateConfigOption");
    List<SelectItemCategory> portalTemplates = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    for(SelectItemCategory itemCategory: portalTemplates){
      uiTemplateInput.getItemCategories().add(itemCategory);
    }
    if(uiTemplateInput.getSelectedItemOption() == null) {
      uiTemplateInput.getItemCategories().get(0).setSelected(true);
    }
  }

  public UIPortalForm() throws Exception {
    super("UIPortalForm");
    createDefaultItem();
    setSelectedTab("PortalSetting") ;
    invokeGetBindingBean(Util.getUIPortal()) ;
  }
  
  @SuppressWarnings("unchecked")
  private class LanguagesComparator implements Comparator<SelectItemOption> {

    public int compare(SelectItemOption o1, SelectItemOption o2) {
      return o1.getLabel().compareToIgnoreCase(o2.getLabel()) ;
    }
  }
  
  private void createDefaultItem() throws Exception {
    UIPortal uiPortal = Util.getUIPortal();
    LocaleConfigService localeConfigService  = getApplicationComponent(LocaleConfigService.class) ;
    Collection<?> listLocaleConfig = localeConfigService.getLocalConfigs() ;
    Locale currentLocate = Util.getPortalRequestContext().getLocale() ;
    Iterator<?> iterator = listLocaleConfig.iterator() ;
    while(iterator.hasNext()) {
      LocaleConfig localeConfig = (LocaleConfig) iterator.next() ;
      Locale locale = localeConfig.getLocale() ;
      SelectItemOption<String> option = new SelectItemOption<String>(
          localeConfig.getLocale().getDisplayName(currentLocate), localeConfig.getLanguage()) ;
      if(locale.getLanguage().equalsIgnoreCase(uiPortal.getLocale())){
        option.setSelected(true) ;
      }
      languages.add(option) ;
    }
    Collections.sort(languages, new LanguagesComparator()) ;
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PortalSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput(FIELD_NAME, FIELD_NAME, null).
                                addValidator(MandatoryValidator.class).
                                addValidator(StringLengthValidator.class, 3, 30).
                                addValidator(IdentifierValidator.class).
                                setEditable(false)).
                 addUIFormInput(new UIFormSelectBox(FIELD_LOCALE, FIELD_LOCALE, languages).
                                addValidator(MandatoryValidator.class));

    List<SelectItemOption<String>> listSkin = new ArrayList<SelectItemOption<String>>() ;
    SkinService skinService = getApplicationComponent(SkinService.class);
    for (String skin : skinService.getAvailableSkinNames()) {
      SelectItemOption<String> skinOption = new SelectItemOption<String>(skin, skin);
      if(uiPortal.getSkin().equals(skin)) skinOption.setSelected(true) ;
      listSkin.add(skinOption);
    }
    UIFormSelectBox uiSelectBox = new UIFormSelectBox(FIELD_SKIN, FIELD_SKIN, listSkin) ;
    uiSettingSet.addUIFormInput(uiSelectBox);
    addUIFormInput(uiSettingSet);
    
    
    UIFormInputSet uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    addUIComponentInput(uiPermissionSetting);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiListPermissionSelector.addValidator(EmptyIteratorValidator.class);
    uiPermissionSetting.addChild(uiListPermissionSelector);


    UIPermissionSelector uiEditPermission = createUIComponent(UIPermissionSelector.class, null, null);
    uiEditPermission.setRendered(false) ;
    uiEditPermission.addValidator(org.exoplatform.webui.organization.UIPermissionSelector.MandatoryValidator.class);
    uiEditPermission.configure("UIPermissionSelector", "editPermission");
    uiPermissionSetting.addChild(uiEditPermission);
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm  =  event.getSource();      
      UIPortal uiPortal = Util.getUIPortal();
      uiForm.invokeSetBindingBean(uiPortal);
//      uiPortal.refreshNavigation(localeConfigService.getLocaleConfig(uiPortal.getLocale()).getLocale()) ;
      UIMaskWorkspace uiMaskWorkspace = uiForm.getParent();
      uiMaskWorkspace.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);
    }
  }
  
  static public class CreateActionListener  extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm = event.getSource();
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      String template = uiForm.getChild(UIFormInputItemSelector.class).getSelectedItemOption().getValue().toString();
      String portalName = uiForm.getUIStringInput(FIELD_NAME).getValue();
      DataStorage dataService = uiForm.getApplicationComponent(DataStorage.class) ;
      PortalConfig config = dataService.getPortalConfig(portalName);
      if(config != null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortalForm.msg.sameName", null)) ;
        return;
      }
      
      UserPortalConfigService service = uiForm.getApplicationComponent(UserPortalConfigService.class);
      service.createUserPortalConfig(portalName, template);
      UserPortalConfig userPortalConfig = service.getUserPortalConfig(portalName, pcontext.getRemoteUser());
      PortalConfig pconfig = userPortalConfig.getPortalConfig();
      uiForm.invokeSetBindingBean(pconfig);
      PageNavigation navigation = service.getPageNavigation(PortalConfig.PORTAL_TYPE, portalName) ;
      navigation.setCreator(pcontext.getRemoteUser());
      pconfig.setCreator(pcontext.getRemoteUser());
      service.update(pconfig);
      service.update(navigation);
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;      
      UIPortalBrowser uiPortalBrowser = uiPortalApp.findFirstComponentOfType(UIPortalBrowser.class);
      //hoa.phamvu: in some case, the create portal action is call out side portal browser
      if(uiPortalBrowser != null) {
        uiPortalBrowser.loadPortalConfigs();
        pcontext.addUIComponentToUpdateByAjax(uiPortalBrowser); 
      }      
    }
  }
  
  static  public class SelectItemOptionActionListener extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm = event.getSource();
      UIFormInputItemSelector templateInput = uiForm.getChild(UIFormInputItemSelector.class);
      uiForm.setSelectedTab(templateInput.getId()) ;
      PortalTemplateConfigOption selectItem = 
        (PortalTemplateConfigOption)templateInput.getSelectedCategory().getSelectItemOptions().get(0);
      List<String> groupIds = selectItem.getGroups();
      Group [] groups = new Group[groupIds.size()];
      OrganizationService service = uiForm.getApplicationComponent(OrganizationService.class) ;
      for(int i = 0; i < groupIds.size(); i++) {
        groups[i] = service.getGroupHandler().findGroupById(groupIds.get(i));
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm);
    }
  }

}
