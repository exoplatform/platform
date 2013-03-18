package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import org.exoplatform.commons.juzu.ajax.Ajax;
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

    private static  Locale locale = null;
    private static ResourceBundle rs=null;

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
      locale=RequestContext.getCurrentInstance().getLocale();
      rs = ResourceBundle.getBundle("locale/portlet/whoisonline/whoisonline", locale);
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<User> friends = whoIsOnline.getFriends(userId);
      return index.with().set("users", friends).set("headerLabel",rs.getString("header.label")).set("messageLabel",rs.getString("message.label")).set("connectLabel",rs.getString("connect.label")).ok();
  }

  @Ajax
  @Resource
  public Response.Render users() {
      locale=RequestContext.getCurrentInstance().getLocale();
      rs = ResourceBundle.getBundle("locale/portlet/whoisonline/whoisonline", locale);
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<User> friends = whoIsOnline.getFriends(userId);
      return users.with().set("users", friends).set("messageLabel",rs.getString("message.label")).set("connectLabel",rs.getString("connect.label")).ok();
  }
}
