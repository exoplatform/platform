/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.webui.util;
/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.page.UIDesktopPage;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.event.Event;

/**
 * Jun 5, 2006
 */
public class Util { 
  static public PortalRequestContext getPortalRequestContext() {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    if(!(context instanceof PortalRequestContext)) {
      context =  (WebuiRequestContext)context.getParentAppRequestContext() ;
    }
    return (PortalRequestContext)context ;
  }
  
  static public UIPortalApplication getUIPortalApplication() {
    return (UIPortalApplication)getPortalRequestContext().getUIApplication() ;
  }

  static public UIPortal getUIPortal() {
    return getUIPortalApplication().findFirstComponentOfType(UIPortal.class) ;
  }  

  static public UIPortalToolPanel getUIPortalToolPanel(){
    return getUIPortalApplication().findFirstComponentOfType(UIPortalToolPanel.class) ;    
  }  
  
  static  public void setShowEditControl(UIComponent uiComponent, Class clazz){
    if(uiComponent == null) return;
    if(uiComponent instanceof UIPortalComponent) {
      UIPortalComponent uiContainer = (UIPortalComponent) uiComponent;
      if(clazz.isInstance(uiContainer)){
        uiContainer.setShowEditControl(true);
      } else {
        uiContainer.setShowEditControl(false);
      }
    }
    if(uiComponent instanceof org.exoplatform.webui.core.UIContainer){
      List<UIComponent> children  = ((org.exoplatform.webui.core.UIContainer)uiComponent).getChildren();
      for(UIComponent comp : children ) setShowEditControl(comp, clazz);
      return;
    }
    
    if(uiComponent instanceof UIComponentDecorator) {
      UIComponentDecorator uiDecorator = (UIComponentDecorator) uiComponent;
      if(uiDecorator.getUIComponent() == null) return;
      setShowEditControl(uiDecorator.getUIComponent(), clazz);
    }
  }

  /**
   * View component on UIWorkspaceWorking
   * $uicomp : current component on UIWorkspaceWorking
   * $clazz : Class of component should show on UIWorkspaceWorking
   */
  static public <T extends UIComponent> T showComponentOnWorking(
      UIComponent uicomp, Class<T> clazz) throws Exception {
    UIPortalApplication uiPortalApp = uicomp.getAncestorOfType(UIPortalApplication.class);
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
    UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);   
    T uiWork = uiToolPanel.createUIComponent(clazz, null, null);
    uiToolPanel.setUIComponent(uiWork);
    uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
    return uiWork;
  }

  static public void showPortalComponentLayoutMode(UIPortalApplication uiPortalApp){   
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
    uiWorkingWS.setRenderedChild(UIPortal.class) ;
    UIPortal uiPortal = uiWorkingWS.getChild(UIPortal.class);    

    UIContainer uiContainer = Util.findUIComponent(uiPortal, UIContainer.class, UIPage.class);
    UIPage uiPage= uiPortal.findFirstComponentOfType(UIPage.class);
    UIPortlet uiPortlet=  Util.findUIComponent(uiPortal, UIPortlet.class, UIPage.class);

    String name = "";
    if(uiContainer != null && uiContainer.isShowEditControl())  name = "'UIContainer'";
    else if(uiPage != null && uiPage.isShowEditControl())  name = "'UIPage'";
    else if(uiPortlet != null  && uiPortlet.isShowEditControl()) name = "'UIPortlet'";

    getPortalRequestContext().getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPortal("+name+");");
  }
  
//  public UIPortlet getFirstUIPortlet(UIContainer uiContainer){
//    return 
//  }
  
//  public UIContainer getFirstUIContainer(){
//    return (UIContainer)Util.findUIComponent(UIContainer uiContainer, UIContainer.class, UIPage.class);
//  }

  static public void showPageComponentLayoutMode(UIPortalApplication uiPortalApp){   
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
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

  @SuppressWarnings("unchecked")
  static public <T extends UIComponent> T findUIComponent(UIComponent uiComponent, Class<T> clazz, Class ignoreClazz){
    if (clazz.isInstance(uiComponent)) return (T)uiComponent;
    if(!(uiComponent instanceof UIContainer)) return null;
    List<UIComponent> children = ((UIContainer)uiComponent).getChildren();    
    for(UIComponent child : children){
      if (clazz.isInstance(child)) return (T)child;      
      else if(!ignoreClazz.isInstance(child)){ 
        UIComponent value = findUIComponent(child, clazz, ignoreClazz);
        if(value != null) return (T)value;
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
    if(Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
      uiPage = uiParent.createUIComponent(context, UIDesktopPage.class, null, null);      	
    } else {
      uiPage = uiParent.createUIComponent(context, UIPage.class, null, null);      	
    }
    PortalDataMapper.toUIPage(uiPage, page);
    return uiPage;
  }

  static public void showComponentLayoutMode(Class clazz) throws Exception  {
    if(clazz == null) return;
    UIPortal uiPortal = getUIPortal();
    UIContainer uiParent  = null;
    if(uiPortal.isRendered()){
      uiPortal.setMaximizedUIComponent(null);
      uiParent = uiPortal;
    } else{
      UIPortalToolPanel uiPortalToolPanel = getUIPortalToolPanel();
      UIPage uiPage  = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      uiParent = uiPage;
    }
    if(uiParent == null) return;
    String layoutMode = clazz.getSimpleName();
    setShowEditControl(uiParent, clazz);
    
    PortalRequestContext context = Util.getPortalRequestContext() ;
    if(uiParent instanceof UIPortal){
      context.getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPortal('"+layoutMode+"');") ;
    } else {
      context.getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showLayoutModeForPage('"+layoutMode+"');") ;
    }
  }
  
  static public void showComponentEditInViewMode(Class clazz) throws Exception  {
    if(clazz == null) return;
    UIPortal uiPortal = getUIPortal();
    UIContainer uiParent  = null;
    if(uiPortal.isRendered()){
      uiPortal.setMaximizedUIComponent(null);
      uiParent = uiPortal;
    } else{
      UIPortalToolPanel uiPortalToolPanel = getUIPortalToolPanel();
      UIPage uiPage  = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      uiParent = uiPage;
    }
    if(uiParent == null) return;
    String layoutMode = clazz.getSimpleName();
    setShowEditControl(uiParent, clazz);
    
    PortalRequestContext context = Util.getPortalRequestContext() ;
//    if(uiParent instanceof UIPortal){
      context.getJavascriptManager().addCustomizedOnLoadScript("eXo.portal.UIPortal.showViewMode('"+layoutMode+"');") ;
//    }
  }
  
  static public UIWorkingWorkspace updateUIApplication(Event<? extends UIComponent> event){
    PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
    UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
    
    UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
    pcontext.setFullRender(true);
    return uiWorkingWS;
  }
  
}
