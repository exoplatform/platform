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
import juzu.template.Template;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.platform.portlet.juzu.gettingstarted.models.GettingStartedService;
import org.exoplatform.platform.portlet.juzu.gettingstarted.models.GettingStartedUtils;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.web.application.RequestContext;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.MissingResourceException;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 07/12/12
 */

public class GettingStarted {

    private static Log logger = ExoLogger.getLogger(GettingStarted.class);
    HashMap parameters = new HashMap();
    HashMap<String, String> status = new HashMap();
    int progress = 0;
    String remoteUser;
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
        String remoteUser = RequestContext.getCurrentInstance().getRemoteUser();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode(GettingStartedUtils.JCR_APPLICATION_NODE);
        if (!userPrivateNode.hasNode(GettingStartedUtils.JCR_GS_NODE)) {

            Node gettingStartedNode = userPrivateNode.addNode(GettingStartedUtils.JCR_GS_NODE);
            userPrivateNode.save();
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_DELETE_GADGET_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_PROFILE_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_CONNECT_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_SPACE_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_ACTIVITY_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_DOCUMENT_PROPERTY_NAME, false);
            gettingStartedNode.save();
        }
        gettingStarted.render();
    }

    @Ajax
    @Resource
    public void delete() throws Exception
    {
        //set Delete
        String userId = RequestContext.getCurrentInstance().getRemoteUser();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, userId).getNode(GettingStartedUtils.JCR_APPLICATION_NODE);
        if (userPrivateNode.hasNode(GettingStartedUtils.JCR_GS_NODE))
        {
            Node gettingStartedNode = userPrivateNode.getNode(GettingStartedUtils.JCR_GS_NODE);
            if (gettingStartedNode.hasProperty(GettingStartedUtils.JCR_DELETE_GADGET_PROPERTY_NAME))
            {
                gettingStartedNode.setProperty(GettingStartedUtils.JCR_DELETE_GADGET_PROPERTY_NAME, true);
                gettingStartedNode.save();
            }
        }
        gettingStarted.render();
    }

    @Ajax
    @Resource
    public void getGsList(String reload) throws Exception {
        HashMap bundle = new HashMap();
        Boolean Isshow = true;
        boolean isChange=false;
        PropertyIterator propertiesIt = null;
        remoteUser = RequestContext.getCurrentInstance().getRemoteUser();
        SessionProvider sProvider = SessionProvider.createSystemProvider();
        Node userPrivateNode = nodeHierarchyCreator_.getUserNode(sProvider, remoteUser).getNode(GettingStartedUtils.JCR_APPLICATION_NODE);
        if (userPrivateNode.hasNode(GettingStartedUtils.JCR_GS_NODE))
        {
            Node gettingStartedNode = userPrivateNode.getNode(GettingStartedUtils.JCR_GS_NODE);
            propertiesIt = userPrivateNode.getNode(GettingStartedUtils.JCR_GS_NODE).getProperties("exo:gs_*");
            while (propertiesIt.hasNext())
            {
                Property tempProp= (Property) propertiesIt.next();
                if (tempProp.getName().equals(GettingStartedUtils.JCR_PROFILE_PROPERTY_NAME)){
                    if(isChange==false) isChange = updateAction(tempProp, gettingStartedNode);
                    else updateAction(tempProp, gettingStartedNode);
                    continue;
                }
                if (tempProp.getName().equals(GettingStartedUtils.JCR_CONNECT_PROPERTY_NAME)){
                    if(isChange==false) isChange =  updateAction(tempProp,gettingStartedNode);
                    else updateAction(tempProp, gettingStartedNode);
                    continue;
                }
                if (tempProp.getName().equals(GettingStartedUtils.JCR_ACTIVITY_PROPERTY_NAME)){
                    if(isChange==false) isChange = updateAction(tempProp, gettingStartedNode);
                    else updateAction(tempProp, gettingStartedNode);
                    continue;
                }
                if (tempProp.getName().equals(GettingStartedUtils.JCR_SPACE_PROPERTY_NAME)){
                    if(isChange==false) isChange = updateAction(tempProp,gettingStartedNode);
                    else  updateAction(tempProp, gettingStartedNode);
                    continue;
                }
                if (tempProp.getName().equals(GettingStartedUtils.JCR_DOCUMENT_PROPERTY_NAME)){
                    if(isChange==false) isChange = updateAction(tempProp, gettingStartedNode);
                    else updateAction(tempProp,gettingStartedNode);
                    continue;
                }
            }
            if(progress>100) progress=100;
            if (progress == 100) Isshow = false;
        } else
        {
            Node gettingStartedNode = userPrivateNode.addNode(GettingStartedUtils.JCR_GS_NODE);
            userPrivateNode.save();
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_DELETE_GADGET_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_PROFILE_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_CONNECT_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_SPACE_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_ACTIVITY_PROPERTY_NAME, false);
            gettingStartedNode.setProperty(GettingStartedUtils.JCR_DOCUMENT_PROPERTY_NAME, false);
            gettingStartedNode.save();
        }

        try {
            bundle.put("profile", LinkProvider.getUserProfileUri(remoteUser));
            bundle.put("connect", LinkProvider.getUserConnectionsUri(remoteUser));
            bundle.put("space", GettingStartedUtils.SPACE_URL);
            bundle.put("activity", "#");
            bundle.put("upload", GettingStartedUtils.UPLOAD_URL);
        } catch (MissingResourceException ex) {
            logger.warn("##Missing Labels of GettingStarted Portlet");
        }
        parameters.putAll(bundle);
        parameters.put(GettingStartedUtils.PROGRESS, new Integer(progress));
        parameters.put(GettingStartedUtils.WIDTH, new Integer((Math.round((200 * progress) / 100))).toString());
        parameters.put(GettingStartedUtils.STATUS, status);
        parameters.put(GettingStartedUtils.SHOW, Isshow.toString());
        if ((isChange)||(reload.equals("true")))
            gettingStartedList.render(parameters);
    }

    private boolean updateAction(Property tempProp, Node gettingStartedNode) throws RepositoryException {
        boolean has=false;
        String gsPropertyName=tempProp.getName();
        has =checkStatus(gsPropertyName);
        if(has)  {
            status.put(gsPropertyName.substring(4), GettingStartedUtils.DONE);
            progress+=20;
        }
        else  status.put(gsPropertyName.substring(4), "");
        if(has!=tempProp.getBoolean()){
            gettingStartedNode.setProperty(gsPropertyName, has);
            gettingStartedNode.save();
            return true;
        }
        return false;
    }

    private boolean checkStatus(String gsPropertyName) {
        if (gsPropertyName.equals(GettingStartedUtils.JCR_CONNECT_PROPERTY_NAME)) return GettingStartedService.hasContacts(remoteUser);
        else if (gsPropertyName.equals(GettingStartedUtils.JCR_ACTIVITY_PROPERTY_NAME)) return GettingStartedService.hasActivities(remoteUser);
        else if (gsPropertyName.equals(GettingStartedUtils.JCR_DOCUMENT_PROPERTY_NAME)) return GettingStartedService.hasDocuments(null,remoteUser);
        else if (gsPropertyName.equals(GettingStartedUtils.JCR_SPACE_PROPERTY_NAME)) return GettingStartedService.hasSpaces(remoteUser);
        else return GettingStartedService.hasAvatar(remoteUser);
    }
}


