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
import org.exoplatform.commons.utils.ListAccess;
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
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String OPENSOCIAL_VIEWER_ID = "opensocial_viewer_id" ;
    private static final String INVITATION_TYPE = "invitationType" ;
    private static final String PEOPLE_INVITATION_TYPE = "people" ;
    private static final String SPACE_INVITATION_TYPE = "space" ;
    private static final String SENDER_NAME = "senderName" ;
    private static final String RELATION_ID = "relationId";
    private static final String SENDER_AVATAR_URL = "senderAvatarUrl";
    private static final String SENDER_POSITION = "senderPosition";
    private static final String SPACE_DISPLAY_NAME = "spaceDisplayName";
    private static final String SPACE_ID = "spaceId";
    private static final String SPACE_AVATAR_URL = "spaceAvatarUrl";
    private static final String MEMBERS_NUMBER = "membersNumber";
    private static final String SPACE_REGISTRATION = "spaceRegistration";
    private static final String SENDER_PROFILE_URL = "profile_url";
    private SpaceService spaceService = null;
    private IdentityManager identityManager = null;
    private RelationshipManager relationshipManager = null;
    static {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
    }


    public PeopleAndSpacesRestService(SpaceService spaceService,IdentityManager identityManager,RelationshipManager relationshipManager){

        this.spaceService = spaceService;
        this.identityManager = identityManager;
        this.relationshipManager = relationshipManager;
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
            List<Space> invitedSpaces = new ArrayList<Space>();
            ListAccess<Space> invitedSpacesListAccess = spaceService.getInvitedSpacesWithListAccess(userId);

            Space[] spaces = invitedSpacesListAccess.load(0, invitedSpacesListAccess.getSize());

            if (spaces.length > 0) {

                invitedSpaces = Arrays.asList(spaces);
            }

            //people
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
            List<Relationship> relations = relationshipManager.getIncoming(identity);


            JSONArray jsonArray = new JSONArray();

            for (Space space : invitedSpaces) {

                String avatar = space.getAvatarUrl();
                JSONObject json = new JSONObject();
                json.put(INVITATION_TYPE, SPACE_INVITATION_TYPE);
                json.put(SPACE_DISPLAY_NAME, space.getDisplayName());
                json.put(SPACE_AVATAR_URL, avatar);
                json.put(SPACE_ID, space.getId());
                json.put(MEMBERS_NUMBER, space.getMembers().length);
                json.put(SPACE_REGISTRATION, space.getRegistration());
                jsonArray.put(json);
            }

            for (Relationship relation : relations) {

                Identity senderId = relation.getSender();
                String avatar = senderId.getProfile().getAvatarImageSource();
                JSONObject json = new JSONObject();
                json.put(INVITATION_TYPE, PEOPLE_INVITATION_TYPE);
                json.put(SENDER_NAME, senderId.getProfile().getFullName());
                json.put(RELATION_ID, relation.getId());
                json.put(SENDER_AVATAR_URL, avatar);
                json.put(SENDER_POSITION, senderId.getProfile().getPosition());
                json.put(SENDER_PROFILE_URL, senderId.getProfile().getUrl());
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
            if (queryPart.startsWith(OPENSOCIAL_VIEWER_ID)) {
                return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
            }
        }
        return null;
    }

}