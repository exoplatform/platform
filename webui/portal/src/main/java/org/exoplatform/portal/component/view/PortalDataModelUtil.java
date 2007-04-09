/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Component;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageBody;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.services.portletcontainer.pci.model.Supports;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Aug 12, 2006  
 */
public class PortalDataModelUtil {

  @SuppressWarnings("unchecked")
  static private <T extends Component> T buildChild(UIComponent uiComponent, boolean recursive){
    Component model = null;
    if(uiComponent instanceof UIPageBody){
      model =  toPageBodyModel((UIPageBody)uiComponent);
    }else if(uiComponent instanceof UIPortlet){
      model = toPortletModel((UIPortlet)uiComponent);
    } else if(uiComponent instanceof UIContainer){       
      model = toContainerModel((UIContainer) uiComponent, recursive);
    }
    return (T)model;
  }

  static private void toPortalComponent(Component model, UIPortalComponent uiPortalComponent){
    model.setId(uiPortalComponent.getId());
    model.setFactoryId(uiPortalComponent.getFactoryId());
    model.setTemplate(uiPortalComponent.getTemplate());
    model.setDecorator(uiPortalComponent.getDecorator());
    model.setHeight(uiPortalComponent.getHeight());
    model.setWidth(uiPortalComponent.getWidth());
    model.setModifiable(uiPortalComponent.isModifiable());   
  }

  static public Container toContainerModel(UIContainer uiContainer , boolean recursive){
    Container model  = new Container();
    toPortalComponent(model , uiContainer);
    model.setTitle(uiContainer.getTitle());        
    model.setIcon(uiContainer.getIcon());
    List<UIComponent> children  = uiContainer.getChildren();
    if(!recursive || children == null)  return model;
    ArrayList<Component>  newChildren= new ArrayList<Component>();
    for(UIComponent child : children){   
      newChildren.add(buildChild(child, recursive));
    }
    model.setChildren(newChildren);
    return model;
  }

  static public Application toPortletModel(UIPortlet uiPortlet){
    Application model = new Application();
    toPortalComponent(model , uiPortlet);
    model.setApplicationInstanceId(uiPortlet.getWindowId());
    model.setShowInfoBar(uiPortlet.getShowInfoBar());
    model.setShowApplicationMode(uiPortlet.getShowWindowState());
    model.setShowApplicationMode(uiPortlet.getShowPortletMode());    
    model.setTitle(uiPortlet.getTitle());
    model.setIcon(uiPortlet.getIcon());
    return model;
  }

  static public Page toPageModel(UIPage uiPage, boolean recursive){
    Page model = new Page();
    toPortalComponent(model , uiPage);
    model.setOwner(uiPage.getOwner());
    model.setName(uiPage.getName());
    model.setIcon(uiPage.getIcon());
    model.setViewPermission(uiPage.getViewPermission());
    model.setEditPermission(uiPage.getEditPermission());
    model.setTitle(uiPage.getTitle());
    model.setShowMaxWindow(uiPage.isShowMaxWindow());
    List<UIComponent> children  = uiPage.getChildren();
    if(!recursive || children == null)  return model;
    ArrayList<Component>  newChildren= new ArrayList<Component>();
    for(UIComponent child : children){   
      if(child  instanceof UIJSApplication) continue;
      newChildren.add(buildChild(child, recursive));
    }
    model.setChildren(newChildren);
    return model;
  }

  static public PortalConfig toPortalConfig(UIPortal uiPortal, boolean recursive){
    PortalConfig model = new PortalConfig();
    toPortalComponent(model , uiPortal);
    model.setOwner(uiPortal.getOwner());    
    model.setLocale(uiPortal.getLocale());
    model.setSkin(uiPortal.getSkin());
    model.setViewPermission(uiPortal.getViewPermission());
    model.setEditPermission(uiPortal.getEditPermission());
    model.setTitle(uiPortal.getTitle());
    List<UIComponent> children  = uiPortal.getChildren();
    if(!recursive || children == null)  return model;
    ArrayList<Component>  newChildren= new ArrayList<Component>();
    for(UIComponent child : children){   
      newChildren.add(buildChild(child, recursive));
    }
    model.getPortalLayout().setChildren(newChildren);
    return model;
  }

  static public PageBody toPageBodyModel(UIPageBody uiBody){
    PageBody model = new PageBody();
    model.setId(uiBody.getId());
    model.setDecorator(uiBody.getDecorator());
    model.setHeight(uiBody.getHeight());
    model.setTemplate(uiBody.getTemplate());
    model.setWidth(uiBody.getWidth());
    model.setModifiable(uiBody.isModifiable());
    return model;
  }
  
  static private void toUIPortalComponent(UIPortalComponent uiPortalComponent, Component model){
    uiPortalComponent.setId(model.getId());
    uiPortalComponent.setFactoryId(model.getFactoryId());
    if(model.getTemplate() != null && model.getTemplate().length() > 0){
      uiPortalComponent.setTemplate(model.getTemplate());
    }
    uiPortalComponent.setDecorator(model.getDecorator());
    uiPortalComponent.setWidth(model.getWidth());
    uiPortalComponent.setHeight(model.getHeight());
    uiPortalComponent.setModifiable(model.isModifiable());
  }
  
  static public void toUIContainer(UIContainer uiContainer, Container model, 
                                   boolean recursive) throws Exception {
    toUIPortalComponent(uiContainer, model);
    uiContainer.setTitle(model.getTitle());
    uiContainer.setIcon(model.getIcon());
    List<Component> children  = model.getChildren();
    if(!recursive || children == null)  return;
    for(Component child : children){   
      uiContainer.addChild(buildChild(uiContainer,  child, recursive));
    }
  }
  
  static public void toUIPortlet(UIPortlet uiPortlet, Application model) throws Exception {
    toUIPortalComponent(uiPortlet, model);
    uiPortlet.setWindowId(model.getApplicationInstanceId());
    uiPortlet.setShowInfoBar(model.getShowInfoBar());
    uiPortlet.setShowWindowState(model.getShowApplicationState());
    uiPortlet.setShowPortletMode(model.getShowApplicationMode());
    uiPortlet.setTitle(model.getTitle());
    uiPortlet.setIcon(model.getIcon());
    uiPortlet.setDescription(model.getDescription());
    initPortletMode(uiPortlet);
  }
  
  static private void initPortletMode(UIPortlet uiPortlet) throws Exception {
    PortletContainerService portletContainer =  uiPortlet.getApplicationComponent(PortletContainerService.class);
    ExoWindowID windowId = uiPortlet.getExoWindowID();    
    String  portletId = windowId.getPortletApplicationName() +"/"+windowId.getPortletName();   
    PortletData portletData = (PortletData) portletContainer.getAllPortletMetaData().get(portletId);
    if(portletData == null) return;
    List supportsList = portletData.getSupports() ;
    List<String> supportModes = new ArrayList<String>() ;
    for (int i = 0; i < supportsList.size(); i++) {
      Supports supports = (Supports) supportsList.get(i) ;
      String mimeType = supports.getMimeType() ;
      if ("text/html".equals(mimeType)) {
        List modes = supports.getPortletMode() ;
        for (int j =0 ; j < modes.size() ; j++) {
          String mode =(String)modes.get(j) ;
          mode = mode.toLowerCase() ;
          //check role admin
          if("config".equals(mode)) { 
            //if(adminRole) 
            supportModes.add(mode) ;
          } else {
            supportModes.add(mode) ;
          }
        }
        break ;
      }
    }
    if(supportModes.size() > 1) supportModes.remove("view");
    uiPortlet.setSupportModes(supportModes);
  }
  
  static public void toUIPage(UIPage uiPage, Page model,
                              boolean recursive) throws Exception {
    toUIPortalComponent(uiPage, model);
    uiPage.setId(model.getPageId()) ;
    uiPage.setOwner(model.getOwner());
    uiPage.setName(model.getName());
    uiPage.setIcon(model.getIcon());
    uiPage.setViewPermission(model.getViewPermission());
    uiPage.setEditPermission(model.getEditPermission());
    uiPage.setTitle(model.getTitle());    
    uiPage.setShowMaxWindow(model.isShowMaxWindow());
    List<Component> children  = model.getChildren();
    if(!recursive || children == null)  return;
    for(Component child : children){   
      uiPage.addChild(buildChild(uiPage,  child, recursive));
    }
  }
  
  static public void toUIPortal(UIPortal uiPortal, UserPortalConfig userPortalConfig, 
                                boolean recursive) throws Exception {
    PortalConfig model = userPortalConfig.getPortalConfig();
    toUIPortalComponent(uiPortal, model);
    uiPortal.setUserPortalConfig(userPortalConfig);
    uiPortal.setOwner(model.getOwner());
    uiPortal.setLocale(model.getLocale());
    uiPortal.setSkin(model.getSkin());
    uiPortal.setViewPermission(model.getViewPermission());
    uiPortal.setEditPermission(model.getEditPermission());
    uiPortal.setTitle(model.getTitle());
    uiPortal.setId("UIPortal") ;
    List<Component> children  = model.getPortalLayout().getChildren();
    if(!recursive || children == null)  return;
    for(Component child : children){   
      uiPortal.addChild(buildChild(uiPortal,  child, recursive));
    }
    uiPortal.setNavigation(userPortalConfig.getNavigations());    
  }
  
  static public void toUIPageBody(UIPageBody uiBody, PageBody model){
    uiBody.setId(model.getId());
    uiBody.setTemplate(model.getTemplate());
    uiBody.setDecorator(model.getDecorator());
    uiBody.setWidth(model.getWidth());
    uiBody.setHeight(model.getHeight());
    uiBody.setModifiable(model.isModifiable());
  }
  
  @SuppressWarnings("unchecked")
  static private <T extends UIComponent> T buildChild(UIContainer uiParent, Component model, 
                                                      boolean recursive) throws Exception {
    UIComponent uiComponent = null;
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    if(model instanceof PageBody){
      UIPageBody uiPageBody = uiParent.createUIComponent(context, UIPageBody.class, model.getFactoryId(), null);
      toUIPageBody(uiPageBody, (PageBody)model);
      uiComponent = uiPageBody;
    }else if(model instanceof Application){
      Application application = (Application) model;
      String factoryId = application.getFactoryId();       
      if(factoryId == null || factoryId.equals(Application.TYPE_PORTLET)){
        UIPortlet uiPortlet = uiParent.createUIComponent(context, UIPortlet.class, model.getFactoryId(), null);
        toUIPortlet(uiPortlet, application);
        uiComponent = uiPortlet;
      }
    } else if(model instanceof Container){
      UIContainer uiContainer = uiParent.createUIComponent(context, UIContainer.class, model.getFactoryId(), null);
      toUIContainer(uiContainer, (Container)model, recursive);
      uiComponent = uiContainer;
    }
    return (T)uiComponent;
  }

}
