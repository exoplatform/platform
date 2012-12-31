package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.web.application.RequestContext;

import javax.inject.Inject;
import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
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
     // PortalRequestContext pcontext = (PortalRequestContext)(WebuiRequestContext.getCurrentInstance());
      //String userId = pcontext.getRequest().getRemoteUser();
     // String userId =  renderContext.getSecurityContext().getRemoteUser();
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<Profile> friends = whoIsOnline.getFriends(userId);
    return index.with().set("users", friends).render();
  }

  @Ajax
  @Resource
  public Response.Render users() {
      System.out.println("RESOURCING");
      String userId= RequestContext.getCurrentInstance().getRemoteUser();
      List<Profile>  friends = whoIsOnline.getFriends(userId);
    return users.with().set("users", friends).render();
  }
}
