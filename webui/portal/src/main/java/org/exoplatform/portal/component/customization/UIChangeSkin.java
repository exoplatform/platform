package org.exoplatform.portal.component.customization;

import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataMapper;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
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
  template = "app:/groovy/portal/webui/component/customization/UIChangeSkin.gtmpl",
  initParams = @ParamConfig(
    name = "ChangeSkinTemplateConfigOption",
    value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/SkinConfigOption.groovy"
  ),
  events = {
    @EventConfig(listeners = UIChangeSkin.SaveActionListener.class),
    @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class)
  }
)
public class UIChangeSkin extends UIContainer {
 
  private String name_;
  
  @SuppressWarnings("unchecked")
  public UIChangeSkin(InitParams initParams) throws Exception  { 
    name_ = "UIChangeSkin";    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeSkinTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
    UIPortal uiPortal = Util.getUIPortal();
    String currentSkin = uiPortal.getSkin();
    
    if(currentSkin == null ) currentSkin = "Default"; 
    for(SelectItemCategory ele : itemCategories) {
      if(ele.getName().equals(currentSkin)) ele.setSelected(true);
      else  ele.setSelected(false);
    }
    
    UIItemSelector selector = new UIItemSelector("Skin");
    selector.setItemCategories(itemCategories);
    selector.setRendered(true);
    addChild(selector);
  }
  
  public String getName() { return name_; }

  public void setName(String name) { name_ = name; }
  
  static public class SaveActionListener  extends EventListener<UIChangeSkin> {
    public void execute(Event<UIChangeSkin> event) throws Exception {
      String skin  = event.getRequestContext().getRequestParameter("skin");
      
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);    
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiApp) ;
      
      if(skin == null || skin.trim().length() < 1) return;       
      if(!uiPortal.isModifiable()) return;
      
      uiPortal.setSkin(skin);
      PortalConfig portalConfig  = PortalDataMapper.toPortal(uiPortal);
      UserPortalConfigService dataService = uiPortal.getApplicationComponent(UserPortalConfigService.class);
      dataService.update(portalConfig);

      uiApp.setSkin(skin);
    }
  }

}
