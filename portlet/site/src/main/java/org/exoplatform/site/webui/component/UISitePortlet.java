/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.site.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Sep 27, 2006  
 */
@ComponentConfig(
    lifecycle =UIApplicationLifecycle.class,
    template = "system:/groovy/webui/core/UIApplication.gtmpl"
)
public class UISitePortlet extends UIPortletApplication{
  public UISitePortlet() throws Exception {
    addChild(UISite.class, null, null) ;
  }
}
