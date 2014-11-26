package org.exoplatform.platform.component;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.api.notification.service.setting.UserSettingService;
import org.exoplatform.commons.api.notification.service.storage.IntranetNotificationDataStorage;
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
  private String currentUser = "";
  private IntranetNotificationDataStorage dataStorage;
  private UserSettingService userSettingService;

  public UINotificationPopoverToolbarPortlet() throws Exception {
    dataStorage = getApplicationComponent(IntranetNotificationDataStorage.class);
    userSettingService = getApplicationComponent(UserSettingService.class);
  }
  
  @Override
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    this.currentUser = context.getRemoteUser();
    super.processRender(app, context);
  }

  protected List<String> getNotifications() throws Exception {
    return dataStorage.getNotificationContent(currentUser, true);
  }

  protected List<String> getActions() {
    return Arrays.asList("MarkRead", "Remove");
  }
  
  protected String getActionUrl(String actionName) throws Exception {
    return event(actionName).replace("javascript:ajaxGet('", "").replace("')", "&" + OBJECTID + "=");
  }

  protected boolean isIntranetActive() {
    if (currentUser == null || currentUser.isEmpty()) {
      currentUser = WebuiRequestContext.getCurrentInstance().getRemoteUser();
    }
    return userSettingService.get(currentUser).isIntranetActive();
  }
  
  public static class MarkReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String notificationId = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkReadActionListener");
      portlet.dataStorage.saveRead(portlet.currentUser, notificationId);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class RemoveActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String notificationId = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action RemoveActionListener: " + notificationId);
      portlet.dataStorage.remove(portlet.currentUser, notificationId);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class MarkAllReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      LOG.info("Run action MarkAllReadActionListener: " + portlet.currentUser);
      portlet.dataStorage.saveReadAll(portlet.currentUser);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
}
