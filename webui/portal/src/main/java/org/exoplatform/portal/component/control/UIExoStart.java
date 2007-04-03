/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.customization.UIChangeSkinForm;
import org.exoplatform.portal.component.customization.UIPageCreationWizard;
import org.exoplatform.portal.component.customization.UIPageEditWizard;
import org.exoplatform.portal.component.customization.UIPageManagement;
import org.exoplatform.portal.component.customization.UIPortalManagement;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.customization.UISkinSelector;
import org.exoplatform.portal.component.customization.UIWizardPageCreationBar;
import org.exoplatform.portal.component.customization.UIManagement.ManagementMode;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.component.widget.UILogged;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig (
  template = "system:/groovy/portal/webui/component/control/UIExoStart.gtmpl" ,
  initParams = {
    @ParamConfig(
      name = "UIExoStartPersonnalizationMenu",
      value = "system:/WEB-INF/conf/uiconf/portal/webui/component/control/UIExoStartPersonnalizationMenu.groovy"
    ),
    @ParamConfig(
      name = "UIExoStartSystemMenu",
      value = "system:/WEB-INF/conf/uiconf/portal/webui/component/control/UIExoStartSystemMenu.groovy"
    )
  },
  events = {
    @EventConfig(listeners = UIExoStart.BasicCustomizationActionListener.class),
//    @EventConfig(listeners = UIExoStart.AdvancedCustomizationActionListener.class),
    @EventConfig(listeners = UIExoStart.MyPortalActionListener.class),
    @EventConfig(listeners = UIExoStart.ChangeSkinActionListener.class),
//    @EventConfig(listeners = UISkinSelector.ChangeSkinActionListener.class),
    @EventConfig(listeners = UIExoStart.ChangeLanguageActionListener.class),
    @EventConfig(listeners = UIExoStart.PageCreationWizardActionListener.class),
    @EventConfig(listeners = UIExoStart.EditCurrentPageActionListener.class),
//    @EventConfig(listeners = UIExoStart.DebugActionListener.class),
    @EventConfig(listeners = UIExoStart.PageManagementActionListener.class),
    @EventConfig(listeners = UIExoStart.PortalManagementActionListener.class),
//    @EventConfig(listeners = UIExoStart.Web20ActionListener.class),
    @EventConfig(listeners = UIExoStart.RefreshActionListener.class),
    @EventConfig(listeners = UIExoStart.ChangePageActionListener.class),
    @EventConfig(listeners = UIExoStart.LoginActionListener.class),
    @EventConfig(listeners = UILogged.LogoutActionListener.class),
    @EventConfig(listeners = UIExoStart.InterfaceSettingsActionListener.class)
  }
)
public class UIExoStart extends UIComponent {
  
  private List<List<MenuItemContainer>> menus = new ArrayList<List<MenuItemContainer>>(3);
  private boolean logged ;
  
  public UIExoStart(InitParams initParams) throws Exception {
    PortalRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    logged = context.getAccessPath() == PortalRequestContext.PRIVATE_ACCESS ;
    List<MenuItemContainer> menu  = null ;
    if(logged) {
      menu = initParams.getParam("UIExoStartPersonnalizationMenu").getMapGroovyObject(context); 
      menus.add(menu) ;     
    }
    menu = initParams.getParam("UIExoStartSystemMenu").getMapGroovyObject(context); 
    menus.add(menu) ;    
  }
  
  public boolean isLogged() { return logged ; }
  
  public List<List<MenuItemContainer>>  getMenus() {  return menus ; }
  
  public List<PageNavigation> getNavigations() {
    return org.exoplatform.portal.component.view.Util.getUIPortal().getNavigations() ;
  }
  
  static public class MenuItem {
    private String name ;
    private String icon ;
    
    public MenuItem(String name, String icon) {
      this.name = name ;
      this.icon = icon ;
    }
    
    public String getName()  { return name ; }
    public String getIcon() { return  icon ; }
  }
  
  static public class MenuItemContainer extends MenuItem {
    private List<MenuItem> children  = new ArrayList<MenuItem>(5) ;
    
    public MenuItemContainer(String name) {
      super(name, name + "Icon") ;
    }
    
    public MenuItemContainer(String name, String icon) {
      super(name, icon) ;
    }
    
    public MenuItemContainer  add(MenuItem item) {     
        children.add(item) ;      
      return this ;
    }
    
    public List<MenuItem>  getChildren() { return children ; }
  }
  
  static public class MenuItemAction extends MenuItem {
    private String action ;
    private boolean useAjax = true;
    
    public  MenuItemAction(String name) {
      super(name, name + "Icon") ;
      this.action = name;
    }
    
    public  MenuItemAction(String name, boolean useAjax) {
      super(name, name + "Icon") ;
      this.useAjax = useAjax;
      this.action = name;
    }
    
    public  MenuItemAction(String name, String icon) {
      super(name, icon) ;
      this.action = name;
    }
    
    public  MenuItemAction(String name, String icon, String action) {
      super(name, icon) ;
      this.action = action ;
    }
    
    public  MenuItemAction(String name, String icon, String action, boolean useAjax) {
      super(name, icon) ;
      this.action = action ;
      this.useAjax = useAjax ;
    }
    
    public String getAction() { return action ; }
    
    public  boolean useAjax() {  return useAjax ; }
  }
  
  public <T extends UIComponent> void setUIControlWSWorkingComponent(Class<T> clazz) throws Exception {
    UIControlWorkspace uiControl =  getAncestorOfType(UIControlWorkspace.class) ;
    UIComponentDecorator uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
    uiWorking.setUIComponent(uiWorking.createUIComponent(clazz, null, null)) ;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T getUIControlWSWorkingComponent() throws Exception {
    UIControlWorkspace uiControl =  getAncestorOfType(UIControlWorkspace.class) ;
    UIComponentDecorator uiWorking = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID) ;
    return (T)uiWorking.getUIComponent();
  }
  
  
  static public class BasicCustomizationActionListener extends EventListener<UIExoStart>{
    @SuppressWarnings("unused")
    public void execute(Event<UIExoStart> event ) throws Exception{
      System.out.println("Call BasicCustomizationActionListener");
    }
  }
    
  static public class PageManagementActionListener extends EventListener<UIExoStart> {    
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource() ;
      uiComp.setUIControlWSWorkingComponent(UIPageManagement.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiComp);
      
      UIPageManagement uiManagement = uiComp.getUIControlWSWorkingComponent();      
      String mode  = event.getRequestContext().getRequestParameter(OBJECTID);
      uiManagement.setMode(ManagementMode.valueOf(mode.toUpperCase()), event);
    }
  }
  
  static public class PortalManagementActionListener extends EventListener<UIExoStart> {    
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiComp = event.getSource() ;
      uiComp.setUIControlWSWorkingComponent(UIPortalManagement.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiComp);
      
      UIPortalManagement uiManagement = uiComp.getUIControlWSWorkingComponent();      
      String mode  = event.getRequestContext().getRequestParameter(OBJECTID);
      uiManagement.setMode(ManagementMode.valueOf(mode.toUpperCase()), event);
    }
  }
  
  static public class MyPortalActionListener extends EventListener<UIExoStart> {    
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uicomp = event.getSource() ;
      uicomp.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;      
      event.getRequestContext().addUIComponentToUpdateByAjax(uicomp.getParent()) ;
    }
  }
  
  static  public class ChangeSkinActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {      
      String skin  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIExoStart uicomp = event.getSource() ;
      UIPortalApplication uiApp = uicomp.getAncestorOfType(UIPortalApplication.class);      
      uiApp.setSkin(skin);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiApp) ;
    }
  } 
  
  static  public class ChangeLanguageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      String localeName  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIExoStart uiComp = event.getSource() ;
      UIPortalApplication uiApp = uiComp.getAncestorOfType(UIPortalApplication.class);
      
      LocaleConfigService localeConfigService  = uiComp.getApplicationComponent(LocaleConfigService.class) ;     
      LocaleConfig localeConfig = localeConfigService.getLocaleConfig(localeName);
      if(localeConfig == null) localeConfig = localeConfigService.getDefaultLocaleConfig();
      uiApp.setLocale(localeConfig.getLocale());
    }
  }  
  
  static public class PageCreationWizardActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiExoStart = event.getSource();
      uiExoStart.setUIControlWSWorkingComponent(UIWizardPageCreationBar.class);
      UIPortalApplication uiApp = uiExoStart.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class) ;
      uiToolPanel.setWorkingComponent(UIPageCreationWizard.class, null) ;
      
      uiExoStart.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;      
          
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
      UIContainer uiParent = uiExoStart.getParent();
      UIComponentDecorator uiWorkingControl = uiParent.getChildById(UIControlWorkspace.WORKING_AREA_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingControl) ;    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiExoStart);
    }
  }
  
  static  public class EditCurrentPageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uiExoStart = event.getSource();
      
      UIPortalApplication uiApp = uiExoStart.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class) ;
      uiToolPanel.setWorkingComponent(UIPageEditWizard.class, null);
      UIPageEditWizard uiWizard = (UIPageEditWizard)uiToolPanel.getUIComponent();
      uiWizard.setDescriptionWizard();
          
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
      UIContainer uiParent = uiExoStart.getParent();
      UIComponentDecorator uiWorkingControl = uiParent.getChildById(UIControlWorkspace.WORKING_AREA_ID);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingControl) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiExoStart);
    }
  }
  
  
//  static  public class Web20ActionListener extends EventListener<UIExoStart> {
//    public void execute(Event<UIExoStart> event) throws Exception {
//      UIExoStart uiStart = event.getSource() ;
//      UIPortalApplication uiApp = uiStart.getAncestorOfType(UIPortalApplication.class);
//    }
//  }
  
    static  public class InterfaceSettingsActionListener extends EventListener<UIExoStart> {
       @SuppressWarnings("unchecked")
      public void execute(Event<UIExoStart> event) throws Exception {
         System.out.println("\n=======> Interface Settings\n");
         UIPortal uiPortal = Util.getUIPortal();
         UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
         UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ; 
         
         uiMaskWS.createUIComponent(UIChangeSkinForm.class);
         
         uiMaskWS.setWindowSize(640, 400);
         uiMaskWS.setShow(true);
         event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
         Util.updateUIApplication(event); 
       }
    }
  
  static  public class RefreshActionListener extends EventListener<UIExoStart> {
    @SuppressWarnings("unused")
    public void execute(Event<UIExoStart> event) throws Exception {
    }
  }
  
//  static  public class DebugActionListener extends EventListener<UIExoStart> {
//    public void execute(Event<UIExoStart> event) throws Exception {
//      UIExoStart uicomp = event.getSource() ;
//      UIPortalApplication uiControl = uicomp.getAncestorOfType(UIPortalApplication.class) ;
//      UIPopupWindow uiPopup = uiControl.getChildById(UIPortalApplication.POPUP_WINDOW_ID) ;
//      uiPopup.setUIComponent(uiPopup.createUIComponent(UIApplicationTree.class, null, null)) ;
//      uiPopup.setRendered(true) ;
//      uiPopup.setShow(true) ;
//      
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopup);
//    }
//  }
  
  static  public class ChangePageActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortal uiPortal = Util.getUIPortal();
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
      uiPortal.setRenderSibbling(UIPortal.class);      
      
      UIExoStart uicomp = event.getSource() ;
      uicomp.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;
    }
  }
  
  static  public class LoginActionListener extends EventListener<UIExoStart> {
    public void execute(Event<UIExoStart> event) throws Exception {
      UIExoStart uicomp = event.getSource() ;
      uicomp.setUIControlWSWorkingComponent(UIWelcomeComponent.class) ;       
    }
  }  
}
