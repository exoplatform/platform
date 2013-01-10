package org.exoplatform.platform.portlet.juzu.suggestions;

import juzu.Path;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.RequestContext;
import org.gatein.common.text.EntityEncoder;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class SuggestionsPeopleSpace {

    private static Locale locale = null;
    private static ResourceBundle rs = null;
    private static Log log = ExoLogger.getLogger(SuggestionsPeopleSpace.class);
    @Inject
    @Path("list.gtmpl")
    Template list;
    static HashMap bundle = new HashMap();

    @View
    public void index() {
        locale = RequestContext.getCurrentInstance().getLocale();
        rs = ResourceBundle.getBundle("suggestions/suggestions", locale);
        try {

            if (rs.getString("connection.label") != null && rs.getString("connection.label").length() > 0) {
                bundle.put("connectionLabel", EntityEncoder.FULL.encode(rs.getString("connection.label")));
            }
            if (rs.getString("Connect.Label") != null && rs.getString("Connect.Label").length() > 0) {
                bundle.put("connectLabel", EntityEncoder.FULL.encode(rs.getString("Connect.Label")));
            }

            if (rs.getString("private.Label") != null && rs.getString("private.Label").length() > 0) {
                bundle.put("privateLabel", EntityEncoder.FULL.encode(rs.getString("private.Label")));
            }
            if (rs.getString("member.Label") != null && rs.getString("member.Label").length() > 0) {
                bundle.put("memberLabel", EntityEncoder.FULL.encode(rs.getString("member.Label")));
            }
            if (rs.getString("suggestions.label") != null && rs.getString("suggestions.label").length() > 0) {
                bundle.put("headerLabel", EntityEncoder.FULL.encode(rs.getString("suggestions.label")));
            }
            if (rs.getString("spacemember.Label") != null && rs.getString("spacemember.Label").length() > 0) {
                bundle.put("spacemember", EntityEncoder.FULL.encode(rs.getString("spacemember.Label")));
            }

            if (rs.getString("public.label") != null && rs.getString("public.label").length() > 0) {
                bundle.put("publicLabel", EntityEncoder.FULL.encode(rs.getString("public.label")));
            }

            if (rs.getString("request.label") != null && rs.getString("request.label").length() > 0) {
                bundle.put("requestSpace", EntityEncoder.FULL.encode(rs.getString("request.label")));
            }

            if (rs.getString("join.label") != null && rs.getString("join.label").length() > 0) {
                bundle.put("joinSpace", EntityEncoder.FULL.encode(rs.getString("join.label")));
            }


        } catch (MissingResourceException ex) {
            log.error("Error In Suggestions Resource Bundle: " + ex.getMessage(), ex);
        }

        list.render(bundle);
    }
}
