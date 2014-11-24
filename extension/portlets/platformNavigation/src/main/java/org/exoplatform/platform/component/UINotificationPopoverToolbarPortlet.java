package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 01/11/12
 */
@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/platformNavigation/portlet/UINotificationPopoverToolbarPortlet/UINotificationPopoverToolbarPortlet.gtmpl",
    events = {
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.MarkAllReadActionListener.class),
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.MarkReadActionListener.class),
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.RemoveActionListener.class)
    }
)
public class UINotificationPopoverToolbarPortlet extends UIPortletApplication {
  private static final Log LOG = ExoLogger.getLogger(UINotificationPopoverToolbarPortlet.class);

  public UINotificationPopoverToolbarPortlet() throws Exception {
  }

  protected List<String> getNotifications() {
    List<String> list = new ArrayList<String>();
    return list;
  }

  protected List<String> getActions() {
    return Arrays.asList("MarkRead", "Remove");
  }
  
  protected String getActionUrl(String actionName) throws Exception {
    return event(actionName).replace("javascript:ajaxGet('", "").replace("')", "&" + OBJECTID + "=");
  }

  public static class MarkAllReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkAllRead");
      
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class RemoveActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action RemoveActionListener");
      
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
  public static class MarkReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkReadActionListener");
      
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
}
