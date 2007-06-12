package org.exoplatform.portal.component.customization;

import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataMapper;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIItemSelector;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/customization/UIChangeLanguage.gtmpl",
    initParams = @ParamConfig(
          name = "ChangeLanguageTemplateConfigOption",
          value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/LanguageConfigOption.groovy"
    ),    
    events = {
      @EventConfig(listeners = UIChangeLanguage.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
    }
)
public class UIChangeLanguage extends UIContainer {
  
  private String name_;
  
  @SuppressWarnings("unchecked")
  public UIChangeLanguage(InitParams initParams) throws Exception  { 
    name_ = "UIChangeLanguage";
    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeLanguageTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
    UIItemSelector selector = new UIItemSelector("Language");
    selector.setItemCategories(itemCategories );
    selector.setRendered(true);
    addChild(selector);
  }
  
  public String getName() { return name_; }
  
  static public class SaveActionListener  extends EventListener<UIChangeLanguage> {
    public void execute(Event<UIChangeLanguage> event) throws Exception {
      String language  = event.getRequestContext().getRequestParameter("language");
      
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);    
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS) ;
      
      if(language == null || language.trim().length() < 1) return;       
      if(!uiPortal.isModifiable()) return;
      
      LocaleConfigService localeConfigService  = event.getSource().getApplicationComponent(LocaleConfigService.class) ;
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(language);
      if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
      PortalConfig portalConfig  = PortalDataMapper.toPortal(uiPortal);
      UserPortalConfigService dataService = uiPortal.getApplicationComponent(UserPortalConfigService.class);
      dataService.update(portalConfig);
      uiApp.setLocale(localeConfig.getLocale());
    }
  }

}
