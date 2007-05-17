/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/view/UIWidgetContainer.gtmpl"
)

public class UIWidgetContainer extends UIContainer {
  
  public UIWidgetContainer() throws Exception {
    UIWidget widgetSystem = addChild(UIWidget.class, null, null);
    UISystemWidgets systemWidgets = createUIComponent(UISystemWidgets.class, null, null);
    widgetSystem.setUIComponent(systemWidgets);
    
    UIWidget widgetInfo = addChild(UIWidget.class, null, null);
    UIInfoWidget uiInfoWidget = createUIComponent(UIInfoWidget.class, null, null);
    widgetInfo.setUIComponent(uiInfoWidget);
  }  
}
