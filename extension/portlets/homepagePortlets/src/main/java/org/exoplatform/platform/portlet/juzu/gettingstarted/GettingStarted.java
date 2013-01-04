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
    static String remoteUserCach="";
    HashMap parameters = new HashMap();
    static HashMap bundle = new HashMap();
    HashMap<String,String> status =new HashMap();
    Locale locale=null;



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
        remoteUser= RequestContext.getCurrentInstance().getRemoteUser();
        if(remoteUserCach.equals("")) remoteUserCach=remoteUser;
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (!userPrivateNode.hasNode("GsGadget")) {

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
        if((bundle.size()==0)||(!remoteUser.equals(remoteUserCach))){
            String profileLabel = "";
            String documentLabel = "";
            String connectLabel = "";
            String activityLabel = "";
            String spaceLabel = "";
            String titleLabel= "";
            try {
                locale=RequestContext.getCurrentInstance().getLocale();
                ResourceBundle rs = ResourceBundle.getBundle("gettingStarted/gettingStarted", locale);
                profileLabel = rs.getString("Upload.label");
                EntityEncoder.FULL.encode(profileLabel);
                connectLabel = rs.getString("Connect.Label");
                EntityEncoder.FULL.encode(connectLabel);
                spaceLabel = rs.getString("Space.Label");
                EntityEncoder.FULL.encode(spaceLabel);
                activityLabel = rs.getString("Activity.Label");
                EntityEncoder.FULL.encode(activityLabel);
                documentLabel = rs.getString("Document.Label");
                EntityEncoder.FULL.encode(documentLabel);
                titleLabel=rs.getString("title.Label");
                EntityEncoder.FULL.encode(titleLabel);

            } catch (MissingResourceException ex) {
                profileLabel = "Upload.label";
                connectLabel = "Connect.label";
                spaceLabel = "Space.label";
                activityLabel = "Activity.label";
                documentLabel = "document.label";
                titleLabel="title.Label";

            } finally {

                bundle.put("profile", LinkProvider.getUserProfileUri(remoteUser));
                bundle.put("profileLabel", profileLabel);
                bundle.put("connect", LinkProvider.getUserConnectionsUri(remoteUser));
                bundle.put("connectLabel", connectLabel);
                bundle.put("space", "/portal/intranet/all-spaces");
                bundle.put("spaceLabel", spaceLabel);
                bundle.put("activity", "#");
                bundle.put("activityLabel", activityLabel);
                bundle.put("upload", "/portal/intranet/documents");
                bundle.put("uploadLabel", documentLabel);
                bundle.put("titleLabel", titleLabel);
            }
        }
        gettingStarted.render();
    }

    @Ajax
    @Resource
    public void delete() throws Exception {
        //set Delete
        String userId = RequestContext.getCurrentInstance().getRemoteUser();

        SessionProvider sProvider = SessionProvider.createSystemProvider();

        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, userId).getNode("ApplicationData");

        if (userPrivateNode.hasNode("GsGadget")) {

            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");

            if (gettingStartedNode.hasProperty("exo:gs_deleteGadget")) {

                gettingStartedNode.setProperty("exo:gs_deleteGadget", true);

                gettingStartedNode.save();
            }
        }
        gettingStarted.render();
    }

    @Ajax
    @Resource
    public void getGsList() throws Exception {


        Boolean Isshow = true;

        PropertyIterator propertiesIt = null;
        int progress = 0;

        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode("ApplicationData");
        if (userPrivateNode.hasNode("GsGadget")) {
            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
            gettingStartedNode.setProperty("exo:gs_profile", GettingStartedService.hasAvatar(remoteUser));
            gettingStartedNode.setProperty("exo:gs_connect", GettingStartedService.hasContacts(remoteUser));
            gettingStartedNode.setProperty("exo:gs_space", GettingStartedService.hasSpaces(remoteUser));
            gettingStartedNode.setProperty("exo:gs_activities", GettingStartedService.hasActivities(remoteUser));
            gettingStartedNode.setProperty("exo:gs_document", GettingStartedService.hasDocuments(null, remoteUser));
            propertiesIt = userPrivateNode.getNode("GsGadget").getProperties("exo:gs_*");
            while (propertiesIt.hasNext()) {
                String clazz = "";
                Property prop = (Property) propertiesIt.next();
                if (prop.getString().equals("true")) {
                    progress += 20;
                    clazz = "done";
                }
                status.put(prop.getName().substring(4), clazz);
            }
            if (progress == 100) Isshow = false;
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
        String[] width =new Integer((160 * progress )/ 100 ).toString().split(".");
            parameters.putAll(bundle);
            parameters.put("progress", new Integer(progress));
            parameters.put("width", new Integer((Math.round((160 * progress )/ 100 ))).toString());
            parameters.put("status", status);
            parameters.put("show", Isshow.toString());
            gettingStartedList.render(parameters);
        }
    }


