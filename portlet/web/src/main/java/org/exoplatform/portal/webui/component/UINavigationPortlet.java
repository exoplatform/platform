package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.component.widget.UIPortalNavigation;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "system:/groovy/webui/core/UIApplication.gtmpl"
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UIHorizontalNavigation",
    template = "app:/groovy/portal/webui/component/UIPortalNavigation.gtmpl" ,
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})

public class UINavigationPortlet extends UIPortletApplication {

  public UINavigationPortlet () throws  Exception { 
    addChild(UIPortalNavigation.class, "UIHorizontalNavigation", null);    
  }  
}