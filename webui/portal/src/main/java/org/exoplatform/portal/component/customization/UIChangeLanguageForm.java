package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIItemSelector;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/customization/UIChangeLanguageForm.gtmpl",
    initParams = {
      @ParamConfig(
          name = "ChangeLanguageTemplateConfigOption",
          value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/ChangeLanguageTemplateConfigOption.groovy"
      )  
    },
    events = {
      @EventConfig(listeners = UIChangeLanguageForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UIChangeLanguageForm extends UIContainer{
  String name_;
  private String[] actions_ = null;
  
  @SuppressWarnings("unchecked")
  public UIChangeLanguageForm(InitParams initParams) throws Exception  { 
    name_ = "UIChangeLanguageForm";
    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeLanguageTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
    UIItemSelector selector = new UIItemSelector("Language");
    selector.setItemCategories(itemCategories );
    selector.setRendered(true);
    addChild(selector);
  }
  
  public String getName() {
    return name_;
  }
  
  public void setActions(String [] actions){
    actions_ = actions;
  }
  
  public String[] getActions() {
    if(actions_ != null) return actions_;
    ArrayList<org.exoplatform.webui.config.Event> events = config.getEvents();
    actions_ = new String[events.size()];    
    for(int i = 0; i < actions_.length; i++){
      actions_[i] = events.get(i).getName();
    }
    return actions_;  
  }
  
  public String event(String actionName) throws Exception {
    StringBuilder b = new StringBuilder() ;
    b.append("javascript:eXo.portal.UIPortalControl.changeLanguage('").append(getName()).append("', '");
    b.append(actionName).append("')");
    return b.toString() ;
  }
  
  static public class SaveActionListener  extends EventListener<UIChangeLanguageForm> {
    public void execute(Event<UIChangeLanguageForm> event) throws Exception {
      String language  = event.getRequestContext().getRequestParameter("Language");
//    TODO khi thay uiMaskWP = uiApp tai dong 91. StackOverflowError say ra. 
//    tai sao trong UIChangeSkin.java ko bi nhu vay.
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);    
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS) ;
      if(language == null || language.trim().length() < 1) return;       
      UserACL userACL = uiPortal.getApplicationComponent(UserACL.class);
      String accessUser = event.getRequestContext().getRemoteUser();
      String permission = uiPortal.getEditPermission();
      if(!userACL.hasPermission(uiPortal.getOwner(), accessUser, permission)) return;
      
      LocaleConfigService localeConfigService  = event.getSource().getApplicationComponent(LocaleConfigService.class) ;
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(language);
      if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
      
      uiPortal.setLocale(language);
      PortalConfig portalConfig  = PortalDataModelUtil.toPortalConfig(uiPortal, true);
      PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
      dataService.savePortalConfig(portalConfig);
      
      uiApp.setLocale(localeConfig.getLocale());
    }
  }

}
