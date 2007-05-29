/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.List;

import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.web.application.widget.WidgetApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig( 
  template = "system:/groovy/portal/webui/component/view/UIWidgetContainer.gtmpl",
  events = @EventConfig(listeners = UIWidgetContainer.DeleteWidgetActionListener.class)
)
public class UIWidgetContainer extends UIContainer {
  
  public UIWidgetContainer() throws Exception {
//    UIWidget widgetSystem = addChild(UIWidget.class, null, null);
//    UISystemWidgets systemWidgets = createUIComponent(UISystemWidgets.class, null, null);
//    widgetSystem.setUIComponent(systemWidgets);
//    UIWidget widgetInfo = addChild(UIWidget.class, null, null);
//    UIInfoWidget uiInfoWidget = createUIComponent(UIInfoWidget.class, null, null);
//    widgetInfo.setUIComponent(uiInfoWidget);
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIWidgetContainer> {
    public void execute(Event<UIWidgetContainer> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(OBJECTID);
//      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n DeleteWidgetActionListener "+id+" \n\n\n\n\n\n\n\n\n\n\n\n");
      UIWidgetContainer uiWidgetContainer = event.getSource();
      
      List<UIComponent> children = uiWidgetContainer.getChildren();
      for(UIComponent com: children){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n CHILD NAME: "+com.getName()+" \nCHILD ID: "+com.getId()+"  \n\n\n\n\n\n\n\n\n\n\n\n");
      }
      
      uiWidgetContainer.removeChildById(id) ;
      UIWelcomeComponent uiWelcomeComponent = uiWidgetContainer.getAncestorOfType(UIWelcomeComponent.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWelcomeComponent);
    }
  }
}
