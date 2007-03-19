package org.exoplatform.groovy.webui.component;

import java.util.ArrayList;

import javax.portlet.PortletRequest;

import org.exoplatform.groovy.webui.component.lifecycle.UIGroovyPortletLifecycle;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIPortletApplication;
import org.exoplatform.webui.config.Event;
import org.exoplatform.webui.config.annotation.ComponentConfig;

@ComponentConfig( lifecycle = UIGroovyPortletLifecycle.class )
public class UIGroovyPortlet extends UIPortletApplication {
  
  private String DEFAULT_TEMPLATE = "app:/groovy/groovy/webui/component/UIGroovyPortlet.gtmpl" ;  
  private String template_ ;
  
  public UIGroovyPortlet() throws Exception {
    PortletRequestContext context = (PortletRequestContext)  RequestContext.getCurrentInstance() ;
    PortletRequest prequest = context.getRequest() ;    
    template_ =  prequest.getPreferences().getValue("template", DEFAULT_TEMPLATE) ;
    System.out.println("\n\n\n\n template is  "+template_ +"\n\n\n");
    
    ArrayList<Event> list  = getComponentConfig().getEvents();
    System.out.println("\n\n\n\n");
    for(Event obj : list){
      System.out.println("event ===== > : "+obj.getName() +" : "+obj);
    }
    System.out.println("\n\n\n\n");
  }
  
  public String url(String name) throws Exception {
    ArrayList list  = getComponentConfig().getEvents();
    System.out.println("\n\n\n\n");
    for(Object obj : list){
      System.out.println("===== >"+obj);
    }
    System.out.println("\n\n\n\n");
    return super.url(name, null); 
  }  
  
  public String getTemplate() {  return template_ ;  }
  
  public UIComponent getViewModeUIComponent() { return null; }

}