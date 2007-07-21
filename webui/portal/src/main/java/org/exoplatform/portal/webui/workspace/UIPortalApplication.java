/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.workspace;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.skin.SkinConfig;
import org.exoplatform.portal.webui.skin.SkinService;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
/**
 * UIPortalApplication 
 *   - UIControlWorkSpace 
 *   - UIWorkingWorkSpace
 *   - UIPopupWindow
 */
@ComponentConfigs({
  @ComponentConfig (
    lifecycle = UIPortalApplicationLifecycle.class,
    template = "system:/groovy/portal/webui/workspace/UIPortalApplication.gtmpl",
    initParams = @ParamConfig(name = "public.showControlWorkspace", value = "true" )
  ),
  @ComponentConfig (
    id = "office" ,
    lifecycle = UIPortalApplicationLifecycle.class,
    template = "system:/groovy/portal/webui/workspace/UIPortalApplication.gtmpl",
    initParams = @ParamConfig( name = "public.showControlWorkspace", value = "false" )    
  )
})
public class UIPortalApplication extends UIApplication {
  
  public static boolean DEVELOPING = false;
  
  private String nodePath_;
  
  static {
    DEVELOPING =  "true".equals(System.getProperty("exo.product.developing")) ;
  }
  
  final static public String UI_CONTROL_WS_ID = "UIControlWorkspace" ;
  final static public String UI_WORKING_WS_ID = "UIWorkingWorkspace" ;
  final static public String UI_MASK_WS_ID = "UIMaskWorkspace" ;
  
  private String skin_ = "Default" ;
  
  private UserPortalConfig userPortalConfig_;
  
  @SuppressWarnings("hiding")
  public  UIPortalApplication(InitParams initParams) throws Exception { 
    PortalRequestContext  context = PortalRequestContext.getCurrentInstance() ;
    context.setUIApplication(this);
    userPortalConfig_ = (UserPortalConfig)context.getAttribute(UserPortalConfig.class);
    if(userPortalConfig_ == null) throw new Exception("Can't load user portal config");
    if(context.getAccessPath() == PortalRequestContext.PUBLIC_ACCESS) {
      initPublicPortal(context, initParams) ;
    } else {
      initPrivatePortal(context) ;
    }
    
    String currentSkin = userPortalConfig_.getPortalConfig().getSkin();
    if(currentSkin != null && currentSkin.trim().length() > 0) skin_ = currentSkin;
    
    setOwner(context.getPortalOwner());    
  } 
  
  public String getSkin() {  return skin_ ; }
  public void setSkin(String skin){ this.skin_ = skin; }
  
  public SkinConfig getSkin(String module) {
    SkinService skinService = getApplicationComponent(SkinService.class);
    return skinService.getSkin(module, skin_);
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
      SkinConfig skinConfig = skinService.getSkin(module, skin_) ;
      if(skinConfig == null && !"Default".equals(skin_)) {
        skinConfig = skinService.getSkin(module, "Default") ;
      }
      if(skinConfig != null) skins.add(skinConfig);
    }
    return skins ;
  }
  
  @SuppressWarnings("hiding")
  private  void  initPublicPortal(PortalRequestContext context, InitParams initParams) throws Exception {
    if("true".equals(initParams.getParam("public.showControlWorkspace").getValue())) {
      addChild(UIControlWorkspace.class, UIPortalApplication.UI_CONTROL_WS_ID, null) ;      
    }
    addWorkingWorkspace(context) ;
  }
  
  @SuppressWarnings("hiding")
  private  void  initPrivatePortal(PortalRequestContext context) throws Exception {
    addChild(UIControlWorkspace.class, UIPortalApplication.UI_CONTROL_WS_ID, null) ;
    addWorkingWorkspace(context) ;
  }
  
  @SuppressWarnings({"hiding","unused"})
  private void addWorkingWorkspace(PortalRequestContext context) throws Exception {
    UIWorkspace uiWorkingWorkspace = 
      createUIComponent(UIWorkspace.class, UIPortalApplication.UI_WORKING_WS_ID, null) ;
    UIPortal uiPortal = createUIComponent(UIPortal.class, null, null);
    PortalDataMapper.toUIPortal(uiPortal, userPortalConfig_);
    uiWorkingWorkspace.addChild(uiPortal) ;    
    uiWorkingWorkspace.addChild(UIPortalToolPanel.class, null, null).setRendered(false) ;    
    addChild(uiWorkingWorkspace) ;
    addChild(UIMaskWorkspace.class, UIPortalApplication.UI_MASK_WS_ID, null) ;
  }
  
  public void  processDecode(WebuiRequestContext context) throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext) context;
    String nodePath = pcontext.getNodePath();
    if(nodePath == null) {
      super.processDecode(context);
      return ;
    }

    nodePath = nodePath.trim();
    if(nodePath.equals(nodePath_)) {
      super.processDecode(context);
      return;
    }
    
    nodePath_ = nodePath;
    UIPortal uiPortal = findFirstComponentOfType(UIPortal.class);
    PageNodeEvent<UIPortal> pnevent = 
      new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, nodePath_) ;
    uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    super.processDecode(context);
  }
  
  public void  processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    if(!context.useAjax()) {
      super.processRender(context) ;
    } else {
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
      w.write("<div class=\"PortalResponse\">") ;
      if(!context.getFullRender()) {
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
      w.    write(pcontext.getJavascriptManager().getJavascript());
      w.    write("eXo.core.Browser.onLoad();\n"); 
      w.    write(pcontext.getJavascriptManager().getCustomizedOnLoadScript()) ;
      if(skin != null){
        w.  write(skin) ;
      }
      w.  write("</div>") ;
      w.write("</div>") ;
    }
    
//    if(w instanceof HtmlValidator) {
//      HtmlValidator validator = (HtmlValidator) w ;
//      validator.finish() ;
//      validator.flush();
//    }
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
      SkinConfig skinConfig = skinService.getSkin(module,skin_) ;
      if(skinConfig == null && !"Default".equals(skin_)) {
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
  
  public UserPortalConfig getUserPortalConfig() { return userPortalConfig_; }
  public void setUserPortalConfig(UserPortalConfig userPortalConfig) {
    this.userPortalConfig_ = userPortalConfig; 
  }
 
}