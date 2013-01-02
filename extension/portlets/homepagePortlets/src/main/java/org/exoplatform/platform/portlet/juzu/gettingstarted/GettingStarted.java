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
import juzu.Resource;
import juzu.View;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import org.exoplatform.platform.portlet.juzu.gettingstarted.models.GettingStartedService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.web.application.RequestContext;
import org.gatein.common.text.EntityEncoder;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 07/12/12
 */

public class GettingStarted  {

    private static Log logger = ExoLogger.getLogger(GettingStarted.class);
    static String remoteUser="";
    static HashMap parameters = new HashMap();
    static HashMap<String,String> status =new HashMap();
    static Locale locale=null;

    @Inject
    NodeHierarchyCreator nodeHierarchyCreator_;

    @Inject
    @Path("gettingStarted.gtmpl")
    Template gettingStarted;

    @Inject
    @Path("gettingStartedList.gtmpl")
    Template gettingStartedList;

    @View
    public void index() throws Exception {
        boolean del = false;
        if(locale==null) locale= RequestContext.getCurrentInstance().getLocale();
        remoteUser= RequestContext.getCurrentInstance().getRemoteUser();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (userPrivateNode.hasNode("GsGadget")) {
            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
            if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {
                del = gettingStartedNode.getProperty("exo:gs_deleteGadget").getBoolean();
            }
        }
        else {
            Node gettingStartedNode = userPrivateNode.addNode("GsGadget");
            userPrivateNode.save();
            gettingStartedNode.setProperty("exo:gs_deleteGadget", false);
            gettingStartedNode.setProperty("exo:gs_profile", false);
            gettingStartedNode.setProperty("exo:gs_connect", false);
            gettingStartedNode.setProperty("exo:gs_space", false);
            gettingStartedNode.setProperty("exo:gs_activities", false);
            gettingStartedNode.setProperty("exo:gs_document", false);
            gettingStartedNode.save();
        }
        if (del) index("false");
        else index("true");
    }

    @View
    public void index(String show)  {
        String titleLabel= "";

        try{
            ResourceBundle rs = ResourceBundle.getBundle("gettingStarted/gettingStarted", locale);

            titleLabel=rs.getString("title.Label");
            EntityEncoder.FULL.encode(titleLabel);
        }
        catch(MissingResourceException ex){

            titleLabel="title.Label";
        }
        finally {
            gettingStarted.with().set("show",show).set("titleLabel", titleLabel).render();
        }
        }

    @Ajax
    @Resource
    public void getGsList() throws Exception {

        String profileLabel = "";
        String documentLabel = "";
        String connectLabel = "";
        String activityLabel = "";
        String spaceLabel = "";

        int progress=0;

        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (userPrivateNode.hasNode("GsGadget")) {
        Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
        gettingStartedNode.setProperty("exo:gs_profile", GettingStartedService.hasAvatar(remoteUser));
        gettingStartedNode.setProperty("exo:gs_connect", GettingStartedService.hasContacts(remoteUser));
        gettingStartedNode.setProperty("exo:gs_space", GettingStartedService.hasSpaces(remoteUser));
        gettingStartedNode.setProperty("exo:gs_activities", GettingStartedService.hasActivities(remoteUser));
        gettingStartedNode.setProperty("exo:gs_document", GettingStartedService.hasDocuments(null, remoteUser));
        }
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

        }
        catch(MissingResourceException ex){
            profileLabel=  "Upload.label";
            connectLabel="Connect.label";
            spaceLabel ="Space.label";
            activityLabel="Activity.label" ;
            documentLabel="document.label";

        }
        finally {
            PropertyIterator propertiesIt = userPrivateNode.getNode("GsGadget").getProperties("exo:gs_*");
            while (propertiesIt.hasNext()) {
                String clazz="" ;
                Property prop = (Property) propertiesIt.next();
                if (prop.getString().equals("true"))
                {
                    progress+=20;
                    clazz="done";
                }
                status.put(prop.getName().substring(4),clazz);
            }
            parameters.put("profile", LinkProvider.getUserProfileUri(remoteUser));
            parameters.put("profileLabel", profileLabel);
            parameters.put("connect", LinkProvider.getUserConnectionsUri(remoteUser));
            parameters.put("connectLabel", connectLabel);
            parameters.put("space", "/portal/intranet/all-spaces");
            parameters.put("spaceLabel", spaceLabel);
            parameters.put("activity", "#");
            parameters.put("activityLabel", activityLabel);
            parameters.put("upload", "/portal/intranet/documents");
            parameters.put("uploadLabel", documentLabel);
            parameters.put("progress",new Integer(progress));
            parameters.put("width", new Integer(Math.round(160/100*progress)));
            parameters.put("status",status);
            gettingStartedList.render(parameters);
        }
    }
}
