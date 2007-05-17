/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/view/UIWidget.gtmpl"
)

public class UIWidget extends UIPortalComponent {
  private UIComponent component_;
  
  public UIComponent getUIComponent() {
    return component_;
  }
  
  public void setUIComponent(UIComponent com) { 
    component_ = com; 
  }
}