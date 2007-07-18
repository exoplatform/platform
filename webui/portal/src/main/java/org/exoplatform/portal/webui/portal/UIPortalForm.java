/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.Component;
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
  
  private static String DEFAULT_FACTORY_ID = "default";
  
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
    
    WebuiRequestContext currReqContext = RequestContext.getCurrentInstance() ;
    WebuiApplication app = (WebuiApplication)currReqContext.getApplication() ;
    List<Component> configs = app.getConfigurationManager().getComponentConfig(UIPortalApplication.class);
    List<SelectItemCategory>  itemCategories = new ArrayList<SelectItemCategory>();
    for(Component ele : configs) {
      String id =  ele.getId();
      if(id == null) id = DEFAULT_FACTORY_ID;
      StringBuilder builder = new StringBuilder(id);
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
      String upId = builder.toString();
      
      SelectItemCategory category = new SelectItemCategory(upId);
      itemCategories.add(category);
      List<SelectItemOption<String>> items = new ArrayList<SelectItemOption<String>>();
      category.setSelectItemOptions(items);
      SelectItemOption<String> item = new SelectItemOption<String>(id, id, "Portal"+upId);
      items.add(item);
    } 
    
    UIFormInputSet uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    uiPermissionSetting.setRendered(false);
    addUIComponentInput(uiPermissionSetting);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiPermissionSetting.addChild(uiListPermissionSelector);
    
    UIPermissionSelector uiEditPermission = createUIComponent(UIPermissionSelector.class, null, null);
    uiEditPermission.setRendered(false) ;
    uiEditPermission.configure("UIPermissionSelector", "editPermission");
    uiPermissionSetting.addChild(uiEditPermission);
        
    UIFormInputItemSelector uiFactoryId = new UIFormInputItemSelector("FactoryId", "factoryId");
    uiFactoryId.setItemCategories(itemCategories);
    uiFactoryId.setRendered(false);
    addUIFormInput(uiFactoryId);
    
    this.<UIFormInputSet>getChildById("PortalSetting").setRendered(true);
    invokeGetBindingBean(Util.getUIPortal()) ;
  }
  
  private void createDefaultItem() throws Exception {
    LocaleConfigService localeConfigService  = getApplicationComponent(LocaleConfigService.class) ;
    Collection<?> listLocaleConfig = localeConfigService.getLocalConfigs() ;

    Iterator<?> iterator = listLocaleConfig.iterator() ;
    while(iterator.hasNext()) {
      LocaleConfig localeConfig = (LocaleConfig) iterator.next() ;
      languages.add(new SelectItemOption<String>(localeConfig.getLanguage(), localeConfig.getLanguage())) ;
    }

    UIFormInputSet uiSettingSet = new UIFormInputSet("PortalSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("name", "name", null).
                                addValidator(EmptyFieldValidator.class).
                                addValidator(NameValidator.class).
                                setEditable(false)).
                 addUIFormInput(new UIFormSelectBox("locale", "locale", languages).
                                addValidator(EmptyFieldValidator.class));
    
    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
    ls.add(new SelectItemOption<String>("Default", "Default")) ;
    ls.add(new SelectItemOption<String>("Mac", "Mac")) ;
    ls.add(new SelectItemOption<String>("Vista", "Vista")) ;
    UIFormSelectBox uiSelectBox = new UIFormSelectBox(SKIN, SKIN, ls) ;
    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.getLocale();
    uiSelectBox.setValue(uiPortal.getSkin());
    uiSelectBox.setEditable(false);
    uiSettingSet.addUIFormInput(uiSelectBox);
    addUIFormInput(uiSettingSet);
    uiSettingSet.setRendered(false);
  }
  
  static public class SaveActionListener  extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm  =  event.getSource();
      String locale = uiForm.getUIStringInput("locale").getValue() ;
      LocaleConfigService localeConfigService  = uiForm.getApplicationComponent(LocaleConfigService.class) ;
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(locale);
      UIPortalApplication uiApp = uiForm.getAncestorOfType(UIPortalApplication.class);
      
      UIPortal uiPortal = Util.getUIPortal();
      uiForm.invokeSetBindingBean(uiPortal);
      
      if(uiPortal.getFactoryId().equals(UIPortalForm.DEFAULT_FACTORY_ID)) uiPortal.setFactoryId(null);      
      if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
      uiApp.setLocale(localeConfig.getLocale());
      
      UIMaskWorkspace uiMaskWorkspace = uiForm.getParent();
      uiMaskWorkspace.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);
    }
  }
  
  static public class CreateActionListener  extends EventListener<UIPortalForm> {
    public void execute(Event<UIPortalForm> event) throws Exception {
      UIPortalForm uiForm = event.getSource();
      String template = uiForm.getChild(UIFormInputItemSelector.class).getSelectedItemOption().getValue().toString();
      String portalName = uiForm.getUIStringInput("name").getValue();
      DataStorage dataService = uiForm.getApplicationComponent(DataStorage.class) ;
      PortalConfig config = dataService.getPortalConfig(portalName);
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
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
      
      pconfig.setCreator(pcontext.getRemoteUser());
      service.update(pconfig);
      
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
