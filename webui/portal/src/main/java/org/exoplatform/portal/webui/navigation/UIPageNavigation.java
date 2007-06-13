/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 11, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "system:/groovy/webui/core/UIApplication.gtmpl"
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UIVerticalNavigation",
    template = "system:/groovy/portal/webui/navigation/UIPageNavigation.gtmpl",
    events = {
      @EventConfig( listeners = UIPortalNavigation.SelectNodeActionListener.class ),
      @EventConfig( listeners = UIPortalNavigation.UpLevelActionListener.class )
    }
  )
})

public class UIPageNavigation extends UIContainer {
  
  public UIPageNavigation() throws Exception {
    addChild(UIPortalNavigation.class, "UIVerticalNavigation", null) ;
  }
  
}
