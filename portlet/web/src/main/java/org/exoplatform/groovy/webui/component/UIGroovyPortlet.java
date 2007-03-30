package org.exoplatform.groovy.webui.component;

import javax.portlet.PortletRequest;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;

@ComponentConfig()
public class UIGroovyPortlet extends UIPortletApplication {
  
  private String DEFAULT_TEMPLATE = "app:/groovy/groovy/webui/component/UIGroovyPortlet.gtmpl" ;  
  private String template_ ;
  
  public UIGroovyPortlet() throws Exception {
    PortletRequestContext context = (PortletRequestContext)  WebuiRequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    template_ =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ;
  }
  
  public String getTemplate() {  return template_ ;  }
  
  public UIComponent getViewModeUIComponent() { return null; }

}