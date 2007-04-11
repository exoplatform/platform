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
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/portal/webui/component/customization/UIChangeSkinForm.gtmpl",   
    events = {
      @EventConfig(listeners = UIChangeSkinForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
//TODO:  This code has many  problems
public class UIChangeSkinForm extends UIFormTabPane{
  
  @SuppressWarnings("unchecked")
  public UIChangeSkinForm() throws Exception  { 
    super("UIChangeSkinForm");

    SelectItemCategory skinVista = new SelectItemCategory("Vista", false);
    List<SelectItemOption> vistaList = new  ArrayList<SelectItemOption>();
    vistaList.add(new SelectItemOption("Vista", "Vista", "Vista"));
    
    skinVista.setSelectItemOptions(vistaList);
    
    SelectItemCategory skinMac = new SelectItemCategory("Mac", false);
    List<SelectItemOption> macList = new  ArrayList<SelectItemOption>();
    macList.add(new SelectItemOption("Mac", "Mac", "Mac"));
    skinMac.setSelectItemOptions(macList);
    
    SelectItemCategory skinDefault = new SelectItemCategory("Default", false);
    List<SelectItemOption> defaultList = new  ArrayList<SelectItemOption>();
    defaultList.add(new SelectItemOption("Default", "Default", "Default"));
    skinDefault.setSelectItemOptions(defaultList);
    skinDefault.setSelected(true);
    
    List<SelectItemCategory> itemCategories = new ArrayList<SelectItemCategory>();
    itemCategories.add(skinDefault);
    itemCategories.add(skinMac);
    itemCategories.add(skinVista);
    
    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Skin", "  ");
    uiTemplate.setItemCategories(itemCategories );
    uiTemplate.setRendered(true);
    addChild(uiTemplate);
  }
  
  static public class SaveActionListener  extends EventListener<UIChangeSkinForm> {
    public void execute(Event<UIChangeSkinForm> event) throws Exception {
      UIChangeSkinForm uicomp = event.getSource() ;
      UIMaskWorkspace uiMaskWorkspace = uicomp.getAncestorOfType(UIMaskWorkspace.class);
      if(!uiMaskWorkspace.isShow()) return;
      uiMaskWorkspace.setUIComponent(null);
      
      UIFormInputItemSelector uiTemplate  = uicomp.getChild(UIFormInputItemSelector.class);
      UIPortalApplication uiApp = uicomp.getAncestorOfType(UIPortalApplication.class);      
      uiApp.setSkin(uiTemplate.getSelectedItemOption().getValue().toString());
      
      UIPortal uiPortal = Util.getUIPortal();     
      uiPortal.setSkin(uiTemplate.getSelectedItemOption().getValue().toString());
      PortalConfig portalConfig  = PortalDataModelUtil.toPortalConfig(uiPortal, true);
      PortalDAO dataService = uiPortal.getApplicationComponent(PortalDAO.class);
      dataService.savePortalConfig(portalConfig);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      String url = pcontext.getRequestContextPath();
      pcontext.getJavascriptManager().addJavascript("window.location=\""+url+"\"");
    }
  }

}
