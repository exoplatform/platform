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
 */
public class WhoIsOnLineController {

    private static final Log LOG = ExoLogger.getLogger(WhoIsOnLineController.class);

    @Inject
    @Path("index.gtmpl")
    Template index;

    @Inject
    @Path("users.gtmpl")
    Template users;

    @View
    public Response.Content index() {
        return index.ok();
    }
}
