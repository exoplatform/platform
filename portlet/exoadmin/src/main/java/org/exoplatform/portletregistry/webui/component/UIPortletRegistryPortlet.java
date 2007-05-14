package org.exoplatform.portletregistry.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.component.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/portletregistry/webui/component/UIPortletRegistryPortlet.gtmpl"
)
public class UIPortletRegistryPortlet extends UIPortletApplication {
  
  public UIPortletRegistryPortlet() throws Exception{
    addChild(ApplicationRegistryWorkingArea.class, null, null);  
    ApplicationRegistryControlArea controlArea = addChild(ApplicationRegistryControlArea.class, null, null);
    controlArea.initApplicationCategories();
//    init();
  }
  
//  private void init() throws Exception {
//    getChild(ApplicationRegistryControlArea.class).initApplicationCategories();
//  }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    context.getWriter().append("<div id=\"").append(getId()).append("\">");
    renderChildren(context) ;
    context.getWriter().append("</div>");
  }
}