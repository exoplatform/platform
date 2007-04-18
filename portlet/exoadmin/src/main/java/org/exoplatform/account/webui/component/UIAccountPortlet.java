package org.exoplatform.account.webui.component;

import org.exoplatform.organization.webui.component.UIAccountForm;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;

@ComponentConfig(  
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/account/webui/component/UIAccountPortlet.gtmpl" 
)

public class UIAccountPortlet extends UIPortletApplication {
  public UIAccountPortlet() throws Exception{    
    addChild(UIAccountForm.class, null, null);
  }
}