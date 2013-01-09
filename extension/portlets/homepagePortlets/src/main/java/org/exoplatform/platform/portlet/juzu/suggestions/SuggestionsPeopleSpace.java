package org.exoplatform.platform.portlet.juzu.suggestions;

import juzu.Path;
import juzu.View;
import juzu.template.Template;
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

    @Inject
    @Path("list.gtmpl")
    Template list;
    static HashMap bundle = new HashMap();

    @View
    public void index() {
        Locale locale = RequestContext.getCurrentInstance().getLocale();
        ResourceBundle rs = ResourceBundle.getBundle("suggestions/suggestions", locale);
        String connectionLabel = "";
        String connectLabel = "";
        String privateLabel = "";
        String memberLabel = "";
        String headerLabel = "";
        String spacemember = "";
        String publicLabel = "";
        String requestSpace = "";
        String join = "";


        try {

            connectionLabel = rs.getString("connection.label");
            EntityEncoder.FULL.encode(connectionLabel);
            connectLabel = rs.getString("Connect.Label");
            EntityEncoder.FULL.encode(connectLabel);
            memberLabel = rs.getString("member.Label");
            EntityEncoder.FULL.encode(memberLabel);
            privateLabel = rs.getString("private.Label");
            EntityEncoder.FULL.encode(privateLabel);
            headerLabel = rs.getString("suggestions.label");
            EntityEncoder.FULL.encode(headerLabel);
            spacemember = rs.getString("spacemember.Label");
            EntityEncoder.FULL.encode(spacemember);
            publicLabel = rs.getString("public.label");
            EntityEncoder.FULL.encode(publicLabel);
            requestSpace = rs.getString("request.label");
            EntityEncoder.FULL.encode(requestSpace);
            join = rs.getString("join.label");
            EntityEncoder.FULL.encode(join);


        } catch (MissingResourceException ex) {
            connectionLabel = "connection.label";
            connectLabel = "Connect.label";
            memberLabel = "member.label";
            privateLabel = "private.label";
            headerLabel = "suggestions.label";
            spacemember = "spacemember.label";
            publicLabel = "public.label";
            requestSpace = "request.label";
            join = "join.label";
        } finally {


            bundle.put("connectionLabel", connectionLabel);
            bundle.put("connectLabel", connectLabel);
            bundle.put("privateLabel", privateLabel);
            bundle.put("memberLabel", memberLabel);
            bundle.put("headerLabel", headerLabel);
            bundle.put("spacemember", spacemember);
            bundle.put("publicLabel", publicLabel);
            bundle.put("requestSpace", requestSpace);
            bundle.put("joinSpace", join);


        }

        list.render(bundle);
    }
}
