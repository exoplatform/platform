/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.container;

import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIAddNewApplication;
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
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID) ;
      UIContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      for(UIComponent uiChild : children) {
        UIWidget uiWidget = (UIWidget) uiChild ;
        if(uiWidget.getApplicationInstanceUniqueId().equals(id)) {
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
  
  static public class AddWidgetContainerActionListener extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIContainer uiWidgetContainer = event.getSource();
      
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n ADD WIDGET CONTAINER \n\n\n\n\n\n\n\n\n\n\n\n");
      
    }
  }
  
  static public class ShowAddNewApplicationActionListener extends EventListener<UIContainer> {

    @Override
    public void execute(Event<UIContainer> event) throws Exception {
         
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWorkspace = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;  
      
      UIAddNewApplication uiAddApplication = uiPortal.createUIComponent(UIAddNewApplication.class,
          null, null);
      //get Widget Applications only
      String[] applicationTypes = {org.exoplatform.web.application.Application.EXO_WIDGET_TYPE};
      
      //Set parent container
      uiAddApplication.setInPage(false);
      uiAddApplication.setUiComponentParent(event.getSource());
      uiAddApplication.getApplicationCategories(event.getRequestContext().getRemoteUser(),applicationTypes);

      uiMaskWorkspace.setWindowSize(700, 375);
      uiMaskWorkspace.setUIComponent(uiAddApplication);
      uiMaskWorkspace.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);
      
    }

  }
  
}
