/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.skin.SkinService;
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
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.NameValidator;
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
        value = "app:/WEB-INF/conf/uiconf/portal/webui/portal/PortalTemplateConfigOption.groovy"
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

  private static final String SKIN = "skin";
  private List<SelectItemOption<String>> languages = new ArrayList<SelectItemOption<String>>() ;
  
  @SuppressWarnings("unchecked")
  public UIPortalForm(InitParams initParams) throws Exception {
    super("UIPortalForm");
    UIFormInputItemSelector uiTemplateInput = new  UIFormInputItemSelector("PortalTemplate", null);
    uiTemplateInput.setRendered(true) ;
    addUIFormInput(uiTemplateInput) ;
    
    createDefaultItem();
    
    UIFormInputSet uiPortalSetting = this.<UIFormInputSet>getChildById("PortalSetting");
    UIFormStringInput uiNameInput = uiPortalSetting.getUIStringInput("name");
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
    this.<UIFormInputSet>getChildById("PortalSetting").setRendered(true);
    invokeGetBindingBean(Util.getUIPortal()) ;
  }
  
  @SuppressWarnings("unchecked")
  private class languagesComparator implements Comparator<SelectItemOption> {

    public int compare(SelectItemOption o1, SelectItemOption o2) {
      return o1.getLabel().compareToIgnoreCase(o2.getLabel()) ;
    }
  }
  
  private void createDefaultItem() throws Exception {
    LocaleConfigService localeConfigService  = getApplicationComponent(LocaleConfigService.class) ;
    Collection<?> listLocaleConfig = localeConfigService.getLocalConfigs() ;

    Iterator<?> iterator = listLocaleConfig.iterator() ;
    while(iterator.hasNext()) {
      LocaleConfig localeConfig = (LocaleConfig) iterator.next() ;
      languages.add(new SelectItemOption<String>(localeConfig.getLocale().getDisplayName(), localeConfig.getLanguage())) ;
    }
    Collections.sort(languages, new languagesComparator()) ;
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PortalSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("name", "name", null).
                                addValidator(EmptyFieldValidator.class).
                                addValidator(NameValidator.class).
                                setEditable(false)).
                 addUIFormInput(new UIFormSelectBox("locale", "locale", languages).
                                addValidator(EmptyFieldValidator.class));
    
    List<SelectItemOption<String>> listSkin = new ArrayList<SelectItemOption<String>>() ;
    SkinService skinService = getApplicationComponent(SkinService.class);
    Iterator<String> skinIterator = skinService.getAvailableSkins();
    while(skinIterator.hasNext()){
      String skin = skinIterator.next();
      SelectItemOption<String> skinOption = new SelectItemOption<String>(skin, skin);
      listSkin.add(skinOption);
    }
    UIFormSelectBox uiSelectBox = new UIFormSelectBox(SKIN, SKIN, listSkin) ;
    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.getLocale();
    uiSelectBox.setValue(uiPortal.getSkin());
    uiSelectBox.setEditable(false);
    uiSettingSet.addUIFormInput(uiSelectBox);
    addUIFormInput(uiSettingSet);
    uiSettingSet.setRendered(false);
    
    
    UIFormInputSet uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    uiPermissionSetting.setRendered(false);
    addUIComponentInput(uiPermissionSetting);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiListPermissionSelector.addValidator(EmptyIteratorValidator.class);
    uiPermissionSetting.addChild(uiListPermissionSelector);


    UIPermissionSelector uiEditPermission = createUIComponent(UIPermissionSelector.class, null, null);
    uiEditPermission.setRendered(false) ;
    uiEditPermission.addValidator(org.exoplatform.webui.organization.UIPermissionSelector.EmptyFieldValidator.class);
    uiEditPermission.configure("UIPermissionSelector", "editPermission");
    uiPermissionSetting.addChild(uiEditPermission);
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm  =  event.getSource();
      String locale = uiForm.getUIStringInput("locale").getValue() ;
     
      UIPortalApplication uiApp = uiForm.getAncestorOfType(UIPortalApplication.class);
      
      UIPortal uiPortal = Util.getUIPortal();
      uiForm.invokeSetBindingBean(uiPortal);
      //if(uiPortal.getFactoryId().equals(UIPortalForm.DEFAULT_FACTORY_ID)) uiPortal.setFactoryId(null);
     
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
      String portalName = uiForm.getUIStringInput("name").getValue();
      DataStorage dataService = uiForm.getApplicationComponent(DataStorage.class) ;
      PortalConfig config = dataService.getPortalConfig(portalName);
      if(config != null) {
        UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPortalForm.msg.sameName", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return;
      }
      
      UserPortalConfigService service = uiForm.getApplicationComponent(UserPortalConfigService.class);
      service.createUserPortalConfig(portalName, template);
      UserPortalConfig userPortalConfig = service.getUserPortalConfig(portalName, pcontext.getRemoteUser());
      PortalConfig pconfig = userPortalConfig.getPortalConfig();
      uiForm.invokeSetBindingBean(pconfig);
      PageNavigation navigation = service.getPageNavigation(PortalConfig.PORTAL_TYPE+"::"+portalName) ;
      navigation.setCreator(pcontext.getRemoteUser());
      pconfig.setCreator(pcontext.getRemoteUser());
      service.update(pconfig);
      service.update(navigation);
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
      
      UIPortalBrowser uiPortalBrowser = uiPortalApp.findFirstComponentOfType(UIPortalBrowser.class);
      uiPortalBrowser.loadPortalConfigs();
      pcontext.addUIComponentToUpdateByAjax(uiPortalBrowser);
    }
  }
  
  static  public class SelectItemOptionActionListener extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm = event.getSource();
      UIFormInputItemSelector templateInput = uiForm.getChild(UIFormInputItemSelector.class);
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
