/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.container;

import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.application.UIWidgets;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 13, 2006
 */
public class UIContainerActionListener {  
  
  static public class EditContainerActionListener  extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
     
      UIContainer uiContainer = event.getSource();
      String id = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
     
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;       
      UIContainerForm containerForm = uiMaskWS.createUIComponent(UIContainerForm.class, null, null);
      if(uiContainer.getId().equals(id)){
        containerForm.setValues(uiContainer);
      } else {
        if(uiContainer.getChildById(id) !=null ){
          containerForm.setValues((UIContainer) uiContainer.getChildById(id));
        } else return ;
      }
      uiMaskWS.setUIComponent(containerForm);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      Util.updateUIApplication(event);
    }
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      int id  = Integer.valueOf(event.getRequestContext().getRequestParameter(UIComponent.OBJECTID)) ;
      UIContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      for(UIComponent uiChild : children) {
        UIWidget uiWidget = (UIWidget) uiChild ;
        if(uiWidget.getApplicationInstanceId().hashCode() == id) {
          children.remove(uiWidget) ;
          break ;
        }
      }
      
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
  
  static public class AddApplicationActionListener  extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      UIContainer uiWidgetContainer = event.getSource();
      String applicationId = event.getRequestContext().getRequestParameter("applicationId");  
      
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
//      uiWidget.setApplicationOwnerId(application.getOwner());
      uiWidgetContainer.addChild(uiWidget);

      String save = event.getRequestContext().getRequestParameter("save");
      if(save == null || !Boolean.valueOf(save).booleanValue()) return;
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
  
  static public class AddWidgetContainerActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIContainer uiWidgetContainer = event.getSource();
      
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n ADD WIDGET CONTAINER \n\n\n\n\n\n\n\n\n\n\n\n");
      
    }
  }
  
}
