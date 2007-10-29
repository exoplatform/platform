/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.application;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
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
        @EventConfig(listeners = UIAddNewApplication.AddApplicationActionListener.class)
    }
)
    
public class UIAddNewApplication extends UIContainer {
  
  static public class AddApplicationActionListener extends EventListener<UIAddNewApplication> {
    
    public void execute(Event<UIAddNewApplication> event) throws Exception {
     
      //Class name of Components
      String strUIContainerClass = "org.exoplatform.portal.webui.container.UIContainer";
      String strUIPageClass = "org.exoplatform.portal.webui.page.UIPage";
     
      //Check the parent Component
      if(UI_COMPONENT_PARENT.getClass().getName().equals(strUIContainerClass)){
        //add to Left Container
        addApplicationToContainer(event);
        
      }else if(UI_COMPONENT_PARENT.getClass().getName().equals(strUIPageClass)){
        //add to Page
        addApplicationToPage(event);
      }
      
    }
    
    /***
     * Add Application to UiPage
     * @param event
     * @throws Exception
     */
    private void addApplicationToPage(Event<UIAddNewApplication> event) throws Exception{
      
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

      Application application = getApplication(uiPortal, applicationId);
      
      // review windowId for eXoWidget and eXoApplication
      if (org.exoplatform.web.application.Application.EXO_PORTLET_TYPE.equals(application
          .getApplicationType())) {
        UIPortlet uiPortlet = uiPage.createUIComponent(UIPortlet.class, null, null);

        StringBuilder windowId = new StringBuilder(uiPage.getOwnerType());
        windowId.append('#').append(uiPage.getOwnerId());
        windowId.append(":/").append(applicationId).append('/').append(uiPortlet.hashCode());
        uiPortlet.setWindowId(windowId.toString());

        if (application != null) {
          if (application.getDisplayName() != null) {
            uiPortlet.setTitle(application.getDisplayName());
          } else if (application.getApplicationName() != null) {
            uiPortlet.setTitle(application.getApplicationName());
          }
          uiPortlet.setDescription(application.getDescription());
        }
        uiPage.addChild(uiPortlet);
      } else if (org.exoplatform.web.application.Application.EXO_WIDGET_TYPE.equals(application
          .getApplicationType())) {
        UIWidget uiWidget = uiPage.createUIComponent(event.getRequestContext(), UIWidget.class,
            null, null);

        StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
        windowId.append(":/").append(applicationId).append('/').append(uiWidget.hashCode());
        uiWidget.setApplicationInstanceId(windowId.toString());

        uiWidget.setApplicationName(application.getApplicationName());
        uiWidget.setApplicationGroup(application.getApplicationGroup());
        uiWidget.setApplicationOwnerType(application.getApplicationType());

        //Set Properties For Widget
        int posX = (int) (Math.random() * 400);
        int posY = (int) (Math.random() * 200);
        uiWidget.getProperties().put(UIApplication.locationX, String.valueOf(posX));
        uiWidget.getProperties().put(UIApplication.locationY, String.valueOf(posY));

        uiPage.addChild(uiWidget);
        
      } else {
        
        UIExoApplication uiExoApp = uiPage.createUIComponent(UIExoApplication.class, null, null);

        StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
        windowId.append(":/").append(applicationId).append('/').append(uiExoApp.hashCode());
        uiExoApp.setApplicationInstanceId(windowId.toString());

        uiExoApp.init();
        uiPage.addChild(uiExoApp);
        
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
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);
      pcontext.setFullRender(true);
      
    }
    
    /***
     * Add Widget to Left Container
     * @param event
     * @throws Exception
     */
    private void addApplicationToContainer(Event<UIAddNewApplication> event) throws Exception{
      
      if(UIAddNewApplication.UI_COMPONENT_PARENT==null) return;
      
      UIContainer uiWidgetContainer = (UIContainer)UIAddNewApplication.UI_COMPONENT_PARENT;
      String applicationId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);  
      
      StringBuilder windowId = new StringBuilder(PortalConfig.USER_TYPE);
      windowId.append("#").append(event.getRequestContext().getRemoteUser()) ;
      windowId.append(":/").append(applicationId).append('/');
      ApplicationRegistryService service = uiWidgetContainer.getApplicationComponent(ApplicationRegistryService.class) ;
      Application application = service.getApplication(applicationId);

      if(application == null) return;
      UIWidget uiWidget = uiWidgetContainer.createUIComponent(event.getRequestContext(), UIWidget.class, null, null);
      windowId.append(uiWidget.hashCode());
      uiWidget.setApplicationInstanceId(windowId.toString());
      uiWidget.setApplicationName(application.getApplicationName());
      uiWidget.setApplicationGroup(application.getApplicationGroup());
      uiWidget.setApplicationOwnerType(application.getApplicationType());
      uiWidgetContainer.addChild(uiWidget);

      UIWidgets uiWidgets = uiWidgetContainer.getAncestorOfType(UIWidgets.class);
      Widgets widgets = PortalDataMapper.toWidgets(uiWidgets);
      UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
      configService.update(widgets);
      
      UIPortalApplication uiPortalApp = (UIPortalApplication)event.getRequestContext().getUIApplication() ;
      uiPortalApp.getUserPortalConfig().setWidgets(widgets) ;
      
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
      
    }
    
  }
  
  private static Application getApplication(UIPortal uiPortal, String id) throws Exception {

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

  
  public static List<ApplicationCategory> getApplicationCategories() throws Exception {
    
    return listAppCategories;
  }
   
  public List<ApplicationCategory> getApplicationCategories(String remoteUser, String[] applicationType) throws Exception {

    // Get Categories
    PortalContainer container = PortalContainer.getInstance();
    ApplicationRegistryService prService = (ApplicationRegistryService) container
        .getComponentInstanceOfType(ApplicationRegistryService.class);

    if(applicationType==null){
      applicationType = new String[] {};
    }

    List<ApplicationCategory> appCategories = prService.getApplicationCategories(remoteUser,
        applicationType);
    
    if (appCategories == null) {
      appCategories = new ArrayList();
    }
    listAppCategories = appCategories;

    return listAppCategories;

  }
  
  private static List<ApplicationCategory> listAppCategories;

  public static UIComponent UI_COMPONENT_PARENT = null;

}
