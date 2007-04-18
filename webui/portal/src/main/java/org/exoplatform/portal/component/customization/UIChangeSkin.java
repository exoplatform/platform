package org.exoplatform.portal.component.customization;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.PortalConfig;
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
    template = "app:/groovy/portal/webui/component/customization/UIChangeSkin.gtmpl",
    initParams = @ParamConfig(
        name = "ChangeSkinTemplateConfigOption",
        value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/ChangeSkinTemplateConfigOption.groovy"
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
    name_ = "UIChangeSkinForm";    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeSkinTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
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
      uiPortal.setSkin(skin);
      PortalConfig portalConfig  = PortalDataModelUtil.toPortalConfig(uiPortal, true);
      PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
      dataService.savePortalConfig(portalConfig);
      
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
      uiMaskWS.setUIComponent(null);
    }
  }


}
