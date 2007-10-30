package org.exoplatform.resources.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/resources/webui/component/UIResourcesPortlet.gtmpl"
)
public class UIResourcesPortlet extends UIPortletApplication {
  public UIResourcesPortlet() throws Exception {
    
  }
}