package org.exoplatform.platform.component;

import java.util.Arrays;
import java.util.List;

import org.exoplatform.commons.api.notification.model.WebNotificationFilter;
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
import org.exoplatform.ws.frameworks.cometd.ContinuationService;
import org.mortbay.cometd.continuation.EXoContinuationBayeux;

@ComponentConfig(
    lifecycle = UIApplicationLifecycle.class,
    template = "app:/groovy/platformNavigation/portlet/UINotificationPopoverToolbarPortlet/UINotificationPopoverToolbarPortlet.gtmpl",
    events = {
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.MarkAllReadActionListener.class),
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.MarkReadActionListener.class),
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.RemoveActionListener.class),
      @EventConfig(listeners = UINotificationPopoverToolbarPortlet.DeleteActionListener.class)
    }
)
public class UINotificationPopoverToolbarPortlet extends UIPortletApplication {
  private static final Log LOG = ExoLogger.getLogger(UINotificationPopoverToolbarPortlet.class);
  private final WebNotificationService webNftService;
  private final UserSettingService userSettingService;
  private final ContinuationService continuation;
  private final EXoContinuationBayeux bayeux;
  private String currentUser = "";
  private int MAX_NUMBER_ON_MENU = 8;

  public UINotificationPopoverToolbarPortlet() throws Exception {
    webNftService = getApplicationComponent(WebNotificationService.class);
    userSettingService = getApplicationComponent(UserSettingService.class);
    continuation = getApplicationComponent(ContinuationService.class);
    bayeux = getApplicationComponent(EXoContinuationBayeux.class);
    MAX_NUMBER_ON_MENU = Integer.valueOf(System.getProperty("exo.notifications.maxitems", "8"));
  }
  
  @Override
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    this.currentUser = context.getRemoteUser();
    StringBuilder scripts = new StringBuilder("NotificationPopoverToolbarPortlet.initCometd('");
    scripts.append(currentUser).append("', '")
           .append(getUserToken()).append("', '")
           .append(getCometdContextName()).append("');");
    
    context.getJavascriptManager().getRequireJS()
           .require("SHARED/jquery_cometd", "cometd")
           .require("PORTLET/platformNavigation/NotificationPopoverToolbarPortlet", "NotificationPopoverToolbarPortlet")
           .addScripts(scripts.toString());
    //
    super.processRender(app, context);
  }

  protected List<String> getNotifications() throws Exception {
    return webNftService.get(new WebNotificationFilter(currentUser, true), 0, MAX_NUMBER_ON_MENU);
  }

  protected List<String> getActions() {
    return Arrays.asList("MarkRead", "Remove", "Delete");
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
  
  protected String getCometdContextName() {
    return (bayeux == null ? "cometd" : bayeux.getCometdContextName());
  }

  public String getUserToken() {
    try {
      return continuation.getUserToken(currentUser);
    } catch (Exception e) {
      LOG.error("Could not retrieve continuation token for user " + currentUser, e);
      return "";
    }
  }
  
  public static class MarkReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String notificationId = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      portlet.webNftService.markRead(notificationId);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class RemoveActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      portlet.webNftService.hidePopover(id);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
  
  public static class DeleteActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      portlet.webNftService.remove(id);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }

  public static class MarkAllReadActionListener extends EventListener<UINotificationPopoverToolbarPortlet> {
    public void execute(Event<UINotificationPopoverToolbarPortlet> event) throws Exception {
      UINotificationPopoverToolbarPortlet portlet = event.getSource();
      portlet.webNftService.markAllRead(portlet.currentUser);
      // Ignore reload portlet
      ((PortalRequestContext) event.getRequestContext().getParentAppRequestContext()).ignoreAJAXUpdateOnPortlets(true);
    }
  }
}
