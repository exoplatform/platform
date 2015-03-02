package org.exoplatform.platform.portlet.juzu.suggestions;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;

import javax.inject.Inject;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class SuggestionsPeopleSpace {
    @Inject
    @Path("list.gtmpl")
    Template list;

    @View
    public Response.Content index() {
        return list.ok();
    }
}
