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
package org.exoplatform.portal.webui.container;

import java.util.List;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.portal.webui.application.UIAddNewApplication;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.application.UIWidgets;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
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
      WebuiRequestContext pContext = event.getRequestContext();
      String id = pContext.getRequestParameter(UIComponent.OBJECTID) ;
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
      pContext.setResponseComplete(true) ;
      pContext.getWriter().write(EventListener.RESULT_OK) ;
    }
  }
//  
//  static public class AddWidgetContainerActionListener extends EventListener<UIContainer> {
//    public void execute(Event<UIContainer> event) throws Exception {
//      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
//      UIContainer uiWidgetContainer = event.getSource();
//      
//      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n ADD WIDGET CONTAINER \n\n\n\n\n\n\n\n\n\n\n\n");
//      
//    }
//  }
  
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
