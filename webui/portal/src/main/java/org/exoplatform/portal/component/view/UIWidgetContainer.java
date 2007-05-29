/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

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
  
  static public class DeleteWidgetActionListener extends EventListener<UIWidget> {
    public void execute(Event<UIWidget> event) throws Exception {
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n DeleteWidgetActionListener \n\n\n\n\n\n\n\n\n\n\n\n");
      String id  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIWidget uiWidget = event.getSource();
      UIContainer parentWidget = (UIContainer)uiWidget.getParent() ; 
      parentWidget.removeChildById(id) ;
    }
  }
}
