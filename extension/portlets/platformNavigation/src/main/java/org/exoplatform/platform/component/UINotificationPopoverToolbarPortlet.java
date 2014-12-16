package org.exoplatform.platform.component;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.api.notification.model.WebFilter;
import org.exoplatform.commons.api.notification.service.WebNotificationService;
import org.exoplatform.commons.api.notification.service.setting.UserSettingService;
import org.exoplatform.commons.notification.channel.WebChannel;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

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
  private String currentUser = "";
  private WebNotificationService webNftService;
  private UserSettingService userSettingService;

  public UINotificationPopoverToolbarPortlet() throws Exception {
    webNftService = getApplicationComponent(WebNotificationService.class);
    userSettingService = getApplicationComponent(UserSettingService.class);
  }
  
  @Override
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    this.currentUser = context.getRemoteUser();
    super.processRender(app, context);
  }

  protected List<String> getNotifications() throws Exception {
    WebFilter filter = new WebFilter(currentUser, true, 8);
    return webNftService.getNotificationContents(filter);
  }

  protected List<String> getActions() {
    return Arrays.asList("MarkRead", "Remove");
  }
  
  protected String getActionUrl(String actionName) throws Exception {
    return event(actionName).replace("javascript:ajaxGet('", "").replace("')", "&" + OBJECTID + "=");
  }

  protected boolean isWebActive() {
    if (currentUser == null || currentUser.isEmpty()) {
      currentUser = WebuiRequestContext.getCurrentInstance().getRemoteUser();
    }
    return userSettingService.get(currentUser).isChannelActive(WebChannel.ID);
  }
  
  public static class MarkReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String notificationId = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkReadActionListener");
      portlet.webNftService.markRead(notificationId);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class RemoveActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String notificationId = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action RemoveActionListener: " + notificationId);
      portlet.webNftService.remove(portlet.currentUser, notificationId);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class MarkAllReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkAllReadActionListener: " + portlet.currentUser);
      portlet.webNftService.markReadAll(portlet.currentUser);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
}
