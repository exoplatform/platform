/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.event.Event;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 5, 2006
 */
public class Util { 
  
  static public UIPortal getUIPortal(){
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    if(!(context instanceof PortalRequestContext)) {
      context =  (WebuiRequestContext)context.getParentAppRequestContext() ;      
    }
    UIPortalApplication uiApp = (UIPortalApplication) context.getUIApplication() ;     
    return uiApp.findFirstComponentOfType(UIPortal.class) ;    
  }  

  static public UIPortalToolPanel getUIPortalToolPanel(){
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    if(!(context instanceof PortalRequestContext)) {
      context =  (WebuiRequestContext)context.getParentAppRequestContext() ;      
    }
    UIPortalApplication uiApp = (UIPortalApplication) context.getUIApplication() ;     
    return uiApp.findFirstComponentOfType(UIPortalToolPanel.class) ;    
  }  

  static public PortalRequestContext getPortalRequestContext() {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    if(!(context instanceof PortalRequestContext)) {
      context =  (WebuiRequestContext)context.getParentAppRequestContext() ;
    }
    return (PortalRequestContext)context ;
  }

  static  public void setShowEditControl(UIComponent uiComponent, Class clazz){
    if(uiComponent == null) return;
    if(uiComponent instanceof UIPortalComponent) {
      UIPortalComponent uiContainer = (UIPortalComponent) uiComponent;
      if(clazz.isInstance(uiContainer)){
        uiContainer.setShowEditControl(true);
      }else {
        uiContainer.setShowEditControl(false);
      }
    }
    if(uiComponent instanceof org.exoplatform.webui.component.UIContainer){
      List<UIComponent> children  = 
        (( org.exoplatform.webui.component.UIContainer)uiComponent).getChildren();
      for(UIComponent comp : children ) setShowEditControl(comp, clazz);
      return;
    }
    UIComponentDecorator uiDecorator = (UIComponentDecorator) uiComponent;
    if(uiDecorator.getUIComponent() == null) return;
    setShowEditControl(uiDecorator.getUIComponent(), clazz);
  }

  /**
   * View component on UIWorkspaceWorking
   * $uicomp : current component on UIWorkspaceWorking
   * $clazz : Class of component should show on UIWorkspaceWorking
   */
  static public <T extends UIComponent> T showComponentOnWorking(
      UIComponent uicomp, Class<T> clazz) throws Exception {
    UIPortalApplication uiPortalApp = uicomp.getAncestorOfType(UIPortalApplication.class);
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);   
    T uiWork = uiToolPanel.createUIComponent(clazz, null, null);
    uiToolPanel.setUIComponent(uiWork);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
    return uiWork;
  }

  static public void showPortalComponentLayoutMode(UIPortalApplication uiPortalApp){   
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    uiWorkingWS.setRenderedChild(UIPortal.class) ;
    UIPortal uiPortal = uiWorkingWS.getChild(UIPortal.class);    

    UIContainer uiContainer = uiPortal.getFirstUIContainer();
    UIPage uiPage= uiPortal.findFirstComponentOfType(UIPage.class);
    UIPortlet uiPortlet= uiPortal.getFirstUIPortlet();

    String name = "";
    if(uiContainer != null && uiContainer.isShowEditControl())  name = "'UIContainer'";
    else if(uiPage != null && uiPage.isShowEditControl())  name = "'UIPage'";
    else if(uiPortlet != null  && uiPortlet.isShowEditControl()) name = "'UIPortlet'";

    getPortalRequestContext().getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPortal("+name+");");
  }

  static public void showPageComponentLayoutMode(UIPortalApplication uiPortalApp){   
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
    UIPortalToolPanel uiPortalToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);

    UIPage uiPage= uiPortalToolPanel.findFirstComponentOfType(UIPage.class);  
    UIContainer uiContainer = uiPage.findFirstComponentOfType(UIContainer.class);
    UIPortlet uiPortlet = uiPage.findFirstComponentOfType(UIPortlet.class);

    String name = "";
    if(uiContainer != null && uiContainer.isShowEditControl())  name = "UIContainer";      
    else if(uiPortlet != null  && uiPortlet.isShowEditControl()) name = "UIPortlet";

    getPortalRequestContext().getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPage('"+name+"');");
  }

  static public UIComponent findUIComponent(UIComponent uiComponent, Class clazz, Class ignoreClazz){
    if (clazz.isInstance(uiComponent)) return uiComponent;
    if(!(uiComponent instanceof UIContainer)) return null;
    List<UIComponent> children = ((UIContainer)uiComponent).getChildren();    
    for(UIComponent child : children){
      if (clazz.isInstance(child)) return child;      
      else if(!ignoreClazz.isInstance(child)){ 
        UIComponent value = findUIComponent(child, clazz, ignoreClazz);
        if(value != null) return value;
      }
    }    
    return null;
  }

  static public void findUIComponents(UIComponent uiComponent, 
                                      List<UIComponent> list, Class clazz, Class ignoreClazz){
    if (clazz.isInstance(uiComponent)) list.add(uiComponent); 
    if(!(uiComponent instanceof UIContainer)) return ;
    List<UIComponent> children = ((UIContainer)uiComponent).getChildren();    
    for(UIComponent child : children){
      if (clazz.isInstance(child)){       
        list.add(child);        
      }else if(!ignoreClazz.isInstance(child)){ 
        findUIComponents(child, list, clazz, ignoreClazz);
      }
    }
  }

  static public UIPage toUIPage(PageNode node, UIComponent uiParent) throws Exception {
    UserPortalConfigService configService = uiParent.getApplicationComponent(UserPortalConfigService.class);
    Page page  = configService.getPage(node.getPageReference(), getPortalRequestContext().getRemoteUser());
    return  toUIPage(page, uiParent);
  }

  static public UIPage toUIPage(Page page, UIComponent uiParent) throws Exception {   
    UIPage uiPage  = Util.getUIPortal().findFirstComponentOfType(UIPage.class);
    if(uiPage != null && uiPage.getId().equals(page.getId())) return uiPage;   
    WebuiRequestContext  context = Util.getPortalRequestContext() ;  
    if("Default".equalsIgnoreCase(page.getFactoryId())) {
      uiPage = uiParent.createUIComponent(context, UIPage.class, null, null);
    } else {
      uiPage = uiParent.createUIComponent(context, UIPage.class, page.getFactoryId(), null);
    }
    PortalDataModelUtil.toUIPage(uiPage, page, true);
    return uiPage;
  }

  static public void showComponentLayoutMode(Class clazz) throws Exception  {
    if(clazz == null) return;
    UIPortal uiPortal = getUIPortal();
    UIContainer uiParent  = null;
    if(uiPortal.isRendered()){
      uiPortal.setMaximizedUIComponent(null);
      uiParent = uiPortal;
    }
    else{
      UIPortalToolPanel uiPortalToolPanel = getUIPortalToolPanel();
      UIPage uiPage  = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      uiParent = uiPage;
    }
    if(uiParent == null) return;
    String layoutMode = clazz.getSimpleName();
    Util.setShowEditControl(uiPortal, clazz);
    
    PortalRequestContext context = Util.getPortalRequestContext() ;
    if(uiParent instanceof UIPortal){
      context.getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPortal('"+layoutMode+"');") ;
    } else {
      context.getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPage('"+layoutMode+"');") ;
    }
  }
  
  static public UIWorkspace updateUIApplication(Event<? extends UIComponent> event){
    PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    
    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    pcontext.addUIComponentToUpdateByAjax(uiControl);
    
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
    pcontext.setFullRender(true);
    return uiWorkingWS;
  }
  
}
