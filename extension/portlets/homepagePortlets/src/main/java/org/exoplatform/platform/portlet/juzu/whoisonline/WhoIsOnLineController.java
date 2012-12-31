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
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<User> friends = whoIsOnline.getFriends(userId);
    return index.with().set("users", friends).render();
  }

  @Ajax
  @Resource
  public Response.Render users() {
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<User> friends = whoIsOnline.getFriends(userId);
    return users.with().set("users", friends).render();
  }
}
