/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.webui.navigation.UIPortalNavigation;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jul 3, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UISiteMap",
    template = "system:/groovy/webui/core/UISitemap.gtmpl" ,
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})
public class UISitemapPortlet extends UIPortletApplication {
  
  public UISitemapPortlet() throws Exception {
    addChild(UIPortalNavigation.class, "UISiteMap", null) ;
  }

}
