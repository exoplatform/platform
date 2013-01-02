package org.exoplatform.platform.portlet.juzu.suggestions;

import juzu.Path;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.web.application.RequestContext;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class SuggestionsPeopleSpace {

    @Inject
    @Path("list.gtmpl")
    Template list;

    @View
    public void index() {
        Locale locale = RequestContext.getCurrentInstance().getLocale();
        ResourceBundle rs = ResourceBundle.getBundle("suggestions/suggestions", locale);
        String headerLabel = rs.getString("suggestions.label");
        list.with().set("headerLabel",headerLabel).render();
    }
}
