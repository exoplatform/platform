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
package org.exoplatform.platform.common.rest.services.GettingStarted;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("homepage/intranet/getting-started/")
@Produces(MediaType.APPLICATION_JSON)
public class GettingStartedRestService implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(GettingStartedRestService.class);
    private static final CacheControl cacheControl;
    static {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
    }

    /**
     * Get user's getting started status REST service URL: /getting-started/get
     *
     * @return: user's getting started status
     */
    @GET
    @Path("get")
    public Response get(@Context
                        SecurityContext sc, @Context
    UriInfo uriInfo) throws Exception {
        SessionProvider sProvider = null;
        try {
            String userId = ConversationState.getCurrent().getIdentity().getUserId();
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(NodeHierarchyCreator.class);
            sProvider = SessionProvider.createSystemProvider();
            Node userPrivateNode = nodeCreator.getUserNode(sProvider, userId).getNode("ApplicationData");
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

            Node gettingStartedNode = userPrivateNode.getNode("GsGadget");
            gettingStartedNode.setProperty("exo:gs_profile", hasAvatar(userId));
            gettingStartedNode.setProperty("exo:gs_connect", hasContacts(userId));
            gettingStartedNode.setProperty("exo:gs_space", hasSpaces(userId));
            gettingStartedNode.setProperty("exo:gs_activities", hasActivities(userId));
            gettingStartedNode.setProperty("exo:gs_document", hasDocuments(userId));

            PropertyIterator propertiesIt = userPrivateNode.getNode("GsGadget").getProperties("exo:gs_*");
            JSONArray jsonArray = new JSONArray();

            while (propertiesIt.hasNext()) {
                Property prop = (Property) propertiesIt.next();
                JSONObject json = new JSONObject();
                json.put("name", prop.getName());
                json.put("value", prop.getString());
                jsonArray.put(json);
            }
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON)
                    .cacheControl(cacheControl)
                    .build();
        } catch (Exception e) {
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
        } finally {
            if (sProvider != null) {
                sProvider.close();
            }

        }
    }

    private Boolean hasDocuments(String userId) {
        return true;
    }

    @SuppressWarnings("deprecation")
    private boolean hasAvatar(String userId) {
        try {
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
            Profile profile = identity.getProfile();

            if (profile.getAvatarUrl() != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean hasSpaces(String userId) {
        try {
            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(SpaceService.class);
            Space[] spaces = spaceService.getAccessibleSpacesWithListAccess(userId).load(0, 1);
            return spaces != null && spaces.length > 0;
            
        } catch (Exception e) {
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean hasActivities(String userId) {
        try {
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            ActivityManager activityService = (ActivityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(ActivityManager.class);
            RealtimeListAccess activities = activityService.getActivitiesWithListAccess(identity);

            if (activities.getSize() != 0) {

            if ((hasAvatar(userId)) && (hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 5))
                return true;
            else if ((hasAvatar(userId)) && (hasContacts(userId)) && (!hasSpaces(userId)) && (activities.getSize() >= 4))
                return true;
            else if ((hasAvatar(userId)) && (!hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 3))
                return true;

            else if ((!hasAvatar(userId)) && (hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 4))
                return true;
            else if ((!hasAvatar(userId)) && (!hasContacts(userId)) && (hasSpaces(userId)) && (activities.getSize() >= 2))
                return true;
            else if ((!hasAvatar(userId)) && (!hasContacts(userId)) && (!hasSpaces(userId)) && (activities.getSize() >= 1))
                return true;
                else return false;
            }
            else return false;
        } catch (Exception e) {
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean hasContacts(String userId) {
        try {

            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(IdentityManager.class);
            RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer()
                    .getComponentInstanceOfType(RelationshipManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                    userId);
            ListAccess<Identity> confirmedContacts = relationshipManager.getConnections(identity);

            return confirmedContacts.getSize() > 0;
        } catch (Exception e) {
            LOG.debug("Error in gettingStarted REST service: " + e.getMessage(), e);
            return false;
        }
    }

}
