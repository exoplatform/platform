package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import org.exoplatform.web.application.RequestContext;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnLineController {

  @Inject
  WhoIsOnline whoIsOnline;

  @Inject
  @Path("index.gtmpl")
  Template index;

  @Inject
  @Path("users.gtmpl")
  Template users;

  @View
  public Response.Render index() {
      Locale locale = RequestContext.getCurrentInstance().getLocale();
      ResourceBundle rs = ResourceBundle.getBundle("whoisonline/whoisonline", locale);
      String connectLabel = rs.getString("connect.label");
      String messageLabel = rs.getString("message.label");
      String headerLabel = rs.getString("header.label");
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<User> friends = whoIsOnline.getFriends(userId);
    return index.with().set("users", friends).set("headerLabel",headerLabel).set("messageLabel",messageLabel).set("connectLabel",connectLabel).render();
  }

  @Ajax
  @Resource
  public Response.Render users() {
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      Locale locale = RequestContext.getCurrentInstance().getLocale();
      ResourceBundle rs = ResourceBundle.getBundle("whoisonline/whoisonline", locale);
      String connectLabel = rs.getString("connect.label");
      String messageLabel = rs.getString("message.label");
      List<User> friends = whoIsOnline.getFriends(userId);
    return users.with().set("users", friends).set("messageLabel",messageLabel).set("connectLabel",connectLabel).render();
  }
}
