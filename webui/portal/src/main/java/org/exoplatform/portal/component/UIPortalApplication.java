/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.control.lifecycle.UIPortalApplicationLifecycle;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.skin.SkinConfig;
import org.exoplatform.portal.skin.SkinService;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.ParamConfig;

/**
 * UIPortalApplication 
 *   - UIControlWorkSpace 
 *   - UIWorkingWorkSpace
 *   - UIPopupWindow
 */
@ComponentConfigs({
  @ComponentConfig (
    lifecycle = UIPortalApplicationLifecycle.class,
    template = "system:/groovy/portal/webui/component/UIPortalApplication.gtmpl",
    initParams = @ParamConfig(name = "public.showControlWorkspace", value = "true" )
  ),
  @ComponentConfig (
    id = "office" ,
    lifecycle = UIPortalApplicationLifecycle.class,
    template = "system:/groovy/portal/webui/component/UIPortalApplication.gtmpl",
    initParams = @ParamConfig( name = "public.showControlWorkspace", value = "false" )    
  )
})
public class UIPortalApplication extends UIApplication {
  
  public static boolean DEVELOPING = false;
  
  static {
    DEVELOPING =  "true".equals(System.getProperty("exo.product.developing")) ;
  }
  
  final static public String UI_CONTROL_WS_ID = "UIControlWorkspace" ;
  final static public String UI_WORKING_WS_ID = "UIWorkingWorkspace" ;
  final static public String UI_MASK_WS_ID = "UIMaskWorkspace" ;
  public  static String      POPUP_WINDOW_ID = "UIPortalApplicationPopupWindow" ;
  
  private String skin = "Default" ;
  private boolean useAjax_ = true ;
  
  @SuppressWarnings("hiding")
  public  UIPortalApplication(InitParams initParams) throws Exception { 
    PortalRequestContext  context = PortalRequestContext.getCurrentInstance() ;
    UserPortalConfig config = ( UserPortalConfig)context.getAttribute(UserPortalConfig.class);
    if(context.getAccessPath() == PortalRequestContext.PUBLIC_ACCESS) {
      initPublicPortal(config, context, initParams) ;
    } else {
      initPrivatePortal(config, context) ;
    }
    
    String currentSkin = config.getPortalConfig().getSkin();
    if(currentSkin != null && currentSkin.trim().length() > 0) {
      skin = currentSkin;
    } 
    
    setOwner(context.getPortalOwner());    
    UIPopupWindow uiWindow = addChild(UIPopupWindow.class, null, POPUP_WINDOW_ID);
    uiWindow.setWindowSize(800, -1) ;
    uiWindow.setShow(false);
  } 
  
  public String getSkin() {  return skin ; }
  
  public SkinConfig getSkin(String module) {
    SkinService skinService = getApplicationComponent(SkinService.class);
    return skinService.getSkin(module, skin);
  }
  
  public List<SkinConfig>  getPortletSkins() {
    List<SkinConfig> skins = new ArrayList<SkinConfig>() ;
    List<UIPortlet> uiportlets = new ArrayList<UIPortlet>() ;
    
    UIWorkspace uiWorkingWS =  getChildById(UI_WORKING_WS_ID ) ;
    UIPortal uiPortal =  uiWorkingWS.getChild(UIPortal.class) ;
    uiPortal.findComponentOfType(uiportlets, UIPortlet.class) ;
    SkinService skinService = getApplicationComponent(SkinService.class);
    for(UIPortlet uiPortlet : uiportlets) {
      String module = uiPortlet.getExoWindowID().getPortletApplicationName() + "/" + uiPortlet.getExoWindowID().getPortletName() ;
      SkinConfig skinConfig = skinService.getSkin(module, skin) ;
      if(skinConfig == null && !"Default".equals(skin)) {
        skinConfig = skinService.getSkin(module, "Default") ;
      }
      if(skinConfig != null) skins.add(skinConfig);
    }
    return skins ;
  }
  
  public void setSkin(String skin){ this.skin = skin; }
  
  public  boolean useAjax() {  return useAjax_ ; }
  public  void    setUseAjax(boolean b) { useAjax_ =  b;  }
  
  @SuppressWarnings("hiding")
  private  void  initPublicPortal(UserPortalConfig config, PortalRequestContext context, InitParams initParams) throws Exception {
    if("true".equals(initParams.getParam("public.showControlWorkspace").getValue())) {
      addChild(UIControlWorkspace.class, UIPortalApplication.UI_CONTROL_WS_ID, null) ;
      
    }
    addChild(UIMaskWorkspace.class,    UIPortalApplication.UI_MASK_WS_ID, null) ;
    addWorkingWorkspace(config, context) ;
  }
  
  @SuppressWarnings("hiding")
  private  void  initPrivatePortal(UserPortalConfig config, PortalRequestContext context) throws Exception {
    addChild(UIControlWorkspace.class, UIPortalApplication.UI_CONTROL_WS_ID, null) ;
    addChild(UIMaskWorkspace.class,    UIPortalApplication.UI_MASK_WS_ID, null) ;
    addWorkingWorkspace(config, context) ;
  }
  
  @SuppressWarnings({"hiding","unused"})
  private void addWorkingWorkspace(UserPortalConfig config, PortalRequestContext context) throws Exception {
    UIWorkspace uiWorkingWorkspace = 
      createUIComponent(UIWorkspace.class, UIPortalApplication.UI_WORKING_WS_ID, null) ;
    UIPortal uiPortal = createUIComponent(UIPortal.class, null, null);
    PortalDataModelUtil.toUIPortal(uiPortal, config, true);
    uiWorkingWorkspace.addChild(uiPortal) ;    
    uiWorkingWorkspace.addChild(UIPortalToolPanel.class, null, null).setRendered(false) ;    
    addChild(uiWorkingWorkspace) ;
  }
  
  public <T extends UIComponent> T setUIControlWSPopupComponent(Class<T> clazz) throws Exception {
    UIPopupWindow uiPopup = getChildById(UIPortalApplication.POPUP_WINDOW_ID) ;
    T uiComponent = uiPopup.createUIComponent(clazz, null, null);
    uiPopup.setUIComponent(uiComponent) ;
    uiPopup.setShow(true) ;  
    return uiComponent;
  }
  
  public void  processRender(RequestContext context) throws Exception {
    if(!context.isAjaxRequest()) {
      super.processRender(context) ;
      return;
    }
    PortalRequestContext pcontext = (PortalRequestContext)context;
    List<UIComponent> list = context.getUIComponentToUpdateByAjax() ;
    List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>(3);
    List<UIComponent> uiDataComponents = new ArrayList<UIComponent>(5);
    
    if(list != null) {
      for(UIComponent uicomponent : list) {
        if(uicomponent instanceof UIPortlet) uiPortlets.add((UIPortlet)uicomponent) ;
        else uiDataComponents.add(uicomponent) ;
      }
    }
    Writer w =  context.getWriter() ;
    w.write("<div class=\"PortalResponse\">") ;
    if(!context.isForceFullUpdate()) {
      for(UIPortlet uiPortlet : uiPortlets) {
        uiPortlet.processRender(context) ;
      }
    }
    w.  write("<div class=\"PortalResponseData\">");
    for(UIComponent uicomponent : uiDataComponents) {
      renderBlockToUpdate(uicomponent, context, w) ;
    }
    String skin  = getAddSkinScript(list);
    w.  write("</div>");
    w.  write("<div class=\"PortalResponseScript\">"); 
    w.    write(pcontext.getJavascript());
    w.    write("eXo.core.Browser.onLoad();\n"); 
    w.    write(pcontext.getCustomizedOnLoadScript()) ;
    if(skin != null){
      w.  write(skin) ;
    }
    w.  write("</div>") ;
    w.write("</div>") ;       
  }
  
  private String getAddSkinScript(List<UIComponent> updateComponents) {
    if(updateComponents == null) return null;
    List<UIPortlet> uiportlets = new ArrayList<UIPortlet>() ;
    for(UIComponent uicomponent : updateComponents) {
      if(uicomponent instanceof UIContainer){
        UIContainer uiContainer = (UIContainer) uicomponent;
        uiContainer.findComponentOfType(uiportlets, UIPortlet.class) ;
      }
    }
    List<SkinConfig> skins = new ArrayList<SkinConfig>() ;
    SkinService skinService = getApplicationComponent(SkinService.class);
    for(UIPortlet uiPortlet : uiportlets){
      String module = uiPortlet.getExoWindowID().getPortletApplicationName() + "/" + uiPortlet.getExoWindowID().getPortletName() ;
      SkinConfig skinConfig = skinService.getSkin(module,skin) ;
      if(skinConfig == null && !"Default".equals(skin)) {
        skinConfig = skinService.getSkin(module, "Default") ;
      }
      if(skinConfig != null) skins.add(skinConfig);
    }
    StringBuilder b = new StringBuilder(1000) ;
    for(SkinConfig ele : skins) {
      b.append("eXo.core.Skin.addSkin('").append(ele.getId()).
        append("','").append(ele.getCSSPath()).append("');\n"); 
    }
    return b.toString() ;
  }
}