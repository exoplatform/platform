/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.component.widget.UIPortalNavigation;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jul 3, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "system:/groovy/webui/component/UIApplication.gtmpl"
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UISiteMap",
    template = "system:/groovy/webui/component/UISitemap.gtmpl" ,
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})
public class UISitemapPortlet extends UIPortletApplication {
  
  public UISitemapPortlet() throws Exception {
    addChild(UIPortalNavigation.class, "UISiteMap", null) ;
  }

}
