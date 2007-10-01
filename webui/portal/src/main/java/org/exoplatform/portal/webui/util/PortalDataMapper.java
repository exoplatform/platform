/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.util;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.Constants;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.Application;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageBody;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.application.UIExoApplication;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.application.UIWidgets;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.services.portletcontainer.PortletContainerService;
import org.exoplatform.services.portletcontainer.pci.ExoWindowID;
import org.exoplatform.services.portletcontainer.pci.PortletData;
import org.exoplatform.services.portletcontainer.pci.model.Supports;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
/**
 * Created by The eXo Platform SAS
 * May 4, 2007  
 * 
 * TODO: Rename this to PortalDataModelMapper
 */
public class PortalDataMapper {
  
  @SuppressWarnings("unchecked")
  static final public <T> T buildChild(UIComponent uiComponent){
    Object model = null;
    if(uiComponent instanceof UIPageBody){
      model =  toPageBodyModel((UIPageBody)uiComponent);
    }else if(uiComponent instanceof UIExoApplication){
      model = toExoApplication((UIExoApplication)uiComponent);
    }else if(uiComponent instanceof UIWidget){
      model = toWidget((UIWidget)uiComponent);  
    }else if(uiComponent instanceof UIPortlet){
      model = toPortletModel((UIPortlet)uiComponent);
    } else if(uiComponent instanceof UIContainer){       
      model = toContainer((UIContainer) uiComponent);
    }
    return (T)model;
  }
  
  static final public Application toWidget(UIWidget uiWidget) {
    Application model = new Application();
    model.setApplicationType(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE);
    model.setInstanceId(uiWidget.getApplicationInstanceId());
    model.setId(uiWidget.getId());
    model.setProperties(uiWidget.getProperties());
    return model;
  }
  
  static final public Application toExoApplication(UIExoApplication uiExoApp) {
    Application model = new Application();
    model.setApplicationType(org.exoplatform.web.application.Application.EXO_APPLICATION_TYPE);
    model.setId(uiExoApp.getId());
    model.setInstanceId(uiExoApp.getApplicationInstanceId()) ;
    model.setShowInfoBar(uiExoApp.getShowInfoBar());
    model.setShowApplicationState(uiExoApp.getShowWindowState());
    model.setTitle(uiExoApp.getTitle());
    model.setIcon(uiExoApp.getIcon());
    model.setDescription(uiExoApp.getDescription());
    model.setProperties(uiExoApp.getProperties());
    return model;
  }
  
  static private void toContainer(Container model, UIContainer uiContainer) {
    model.setId(uiContainer.getId());
    model.setName(uiContainer.getName());
    model.setTitle(uiContainer.getTitle());
    model.setIcon(uiContainer.getIcon());
    model.setHeight(uiContainer.getHeight());
    model.setWidth(uiContainer.getWidth());
    model.setTemplate(uiContainer.getTemplate());
    model.setFactoryId(uiContainer.getFactoryId());
    
    List<UIComponent> uiChildren = uiContainer.getChildren();
    if(uiChildren == null)  return ;
    ArrayList<Object>  children = new ArrayList<Object>();
    for(UIComponent child : uiChildren){ 
      Object component = buildChild(child);
      if(component != null) children.add(component);
    }
    model.setChildren(children);
  }
  
  static final public Application toPortletModel(UIPortlet uiPortlet){
    Application model = new Application();
    model.setInstanceId(uiPortlet.getWindowId().toString());
    model.setApplicationType(uiPortlet.getFactoryId());
    model.setTitle(uiPortlet.getTitle());    
    model.setDescription(uiPortlet.getDescription());
    model.setShowInfoBar(uiPortlet.getShowInfoBar());
    model.setShowApplicationState(uiPortlet.getShowWindowState());
    model.setShowApplicationMode(uiPortlet.getShowPortletMode());    
    model.setDescription(uiPortlet.getDescription());
    model.setIcon(uiPortlet.getIcon());
    model.setProperties(uiPortlet.getProperties());
    return model;
  }
  
  static final public Container toContainer(UIContainer uiContainer){
    Container model = new Container();
    toContainer(model, uiContainer);
    return model;
  }
  
  static final public Page toPageModel(UIPage uiPage){
    Page model = new Page();
    toContainer(model , uiPage);
    model.setCreator(uiPage.getCreator());
    model.setModifier(uiPage.getModifier());
    model.setOwnerId(uiPage.getOwnerId());
    model.setOwnerType(uiPage.getOwnerType());
    model.setIcon(uiPage.getIcon());
    model.setPageId(uiPage.getPageId());
    model.setAccessPermissions(uiPage.getAccessPermissions());
    model.setEditPermission(uiPage.getEditPermission());
    model.setFactoryId(uiPage.getFactoryId());
    model.setShowMaxWindow(uiPage.isShowMaxWindow());   
    model.setModifiable(uiPage.isModifiable());
    return model;
  }
  
  static final public PortalConfig toPortal(UIPortal uiPortal){
    PortalConfig model = new PortalConfig();
    model.setName(uiPortal.getName());
    model.setCreator(uiPortal.getCreator());
    model.setModifier(uiPortal.getModifier());
    model.setFactoryId(uiPortal.getFactoryId());
    model.setAccessPermissions(uiPortal.getAccessPermissions());
    model.setEditPermission(uiPortal.getEditPermission());
    model.setLocale(uiPortal.getLocale());
    model.setSkin(uiPortal.getSkin());
    model.setTitle(uiPortal.getTitle());
    model.setModifiable(uiPortal.isModifiable());
   
    List<UIComponent> children  = uiPortal.getChildren();
    if(children == null)  return  model;
    ArrayList<Object>  newChildren = new ArrayList<Object>();
    for(UIComponent child : children){ 
      Object component = buildChild(child);
      if(component != null) newChildren.add(component);
    }
    model.getPortalLayout().setChildren(newChildren);
    return model;
  }
  
  @SuppressWarnings("unused")
  static final public PageBody toPageBodyModel(UIPageBody uiPageBody){
    return new PageBody();
  }
  
  static final public Widgets toWidgets(UIWidgets uiWidgets) throws Exception {
    Widgets model = new Widgets();
    model.setAccessPermissions(uiWidgets.getAccessPermissions());
    model.setEditPermission(uiWidgets.getEditPermission());
    model.setOwnerType(uiWidgets.getOwnerType());
    model.setOwnerId(uiWidgets.getOwnerId());
    
    List<UIComponent> uiChildren  = uiWidgets.getChildren();
    if(uiChildren == null)  return model;
    ArrayList<Container> modelChildren = new ArrayList<Container>();
    for(UIComponent uiChild : uiChildren) {
      Container container = toContainer((UIContainer)uiChild) ;
      modelChildren.add(container) ;
    }
    model.setChildren(modelChildren);
    return model;
  }
  
  
  
  
//  ************************************************************************************************

  
  
  
  static public void toUIExoApplication(UIExoApplication uiExoApp, Application model) throws Exception {
    uiExoApp.setApplicationInstanceId(model.getInstanceId()) ;
    uiExoApp.setShowInfoBar(model.getShowInfoBar());
    uiExoApp.setShowWindowState(model.getShowApplicationState());
    uiExoApp.setTitle(model.getTitle());
    uiExoApp.setIcon(model.getIcon());
    uiExoApp.setDescription(model.getDescription());
    uiExoApp.setProperties(model.getProperties());
    uiExoApp.init() ;
  }
  
  static public void toUIWidget(UIWidget uiWidget, Application model) throws Exception {
    uiWidget.setApplicationInstanceId(model.getInstanceId()) ;
    uiWidget.setId(model.getInstanceId());
    uiWidget.setProperties(model.getProperties());
  }
  
  /**
   * Fill the UI component with both information from the persistent model and some coming
   * from the portlet.xml defined by the JSR 286 specification
   */
  static public void toUIPortlet(UIPortlet uiPortlet, Application model) throws Exception {
	/*
	 * Fill UI component object with info from the XML file that persist portlet information
	 */
    uiPortlet.setWindowId(model.getInstanceId());
    uiPortlet.setTitle(model.getTitle());
    uiPortlet.setIcon(model.getIcon());
    uiPortlet.setDescription(model.getDescription());
    uiPortlet.setFactoryId(model.getApplicationType());    
    uiPortlet.setShowInfoBar(model.getShowInfoBar());
    uiPortlet.setShowWindowState(model.getShowApplicationState());
    uiPortlet.setShowPortletMode(model.getShowApplicationMode());
    uiPortlet.setProperties(model.getProperties());
  
    PortletContainerService portletContainer =  uiPortlet.getApplicationComponent(PortletContainerService.class);
    ExoWindowID windowId = uiPortlet.getExoWindowID();    
    String  portletId = windowId.getPortletApplicationName() + Constants.PORTLET_META_DATA_ENCODER + windowId.getPortletName();   
    PortletData portletData = (PortletData) portletContainer.getAllPortletMetaData().get(portletId);
    if(portletData == null) return;
    
    /*
     * Define which portlet modes the portlet supports and hence should be shown in the portlet
     * info bar
     */
    List<?> supportsList = portletData.getSupports() ;
    List<String> supportModes = new ArrayList<String>() ;
    for (int i = 0; i < supportsList.size(); i++) {
      Supports supports = (Supports) supportsList.get(i) ;
      String mimeType = supports.getMimeType() ;
      if ("text/html".equals(mimeType)) {
        List<?> modes = supports.getPortletMode() ;
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
  
  static public void toUIContainer(UIContainer uiContainer, Container model) throws Exception {
    uiContainer.setId(model.getId());
    uiContainer.setWidth(model.getWidth());
    uiContainer.setHeight(model.getHeight());
    uiContainer.setTitle(model.getTitle());
    uiContainer.setIcon(model.getIcon());
    uiContainer.setFactoryId(model.getFactoryId());
    uiContainer.setName(model.getName());
    uiContainer.setTemplate(model.getTemplate());
    
    List<Object> children  = model.getChildren();
    if(children == null)  return;
    for(Object child : children) {
      UIComponent uiComp = buildChild(uiContainer, child);
      if(uiComp != null) uiContainer.addChild(uiComp);
    }
  }
  
  static public void toUIPage(UIPage uiPage, Page model) throws Exception {
    toUIContainer(uiPage, model);
    uiPage.setCreator(model.getCreator());
    uiPage.setModifier(model.getModifier());
    uiPage.setOwnerId(model.getOwnerId());
    uiPage.setOwnerType(model.getOwnerType());
    uiPage.setIcon(model.getIcon());
    uiPage.setAccessPermissions(model.getAccessPermissions());
    uiPage.setEditPermission(model.getEditPermission());
    uiPage.setFactoryId(model.getFactoryId());
    uiPage.setPageId(model.getPageId());
    uiPage.setShowMaxWindow(model.isShowMaxWindow());   
    uiPage.setModifiable(model.isModifiable());
  }
  
  static public void toUIPortal(UIPortal uiPortal, UserPortalConfig userPortalConfig) throws Exception {
    PortalConfig model = userPortalConfig.getPortalConfig();
    
    uiPortal.setId("UIPortal") ; 
    uiPortal.setCreator(model.getCreator());
    uiPortal.setModifier(model.getModifier());
    uiPortal.setName(model.getName());
    uiPortal.setFactoryId(model.getFactoryId());
    uiPortal.setOwner(model.getName());
    uiPortal.setTitle(model.getTitle());
    uiPortal.setModifiable(model.isModifiable());
    
    uiPortal.setLocale(model.getLocale());
    uiPortal.setSkin(model.getSkin());
    uiPortal.setAccessPermissions(model.getAccessPermissions());
    uiPortal.setEditPermission(model.getEditPermission());
    
    List<Object> children  = model.getPortalLayout().getChildren();
    if(children != null) { 
      for(Object child : children){   
        UIComponent uiComp = buildChild(uiPortal, child);
        if(uiComp != null) uiPortal.addChild(uiComp);
      }
    }
    uiPortal.setNavigation(userPortalConfig.getNavigations());   
  }
  
  static public void toUIWidgets(UIWidgets uiWidgets, Widgets model) throws Exception {
    uiWidgets.setId(model.getId());
    uiWidgets.setAccessPermissions(model.getAccessPermissions());
    uiWidgets.setEditPermission(model.getEditPermission());
    uiWidgets.setOwnerType(model.getOwnerType());
    uiWidgets.setOwnerId(model.getOwnerId());
    
    ArrayList<Container> children  = model.getChildren();
    if(children == null)  return;
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    for(Container child : children) { 
      UIContainer uiContainer = uiWidgets.createUIComponent(context, UIContainer.class, "WidgetContainer", null);
      uiContainer.setRendered(false);
      toUIContainer(uiContainer, child);
      uiWidgets.addChild(uiContainer);
    }
    uiWidgets.updateDropdownList();
  }
  
  
  @SuppressWarnings("unchecked")
  static private <T extends UIComponent> T buildChild(UIPortalComponent uiParent, Object model) throws Exception {
    UIComponent uiComponent = null;
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    if(model instanceof PageBody){
      UIPageBody uiPageBody = uiParent.createUIComponent(context, UIPageBody.class, null, null);
      uiComponent = uiPageBody;
    }else if(model instanceof Application){
      Application application = (Application) model;
      String factoryId = application.getApplicationType();    
      if(factoryId == null || factoryId.equals(org.exoplatform.web.application.Application.EXO_PORTLET_TYPE)){
        UIPortlet uiPortlet = uiParent.createUIComponent(context, UIPortlet.class, null, null);
        toUIPortlet(uiPortlet, application);
        uiComponent = uiPortlet;
      } else if(factoryId.equals(org.exoplatform.web.application.Application.EXO_APPLICATION_TYPE)) {
        UIExoApplication uiExoApp = 
          uiParent.createUIComponent(context, UIExoApplication.class, null, null);
        toUIExoApplication(uiExoApp, application) ;
//        uiExoApp.init() ;
        uiComponent = uiExoApp ;
      } else if(factoryId.equals(org.exoplatform.web.application.Application.EXO_WIDGET_TYPE)) {
        UIWidget uiWidget = uiParent.createUIComponent(context, UIWidget.class, null, null);
        toUIWidget(uiWidget, application) ;
        uiComponent = uiWidget ;
      }
    } else if(model instanceof Container){
      Container container = (Container) model;
      UIContainer uiContainer = uiParent.createUIComponent(context, UIContainer.class, container.getFactoryId(), null);
      toUIContainer(uiContainer, (Container)model);
      uiComponent = uiContainer;
    }
    return (T)uiComponent;
  }

}
