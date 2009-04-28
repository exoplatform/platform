package org.exoplatform.toolbar.webui.component;


import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(  
    lifecycle = UIApplicationLifecycle.class
) 
public class UIAdminToolbarPortlet extends UIPortletApplication {
  public UIAdminToolbarPortlet() throws Exception{    
    
  }
}
