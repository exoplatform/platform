package org.exoplatform.platform.common.rest.services.WhoIsOnLinePortlet;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.user.UserStateModel;
import org.exoplatform.services.user.UserStateService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.notification.LinkProviderUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Collections;
import java.util.List;

@Path("/whoIsOnline")
@Produces("application/json")
public class WhoIsOnLineRestService implements ResourceContainer {

    private IdentityManager identityManager;
    private UserStateService userStateService;
    private SpaceService spaceService;
    private static final int MAX_USER = 17;
    private static final int INDEX_USER = 18;
    private static final String INVISIBLE = "invisible";


    public WhoIsOnLineRestService(IdentityManager identityManager, UserStateService userStateService, SpaceService spaceService) {
        this.identityManager = identityManager;
        this.userStateService = userStateService;
        this.spaceService = spaceService;
    }

    @GET
    @Path("{userId}/{spaceName}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("users")
    @ApiOperation(value = "Gets a space description by pretty name",
            httpMethod = "GET",
            response = Response.class,
            notes = "This can only be done by the logged in user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Request fulfilled"),
            @ApiResponse (code = 500, message = "Internal server error"),
            @ApiResponse (code = 400, message = "Invalid query input"),
            @ApiResponse (code = 404, message = "Resource not found")})
    public Response getOnlineFriends(@Context UriInfo uriInfo,
                                                    @Context Request request,
                                                    @ApiParam(value = "Space pretty name", required = true) @PathParam("userId") String userId, @PathParam("spaceName") String spaceName) {

        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isBlank(userId)) {
            userId = ConversationState.getCurrent().getIdentity().getUserId();
        }
        if (StringUtils.isBlank(userId)) {
            return Response.ok(jsonArray, MediaType.APPLICATION_JSON).build();
        }
        Space space = null;
        if (StringUtils.isNotBlank(spaceName)) {
            space = spaceService.getSpaceByPrettyName(spaceName);
        }
        if (space != null) {
            jsonArray = getOnlineUsersMembersOfSpace(userId, space);
        } else {
            jsonArray = getOnlineUsers(userId);
        }
        return Response.ok(jsonArray, MediaType.APPLICATION_JSON).build();
    }

    private JSONArray getOnlineUsersMembersOfSpace(String userId, Space space) {
        JSONArray jsonArray = new JSONArray();
        try {
            List<UserStateModel> users = userStateService.online();
            Collections.reverse(users);
            if (users.size() > MAX_USER) {
                users = users.subList(0, INDEX_USER);
            }
            String superUserName = System.getProperty("exo.super.user");
            for (UserStateModel userModel : users) {
                String user = userModel.getUserId();
                if (user.equals(userId) || user.equals(superUserName) || INVISIBLE.equals(userModel.getStatus()) || !spaceService.isMember(space,user)) continue;
                JSONObject json = new JSONObject();
                Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user,false);
                Profile userProfile = userIdentity.getProfile();
                json.put("id", userProfile.getId());
                json.put("profileUrl", LinkProvider.getUserProfileUri(userIdentity.getRemoteId()));
                json.put("avatar", LinkProviderUtils.getUserAvatarUrl(userProfile));
                jsonArray.put(json);
            }
            return jsonArray;

        } catch (Exception e) {
            return jsonArray;
        }
    }

    private JSONArray getOnlineUsers(String userId) {
        JSONArray jsonArray = new JSONArray();
        try {
            List<UserStateModel> users = userStateService.online();
            Collections.reverse(users);
            if (users.size() > MAX_USER) {
                users = users.subList(0, INDEX_USER);
            }

            String superUserName = System.getProperty("exo.super.user");
            for (UserStateModel userModel : users) {
                String user = userModel.getUserId();
                if (user.equals(userId) || user.equals(superUserName) || INVISIBLE.equals(userModel.getStatus())) continue;
                JSONObject json = new JSONObject();
                Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user,false);
                Profile userProfile = userIdentity.getProfile();
                json.put("id", userProfile.getId());
                json.put("profileUrl", LinkProvider.getUserProfileUri(userIdentity.getRemoteId()));
                json.put("avatar", LinkProviderUtils.getUserAvatarUrl(userProfile));
                jsonArray.put(json);
            }
            return jsonArray;

        } catch (Exception e) {
            return jsonArray;
        }
    }
}
