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
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Gadgets;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL Author : 
 *  Anh Nguyen 
 *  ntuananh.vn@gmail.com
 * Oct 18, 2007
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/application/UIAddNewApplication.gtmpl",
    events = { 
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class),
        @EventConfig(listeners = UIAddNewApplication.AddApplicationActionListener.class),
        @EventConfig(listeners = UIAddNewApplication.AddToStartupActionListener.class)
    }
)
    
public class UIAddNewApplication extends UIContainer {
   
  static public class AddApplicationActionListener extends EventListener<UIAddNewApplication> {
    
    public void execute(Event<UIAddNewApplication> event) throws Exception {
      if(event.getSource().isInPage()) addApplicationToPage(event);        
      else addApplicationToContainer(event);
    }
    
    /***
     * Add Application to UiPage
     * @param event
     * @throws Exception
     */
    public void addApplicationToPage(Event<UIAddNewApplication> event) throws Exception{
      UIPortal uiPortal = Util.getUIPortal();
      
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIPage uiPage = null;
      if (uiPortal.isRendered()) {
        uiPage = uiPortal.findFirstComponentOfType(UIPage.class);
      } else {
        UIPortalToolPanel uiPortalToolPanel = uiPortalApp
            .findFirstComponentOfType(UIPortalToolPanel.class);
        uiPage = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      }

      String applicationId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);

      Application application = event.getSource().getApplication(applicationId);
      //TODO review windowId for eXoWidget and eXoApplication
      if (org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(application
          .getApplicationType())) {
        UIPortlet uiPortlet = uiPage.createUIComponent(UIPortlet.class, null, null);

        StringBuilder windowId = new StringBuilder(uiPage.getOwnerType());
        windowId.append('#').append(uiPage.getOwnerId());
        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/').append(uiPortlet.hashCode());
        uiPortlet.setWindowId(windowId.toString());
        uiPortlet.setPortletInPortal(false);

        if (application != null) {
          if (application.getDisplayName() != null) {
            uiPortlet.setTitle(application.getDisplayName());
          } else if (application.getApplicationName() != null) {
            uiPortlet.setTitle(application.getApplicationName());
          }
          uiPortlet.setDescription(application.getDescription());
          List<String> accessPers = application.getAccessPermissions() ;
          String[] accessPermissions = accessPers.toArray(new String[accessPers.size()]) ;
          uiPortlet.setAccessPermissions(accessPermissions) ;
          uiPortlet.setEditPermission(uiPage.getEditPermission()) ;
        }
        uiPage.addChild(uiPortlet);
        
//      } else if (org.exoplatform.web.application.Application.EXO_WIDGET_TYPE.equals(application
//          .getApplicationType())) {
//        UIWidget uiWidget = uiPage.createUIComponent(event.getRequestContext(), UIWidget.class,
//            null, null);
//
//        StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
//        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/').append(uiWidget.hashCode());
//        uiWidget.setApplicationInstanceId(windowId.toString());
//
//        //Set Properties For Widget
//        int posX = (int) (Math.random() * 400);
//        int posY = (int) (Math.random() * 200);
//        uiWidget.getProperties().put(UIApplication.locationX, String.valueOf(posX));
//        uiWidget.getProperties().put(UIApplication.locationY, String.valueOf(posY));
//
//        uiPage.addChild(uiWidget);
//        //TODO: dang.tung -- add new gadget
      } else if (org.exoplatform.web.application.Application.EXO_GAGGET_TYPE.equals(application
          .getApplicationType())) {
        UIGadget uiGadget = uiPage.createUIComponent(event.getRequestContext(), UIGadget.class, null, null);

        StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/').append(uiGadget.hashCode());
        uiGadget.setApplicationInstanceId(windowId.toString());
        uiGadget.setId("_" + uiGadget.hashCode()) ;
 
        //Set Properties For gadget
        int posX = (int) (Math.random() * 400);
        int posY = (int) (Math.random() * 200);
        
        uiGadget.getProperties().put(UIApplication.locationX, String.valueOf(posX));
        uiGadget.getProperties().put(UIApplication.locationY, String.valueOf(posY));

        uiPage.addChild(uiGadget);
      }

      //Save all changes
      if (uiPage.isModifiable()) {
        Page page = PortalDataMapper.toPageModel(uiPage);
        UserPortalConfigService configService = uiPortalApp
            .getApplicationComponent(UserPortalConfigService.class);
        if (page.getChildren() == null)
          page.setChildren(new ArrayList<Object>());
        configService.update(page);
      }

      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      
    }
    
    /***
     * Add Widget to Left Container
     * @param event
     * @throws Exception
     */
    public void addApplicationToContainer(Event<UIAddNewApplication> event) throws Exception{
      UIContainer uiWidgetContainer = (UIContainer)event.getSource().getUiComponentParent() ;
      String applicationId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      if(applicationId.contains("eXoGadgets")) {
        ApplicationRegistryService service = uiWidgetContainer.getApplicationComponent(ApplicationRegistryService.class) ;
        Application application = service.getApplication(applicationId);
        if(application == null) return;
        StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
        windowId.append("#").append(event.getRequestContext().getRemoteUser()) ;
        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/');
        UIGadget uiGadget = uiWidgetContainer.createUIComponent(event.getRequestContext(), UIGadget.class, null, null);
        windowId.append(uiGadget.hashCode());
        uiGadget.setApplicationInstanceId(windowId.toString());
        uiGadget.setId("_" + uiGadget.hashCode()) ;
        uiWidgetContainer.addChild(uiGadget);
  
        UIGadgets uiGadgets = uiWidgetContainer.getAncestorOfType(UIGadgets.class);
        Gadgets gadgets = PortalDataMapper.toGadgets(uiGadgets);
        UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
        configService.update(gadgets);
        
        UIPortalApplication uiPortalApp = (UIPortalApplication)event.getRequestContext().getUIApplication() ;
        uiPortalApp.getUserPortalConfig().setGadgets(gadgets) ;
        
        UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
        event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
//        return;
//      } else {
//        UIWidgets uiWidgets = uiWidgetContainer.getAncestorOfType(UIWidgets.class);
//        if(uiWidgets==null) return ;
//        ApplicationRegistryService service = uiWidgetContainer.getApplicationComponent(ApplicationRegistryService.class) ;
//        Application application = service.getApplication(applicationId);
//        if(application == null) return;
//        StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
//        windowId.append("#").append(event.getRequestContext().getRemoteUser()) ;
//        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/');
//        UIWidget uiWidget = uiWidgetContainer.createUIComponent(event.getRequestContext(), UIWidget.class, null, null);
//        windowId.append(uiWidget.hashCode());
//        uiWidget.setApplicationInstanceId(windowId.toString());
//        uiWidgetContainer.addChild(uiWidget);
//  
//        Widgets widgets = PortalDataMapper.toWidgets(uiWidgets);
//        UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
//        configService.update(widgets);
//        
//        UIPortalApplication uiPortalApp = (UIPortalApplication)event.getRequestContext().getUIApplication() ;
//        uiPortalApp.getUserPortalConfig().setWidgets(widgets) ;
//        
//        UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
//        event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
//      }
    }
    
  }
  
  static public class AddToStartupActionListener extends EventListener<UIAddNewApplication> {
    public void execute(Event<UIAddNewApplication> event) throws Exception {
      if(event.getSource().isInPage()) addApplicationToPage(event);        
    }
    
    /***
     * Add Application to UiPage
     * @param event
     * @throws Exception
     */
    public void addApplicationToPage(Event<UIAddNewApplication> event) throws Exception{
      UIPortal uiPortal = Util.getUIPortal();
      
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIPage uiPage = null;
      if (uiPortal.isRendered()) {
        uiPage = uiPortal.findFirstComponentOfType(UIPage.class);
      } else {
        UIPortalToolPanel uiPortalToolPanel = uiPortalApp
            .findFirstComponentOfType(UIPortalToolPanel.class);
        uiPage = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      }

      String applicationId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);

      Application application = event.getSource().getApplication(applicationId);
      //TODO review windowId for eXoWidget and eXoApplication
      if (org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(application.getApplicationType())) {
        UIPortlet uiPortlet = uiPage.createUIComponent(UIPortlet.class, null, null);

        StringBuilder windowId = new StringBuilder(uiPage.getOwnerType());
        windowId.append('#').append(uiPage.getOwnerId());
        windowId.append(":/").append(application.getApplicationGroup() + "/" + application.getApplicationName()).append('/')
          .append(uiPortlet.hashCode());
        uiPortlet.setWindowId(windowId.toString());
        uiPortlet.setPortletInPortal(false);
        uiPortlet.getProperties().setProperty("appStatus", "HIDE");

        if (application != null) {
          if (application.getDisplayName() != null) {
            uiPortlet.setTitle(application.getDisplayName());
          } else if (application.getApplicationName() != null) {
            uiPortlet.setTitle(application.getApplicationName());
          }
          uiPortlet.setDescription(application.getDescription());
          List<String> accessPers = application.getAccessPermissions() ;
          String[] accessPermissions = accessPers.toArray(new String[accessPers.size()]) ;
          uiPortlet.setAccessPermissions(accessPermissions) ;
          uiPortlet.setEditPermission(uiPage.getEditPermission()) ;
        }
        uiPage.addChild(uiPortlet);
      }

      //Save all changes
      if (uiPage.isModifiable()) {
        Page page = PortalDataMapper.toPageModel(uiPage);
        UserPortalConfigService configService = uiPortalApp
            .getApplicationComponent(UserPortalConfigService.class);
        if (page.getChildren() == null)
          page.setChildren(new ArrayList<Object>());
        configService.update(page);
      }

      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      
    }
  }
  
  private Application getApplication(String id) throws Exception {

    List<ApplicationCategory> pCategories = getApplicationCategories();
    
    for (ApplicationCategory pCategory : pCategories) {
      List<Application> applications = pCategory.getApplications();
      for (Application application : applications) {
        if (application.getId().equals(id))
          return application;
      }
    }

    return null;
  }

  
  public List<ApplicationCategory> getApplicationCategories() throws Exception {
    
    return listAppCategories;
  }
   
  public List<ApplicationCategory> getApplicationCategories(String remoteUser, String[] applicationType) throws Exception {
    ExoContainer container  = ExoContainerContext.getCurrentContainer();
    ApplicationRegistryService prService = (ApplicationRegistryService) container
        .getComponentInstanceOfType(ApplicationRegistryService.class);

    if(applicationType==null){
      applicationType = new String[] {};
    }

    List<ApplicationCategory> appCategories = prService.getApplicationCategories(remoteUser,
        applicationType);
    
    if (appCategories == null) {
      appCategories = new ArrayList();
    } else {
      Iterator<ApplicationCategory> cateItr = appCategories.iterator() ;
      while(cateItr.hasNext()) {
        ApplicationCategory cate = cateItr.next() ;
        List<Application> applications = cate.getApplications() ;
        if(applications.size()<1) cateItr.remove() ;
      }
    }
    listAppCategories = appCategories;

    return listAppCategories;

  }
  
  private List<ApplicationCategory> listAppCategories;

  private UIComponent uiComponentParent;
  
  private boolean isInPage;

  public UIComponent getUiComponentParent() {
    return uiComponentParent;
  }

  public void setUiComponentParent(UIComponent uiComponentParent) {
    this.uiComponentParent = uiComponentParent;
  }

  public boolean isInPage() {
    return isInPage;
  }

  public void setInPage(boolean isInPage) {
    this.isInPage = isInPage;
  }

}
