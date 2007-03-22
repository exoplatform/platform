package org.exoplatform.account.webui.component;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UILoginForm;
import org.exoplatform.portal.component.widget.UILoginForm.SigninActionListener;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;

@ComponentConfigs({
  @ComponentConfig(  
      lifecycle = UIApplicationLifecycle.class,
      template = "app:/groovy/account/webui/component/UIAccountPortlet.gtmpl" 
      
    ),
  @ComponentConfig(
    type = UILoginForm.class,     
    lifecycle = UIFormLifecycle.class ,
    template = "app:/groovy/account/webui/component/UILoginForm.gtmpl" ,
    events = @EventConfig(listeners = SigninActionListener.class )
  )    
})

public class UIAccountPortlet extends UIPortletApplication {
  public UIAccountPortlet() throws Exception{    
    UILoginForm loginForm = addChild(UILoginForm.class, null, null);
    addChild(UIAccountForm.class, null, null);
    PortalRequestContext prContext = Util.getPortalRequestContext();
    if(prContext.getAccessPath() == PortalRequestContext.PRIVATE_ACCESS) loginForm.setRendered(false);
  }
}