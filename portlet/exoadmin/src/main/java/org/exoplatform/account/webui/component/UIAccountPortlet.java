package org.exoplatform.account.webui.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.organization.UIAccountForm;

@ComponentConfig(  
    lifecycle = UIApplicationLifecycle.class
) 
public class UIAccountPortlet extends UIPortletApplication {
  public UIAccountPortlet() throws Exception{    
    addChild(UIAccountForm.class, null, null);
  }
}