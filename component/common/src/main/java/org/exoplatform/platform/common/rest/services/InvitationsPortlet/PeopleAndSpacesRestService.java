/**
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
package org.exoplatform.platform.common.rest.services.InvitationsPortlet;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 14/12/12
 */
@Path("/homepage/intranet/invitations/")
@Produces("application/json")

public class PeopleAndSpacesRestService implements ResourceContainer {
    private static Log log = ExoLogger.getLogger(PeopleAndSpacesRestService.class);

    private static final CacheControl cacheControl;

    static {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
    }


    @GET
    @Path("allInvitations")
    public Response getAllInvitations(@Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }
            //spaces
            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            List<Space> invitedSpaces = spaceService.getInvitedSpaces(userId);

            //people
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
            RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
            List<Relationship> relations = relationshipManager.getIncoming(identity);


            JSONArray jsonArray = new JSONArray();


            for (Space space : invitedSpaces) {

                String avatar = space.getAvatarUrl();
                if (avatar == null) {
                    avatar = "/social-resources/skin/ShareImages/SpaceImages/SpaceLogoDefault_61x61.gif";
                }

                JSONObject json = new JSONObject();
                json.put("invitationType", "space");
                json.put("name", space.getName());
                json.put("displayName", space.getDisplayName());
                json.put("type", space.getType());
                json.put("spaceUrl", space.getUrl());
                json.put("avatarUrl", avatar);
                json.put("spaceId", space.getId());
                json.put("number", space.getMembers().length);
                json.put("visibility", space.getVisibility());
                json.put("registration", space.getRegistration());
                jsonArray.put(json);
            }

            for (Relationship relation : relations) {

                Identity senderId = relation.getSender();
                String avatar = senderId.getProfile().getAvatarImageSource();
                if (avatar == null) {
                    avatar = "/social-resources/skin/ShareImages/Avatar.gif";
                }

                JSONObject json = new JSONObject();
                json.put("invitationType", "people");
                json.put("senderName", senderId.getProfile().getFullName());
                json.put("relationId", relation.getId());
                json.put("avatar", avatar);
                json.put("profile", senderId.getProfile().getUrl());
                json.put("position", senderId.getProfile().getPosition());
                jsonArray.put(json);
            }

            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            log.error("Error in space invitation rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    private String getUserId(SecurityContext sc, UriInfo uriInfo) {
        try {
            return sc.getUserPrincipal().getName();
        } catch (NullPointerException e) {
            return getViewerId(uriInfo);
        } catch (Exception e) {
            return null;
        }
    }

    private String getViewerId(UriInfo uriInfo) {
        URI uri = uriInfo.getRequestUri();
        String requestString = uri.getQuery();
        if (requestString == null) return null;
        String[] queryParts = requestString.split("&");
        for (String queryPart : queryParts) {
            if (queryPart.startsWith("opensocial_viewer_id")) {
                return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
            }
        }
        return null;
    }

}
