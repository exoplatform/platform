/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.portlet.juzu.gettingstarted;

import juzu.Path;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.web.application.RequestContext;
import org.gatein.common.text.EntityEncoder;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 07/12/12
 */
public class GettingStarted {

    private static Log logger = ExoLogger.getLogger(GettingStarted.class);

    @Inject
    NodeHierarchyCreator nodeHierarchyCreator_;

    @Inject
    @Path("gettingStarted.gtmpl")
    Template gettingStarted;

    @View
    public void index() throws Exception {
        boolean del = false;
        String remoteUser = RequestContext.getCurrentInstance().getRemoteUser();;
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (userPrivateNode.hasNode("GsGadget")) {
            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
            if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {
                del = gettingStartedNode.getProperty("exo:gs_deleteGadget").getBoolean();
            }
        }
        if (del) index("false");
        else index("true");
    }

    @View
    public void index(String show)  {
        Locale locale = RequestContext.getCurrentInstance().getLocale();
        String remoteUser = RequestContext.getCurrentInstance().getRemoteUser();

        String profileLabel = "";
        String documentLabel = "";
        String connectLabel = "";
        String activityLabel = "";
        String spaceLabel = "";
        String titleLabel= "";

        try{
        ResourceBundle rs = ResourceBundle.getBundle("gettingStarted/gettingStarted", locale);
            profileLabel = rs.getString("Upload.label");
            EntityEncoder.FULL.encode(profileLabel);
            connectLabel = rs.getString("Connect.Label");
            EntityEncoder.FULL.encode(connectLabel);
            spaceLabel = rs.getString( "Space.Label");
            EntityEncoder.FULL.encode(spaceLabel);
            activityLabel = rs.getString("Activity.Label");
            EntityEncoder.FULL.encode(activityLabel);
            documentLabel = rs.getString("Document.Label");
            EntityEncoder.FULL.encode(documentLabel);
            titleLabel=rs.getString("title.Label");
            EntityEncoder.FULL.encode(titleLabel);
        }
        catch(MissingResourceException ex){
            profileLabel=  "Upload.label";
            connectLabel="Connect.label";
            spaceLabel ="Space.label";
            activityLabel="Activity.label" ;
            documentLabel="document.label";
            titleLabel="title.Label";
        }
        finally {
            HashMap parameters = new HashMap();
            parameters.put("show", show);
            parameters.put("titleLabel", titleLabel);
            parameters.put("profile", LinkProvider.getUserProfileUri(remoteUser));
            parameters.put("profileLabel", profileLabel);
            parameters.put("connect", LinkProvider.getUserConnectionsUri(remoteUser));
            parameters.put("connectLabel", connectLabel);
            parameters.put("space", "/portal/intranet/all-spaces");
            parameters.put("spaceLabel", spaceLabel);
            parameters.put("activity", "#");
            parameters.put("activityLabel", activityLabel);
            parameters.put("upload", "portal/intranet/documents");
            parameters.put("uploadLabel", documentLabel);
            gettingStarted.render(parameters);
        }
    }
}
