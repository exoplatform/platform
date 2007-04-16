package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
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
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/customization/UIChangeSkinForm.gtmpl",
    initParams = {
        @ParamConfig(
            name = "ChangeSkinTemplateConfigOption",
            value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/ChangeSkinTemplateConfigOption.groovy"
        )        
    },
    events = {
      @EventConfig(listeners = UIChangeSkinForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
//TODO:  This code has many  problems
public class UIChangeSkinForm extends UIContainer {
  String name_;
  private String[] actions_ = null ;
  
  @SuppressWarnings("unchecked")
  public UIChangeSkinForm(InitParams initParams) throws Exception  { 
    name_ = "UIChangeSkinForm";    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    Param param = initParams.getParam("ChangeSkinTemplateConfigOption");
    List<SelectItemCategory> itemCategories = (List<SelectItemCategory>)param.getMapGroovyObject(context);
    
    UIItemSelector selector = new UIItemSelector("Skin");
    selector.setItemCategories(itemCategories);
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
    b.append("javascript:eXo.portal.UIPortalControl.changeSkin('").append(getName()).append("', '");
    b.append(actionName).append("')");
    return b.toString() ;
  }
  
  static public class SaveActionListener  extends EventListener<UIChangeSkinForm> {
    public void execute(Event<UIChangeSkinForm> event) throws Exception {
/*      UIChangeSkinForm uicomp = event.getSource() ;
      UIMaskWorkspace uiMaskWorkspace = uicomp.getAncestorOfType(UIMaskWorkspace.class);
      if(!uiMaskWorkspace.isShow()) return;
      uiMaskWorkspace.setUIComponent(null);
      
      UIItemSelector uiTemplate  = uicomp.getChild(UIItemSelector.class);
      UIPortalApplication uiApp = uicomp.getAncestorOfType(UIPortalApplication.class);      
      uiApp.setSkin(uiTemplate.getSelectedItemOption().getValue().toString());
      
      UIPortal uiPortal = Util.getUIPortal();     
      uiPortal.setSkin(uiTemplate.getSelectedItemOption().getValue().toString());
      PortalConfig portalConfig  = PortalDataModelUtil.toPortalConfig(uiPortal, true);
      PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
      dataService.savePortalConfig(portalConfig);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      String url = pcontext.getRequestContextPath();
      pcontext.getJavascriptManager().addJavascript("window.location=\""+url+"\"");*/
    }
  }

}
