package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.RequestContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnLineController {

    private static final Log LOG = ExoLogger.getLogger(WhoIsOnLineController.class);

    @Inject
    WhoIsOnline whoIsOnline;

    @Inject
    @Path("index.gtmpl")
    Template index;

    @Inject
    @Path("users.gtmpl")
    Template users;

    @View
    public Response.Content index() {
        try {

            String userId = RequestContext.getCurrentInstance().getRemoteUser();
            List<User> friends = whoIsOnline.getFriends(userId);
            if (friends == null) {
                friends = new ArrayList<User>();
                LOG.info("No  logged user | WhoIsOnLin Portlet will not be displayed");
            }
            return index.with().set("users", friends).ok();

        } catch (Exception e) {
            LOG.error("Error while rendering WhoIsOnLine Portlet :" + e.getMessage(), e);
            return index.with().set("users", new ArrayList<User>()).ok();
        }

    }

    @Ajax
    @Resource
    public Response.Content users() {
        try {
            String userId = RequestContext.getCurrentInstance().getRemoteUser();
            List<User> friends = whoIsOnline.getFriends(userId);
            if (friends == null) {
                friends = new ArrayList<User>();
                LOG.info("No  logged user | WhoIsOnLin Portlet will not be displayed");
            }
            return users.with().set("users", friends).ok();

        } catch (Exception e) {
            LOG.error("Error while rendering WhoIsOnLine Portlet :" + e.getMessage(), e);
            return users.with().set("users", new ArrayList<User>()).ok();
        }
    }
}
