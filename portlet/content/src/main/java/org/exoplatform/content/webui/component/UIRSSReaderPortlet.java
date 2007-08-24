/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.content.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 30, 2006
 */
@ComponentConfig(
   lifecycle = UIApplicationLifecycle.class,
   template = "app:/groovy/rssreader/webui/component/UIRSSReaderPortlet.gtmpl"
)
public class UIRSSReaderPortlet extends UIPortletApplication {
  
  public UIRSSReaderPortlet() throws Exception {
    UIContentNavigation uiContentNav = addChild(UIContentNavigation.class, null, null) ; 
    addChild(UIContentWorkingArea.class, null, null) ;
    uiContentNav.refresh();
  }
  
}