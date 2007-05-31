/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.config.model.Widgets;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import com.sun.corba.se.impl.oa.poa.AOMEntry;
/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig( 
  template = "system:/groovy/portal/webui/component/view/UIWidgetContainer.gtmpl",
  events = {
      @EventConfig(listeners = UIWidgetContainer.DeleteWidgetActionListener.class),
      @EventConfig(listeners = UIWidgetContainer.AddApplicationActionListener.class)
  }
)
public class UIWidgetContainer extends UIContainer {
  
  public UIWidgetContainer() throws Exception {
    
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIWidgetContainer> {
    public void execute(Event<UIWidgetContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIWidgetContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      Iterator<UIComponent> iter = children.iterator();
      while(iter.hasNext()) {
        UIWidget uiWidget = (UIWidget) iter.next();
        if(uiWidget.getApplicationId().equals(id)) {
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
  
  static public class AddApplicationActionListener  extends EventListener<UIWidgetContainer> {
    public void execute(Event<UIWidgetContainer> event) throws Exception {
      UIWidgetContainer uiWidgetContainer = event.getSource();
      String applicationId = event.getRequestContext().getRequestParameter("applicationId");  
      
      StringBuilder windowId = new StringBuilder(PortalConfig.PORTAL_TYPE);
      windowId.append(":/").append(applicationId).append('/');
      ApplicationRegistryService service = uiWidgetContainer.getApplicationComponent(ApplicationRegistryService.class) ;
      Application application = service.getApplication(applicationId);
      
      if(application != null) {
        UIWidget uiWidget = uiWidgetContainer.createUIComponent(event.getRequestContext(), UIWidget.class, null, null);
        windowId.append('/').append(uiWidget.hashCode());
        uiWidget.setApplicationInstanceId(windowId.toString());
        uiWidget.setApplicationName(application.getApplicationName());
        uiWidget.setApplicationGroup(application.getApplicationGroup());
        uiWidget.setApplicationOwnerType(application.getApplicationType());
        uiWidget.setApplicationOwnerId(application.getOwner());
        
        uiWidgetContainer.addChild(uiWidget);
        
        UIWidgets uiWidgets = uiWidgetContainer.getAncestorOfType(UIWidgets.class);
        Widgets widgets = PortalDataMapper.toWidgets(uiWidgets);
        UserPortalConfigService configService = uiWidgetContainer.getApplicationComponent(UserPortalConfigService.class);
        configService.update(widgets);
      }
      
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);      
    }
  }
}
