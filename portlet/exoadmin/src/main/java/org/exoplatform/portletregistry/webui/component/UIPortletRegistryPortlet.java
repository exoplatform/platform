package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/portletregistry/webui/component/UIPortletRegistryPortlet.gtmpl"
)
public class UIPortletRegistryPortlet extends UIPortletApplication {
  
  public UIPortletRegistryPortlet() throws Exception{
    addChild(UIPortletRegistryCategory.class, null, null);    
    addChild(UIWorkingArea.class, null, null);   
  }
  
}