/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Author : lxchiati  
 *          lebienthuy@gmail.com
 * May 30, 2006
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "system:/groovy/webui/component/UIApplication.gtmpl" 
)

public class UITestFormPortlet extends UIPortletApplication {
  
  public UITestFormPortlet() throws Exception {
//    addChild(UITestTemplate.class, null, null) ;
    addChild(UITestForm.class, null, "formtest2") ;
  }
}
