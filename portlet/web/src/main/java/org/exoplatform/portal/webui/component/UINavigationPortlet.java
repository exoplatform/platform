package org.exoplatform.portal.webui.component;

import javax.portlet.PortletRequest;

import org.exoplatform.portal.webui.navigation.UIPortalNavigation;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIApplicationLifecycle.class
  ),
  @ComponentConfig(
    type = UIPortalNavigation.class,
    id = "UIHorizontalNavigation",    
    events = @EventConfig(listeners = UIPortalNavigation.SelectNodeActionListener.class)
  )
})

public class UINavigationPortlet extends UIPortletApplication {
  private static String DEFAULT_TEMPLATE = "app:/groovy/portal/webui/component/UIPortalNavigation.gtmpl" ;
  public UINavigationPortlet () throws  Exception { 
    PortletRequestContext context = (PortletRequestContext)  WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    String template =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ;    
    UIPortalNavigation portalNavigation = addChild(UIPortalNavigation.class, "UIHorizontalNavigation", null);
    portalNavigation.getComponentConfig().setTemplate(template) ;
  }  
}