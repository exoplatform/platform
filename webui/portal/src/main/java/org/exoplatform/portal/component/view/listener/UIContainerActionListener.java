/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIContainerForm;
import org.exoplatform.portal.component.view.PortalDataMapper;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIWidget;
import org.exoplatform.portal.component.view.UIWidgets;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
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
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;       
    
      UIContainer child = uiContainer.getChildById(event.getRequestContext().getRequestParameter(UIContainer.OBJECTID));
      UIContainerForm containerForm = uiMaskWS.createUIComponent(UIContainerForm.class, null, null); 
      containerForm.setValues(child);
      uiMaskWS.setUIComponent(containerForm);
      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      Util.updateUIApplication(event);
    }
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      Iterator<UIComponent> iter = children.iterator();
      while(iter.hasNext()) {
        UIWidget uiWidget = (UIWidget) iter.next();
        if(uiWidget.getApplicationInstanceId().equals(id)) {
          iter.remove();
          break;
        }
      }
      
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
      
      UIWidgets uiWidgets = uiWidgetContainer.getAncestorOfType(UIWidgets.class);
      Widgets widgets = PortalDataMapper.toWidgets(uiWidgets);
      UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
      configService.update(widgets);
    }
  }
  
  static public class AddApplicationActionListener  extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      UIContainer uiWidgetContainer = event.getSource();
      String applicationId = event.getRequestContext().getRequestParameter("applicationId");  
      
      StringBuilder windowId = new StringBuilder(PortalConfig.PORTAL_TYPE);
      windowId.append(":/").append(applicationId).append('/');
      ApplicationRegistryService service = uiWidgetContainer.getApplicationComponent(ApplicationRegistryService.class) ;
      Application application = service.getApplication(applicationId);
      
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
      
      if(application == null) return;
      UIWidget uiWidget = uiWidgetContainer.createUIComponent(event.getRequestContext(), UIWidget.class, null, null);
      windowId.append(uiWidget.hashCode());
      uiWidget.setApplicationInstanceId(windowId.toString());
      uiWidget.setApplicationName(application.getApplicationName());
      uiWidget.setApplicationGroup(application.getApplicationGroup());
      uiWidget.setApplicationOwnerType(application.getApplicationType());
      uiWidget.setApplicationOwnerId(application.getOwner());

      uiWidgetContainer.addChild(uiWidget);

      String save = event.getRequestContext().getRequestParameter("save");
      if(save == null || !Boolean.valueOf(save).booleanValue()) return;
      UIWidgets uiWidgets = uiWidgetContainer.getAncestorOfType(UIWidgets.class);
      Widgets widgets = PortalDataMapper.toWidgets(uiWidgets);
      UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
      configService.update(widgets);
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
