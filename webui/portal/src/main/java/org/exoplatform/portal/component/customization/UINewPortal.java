/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.organization.webui.component.UIAccessGroup;
import org.exoplatform.organization.webui.component.UIAccountInputSet;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.NameValidator;
import org.exoplatform.webui.config.Component;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 11, 2007  
 */

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormTabPane.gtmpl",    
  initParams = @ParamConfig(
      name = "PortalTemplateConfigOption", 
      value = "app:/WEB-INF/conf/uiconf/portal/webui/component/customization/UIPortalTemplateConfigOption.groovy"
  ),    
  events = {
    @EventConfig(listeners = UINewPortal.SaveActionListener.class),
    @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
  }
)

public class UINewPortal extends UIFormTabPane {

  private static final String SKIN = "skin";
  private PortalConfig portalConfig_;
  private List<SelectItemOption<String>> languages = new ArrayList<SelectItemOption<String>>() ;
  
  private static String DEFAULT_FACTORY_ID = "default";
  
  public UINewPortal(InitParams initParams) throws Exception {
    super("UINewPortal");
    
    LocaleConfigService localeConfigService  = getApplicationComponent(LocaleConfigService.class) ;
    Collection listLocaleConfig = localeConfigService.getLocalConfigs() ;

    Iterator iterator = listLocaleConfig.iterator() ;
    while(iterator.hasNext()) {
      LocaleConfig localeConfig = (LocaleConfig) iterator.next() ;
      languages.add(new SelectItemOption<String>(localeConfig.getLanguage(), localeConfig.getLanguage())) ;
    }
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PortalSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("name", "name", null).
                                addValidator(EmptyFieldValidator.class).
                                addValidator(NameValidator.class).
                                setEditable(true)).
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
    uiSettingSet.getUIFormSelectBox("locale").setEditable(false);
    
    UIFormInputItemSelector templateInput = new  UIFormInputItemSelector("AccountTemplate", null);
    templateInput.setRendered(false) ;
    addUIFormInput(templateInput) ;
    
    if(initParams == null) return ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    
    Param param = initParams.getParam("PortalTemplateConfigOption");
    System.out.println("\n\n\n == > Ha an cut "+param+"\n\n\n");
    List<SelectItemCategory> itemConfigs = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    for(SelectItemCategory itemCategory: itemConfigs){
      templateInput.getItemCategories().add(itemCategory);
    }
    
    if(templateInput.getSelectedItemOption() == null) {
      templateInput.getItemCategories().get(0).setSelected(true);
    }
    
    UIAccessGroup uiAccessGroup = createUIComponent(UIAccessGroup.class, null, "UIAccessGroup");
    uiAccessGroup.setRendered(false);
    uiAccessGroup.configure("AccessGroup", "accessGroup");
    addUIComponentInput(uiAccessGroup);
    
  }
  
  public PortalConfig getPortalConfig() { return portalConfig_; }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIAccessGroup uiAccessGroup = getChild(UIAccessGroup.class);
    if(uiAccessGroup == null) return;
    UIPopupWindow uiPopupWindow = uiAccessGroup.getChild(UIPopupWindow.class) ;
    uiPopupWindow.processRender(context);
  }
  
  public void setValues(PortalConfig uiPortal) throws Exception {
    portalConfig_ = uiPortal;
    if(portalConfig_.getFactoryId() == null) portalConfig_.setFactoryId(DEFAULT_FACTORY_ID);    
    invokeGetBindingBean(portalConfig_) ;
    
    UIAccessGroup uiAccessGroup = getChild(UIAccessGroup.class);
    uiAccessGroup.setGroups(uiPortal.getAccessGroup());
  }
  
  static public class SaveActionListener  extends EventListener<UINewPortal> {
    public void execute(Event<UINewPortal> event) throws Exception {
      
    }
  }

}
