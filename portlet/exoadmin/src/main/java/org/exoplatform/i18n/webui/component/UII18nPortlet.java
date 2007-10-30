package org.exoplatform.i18n.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/resources/webui/component/UII18nPortlet.gtmpl"
)
public class UII18nPortlet extends UIPortletApplication {
  public UII18nPortlet() throws Exception {
    
  }
}

