package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPopupMessages;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/portletregistry/webui/component/UIPortletRegistryPortlet.gtmpl"
)
public class UIPortletRegistryPortlet extends UIPortletApplication {
  
  public UIPortletRegistryPortlet() throws Exception{
    addChild(ApplicationRegistryWorkingArea.class, null, null);  
    ApplicationRegistryControlArea uiControlArea = addChild(ApplicationRegistryControlArea.class, null, null);
    uiControlArea.initApplicationCategories();
  }
  
  public void renderPopupMessages() throws Exception {
    UIPopupMessages uiPopupMsg = getUIPopupMessages();
    if(uiPopupMsg == null)  return ;
    WebuiRequestContext  context =  WebuiRequestContext.getCurrentInstance() ;
    uiPopupMsg.processRender(context);
  }

}