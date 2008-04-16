package org.exoplatform.portal.webui.component;

import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Author : Tran The Trong
 *          trongtt@gmail.com
 * April 16, 2008
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template =  "app:/groovy/webui/component/UIProcessEventPortlet.gtmpl",
  events = {
    @EventConfig(listeners = UIProcessEventPortlet.SubmitEventActionListener.class),
    @EventConfig(listeners = UIProcessEventPortlet.ProcessEventActionListener.class)
  }
)

public class UIProcessEventPortlet extends UIPortletApplication {
  public UIProcessEventPortlet() throws Exception {}

  static  public class SubmitEventActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      System.out.println("\n\n\n\n =========== SubmitEvent =================== \n\n\n\n");
      ActionResponse actionRes = event.getRequestContext().getResponse() ;
      actionRes.setEvent(new QName("ProcessEvent"), null) ;
    }
  }

  static  public class ProcessEventActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      System.out.println("\n\n\n\n =========== ProcessEvent : " + event.getSource().getId() + "=================== \n\n\n\n");
    }
  }
}
