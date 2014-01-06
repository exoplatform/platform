package org.exoplatform.platform.common.rest.services.SuggestSpacesPortlet;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Path("/homepage/intranet/spaces/")
@Produces("application/json")
public class SpaceRestServices implements ResourceContainer {

    private static final Log LOG = ExoLogger.getLogger(SpaceRestServices.class);

    private static final CacheControl cacheControl;

    static {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
    }

    @GET
    @Path("suggestions")
    public Response getSuggestions(@Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            List<Space> suggestedSpaces = spaceService.getPublicSpaces(userId);
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
            List<Identity> connections = identityManager.getConnections(identity);

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonGlobal = new JSONObject();
            for (Space space : suggestedSpaces) {

                if (space.getVisibility().equals(Space.HIDDEN))
                    continue;
                if (space.getRegistration().equals(Space.CLOSE))
                    continue;
                List<Identity> identityListMember = new ArrayList<Identity>();
                String avatar = space.getAvatarUrl();
                if (avatar == null) {
                    avatar = "/social-resources/skin/images/ShareImages/UserAvtDefault.png";
                }
                for (String mem : space.getMembers()) {
                    Identity identityMem = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, mem);
                    identityListMember.add(identityMem);
                }
                int k = 0;
                for (Identity i : identityListMember) {
                    for (Identity j : connections) {
                        if (j.equals(i)) {
                            k++;
                        }
                    }
                }
                String spaceType = "";
                if (space.getRegistration().equals(Space.OPEN)) {
                    spaceType = "Public";
                } else {
                    spaceType = "Private";
                }
                JSONObject json = new JSONObject();
                json.put("name", space.getName());
                json.put("spaceId", space.getId());
                json.put("displayName", space.getDisplayName());
                json.put("spaceUrl", space.getUrl());
                json.put("avatarUrl", avatar);
                json.put("registration", space.getRegistration());
                json.put("members", space.getMembers().length);
                json.put("privacy", spaceType);
                json.put("number", k);
                json.put("createdDate", space.getCreatedTime());
                jsonArray.put(json);
            }
            jsonGlobal.put("items",jsonArray);
            jsonGlobal.put("noConnections",connections.size());
            return Response.ok(jsonGlobal.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            LOG.error("Error in space invitation rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    @GET
    @Path("accept/{spaceName}")
    public Response accept(@PathParam("spaceName") String spaceName, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);

            if (spaceService.isInvitedUser(spaceService.getSpaceById(spaceName), userId))
                spaceService.addMember(spaceService.getSpaceById(spaceName), userId);

            return Response.ok("{}", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            LOG.error("Error in space accept rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    @GET
    @Path("deny/{spaceName}")
    public Response deny(@PathParam("spaceName") String spaceName, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            spaceService.removeInvitedUser(spaceService.getSpaceById(spaceName), userId);

            return Response.ok("{}", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        } catch (Exception e) {
            LOG.error("Error in space deny rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    @GET
    @Path("request/{spaceName}")
    public Response request(@PathParam("spaceName") String spaceName, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            spaceService.addPendingUser(spaceService.getSpaceById(spaceName), userId);

            return Response.ok("{}", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        } catch (Exception e) {
            LOG.error("Error in space deny rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    @GET
    @Path("join/{spaceName}")
    public Response join(@PathParam("spaceName") String spaceName, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            if (spaceService.getSpaceById(spaceName).getRegistration().equals("open"))
                spaceService.addMember(spaceService.getSpaceById(spaceName), userId);


            return Response.ok("{}", MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        } catch (Exception e) {
            LOG.error("Error in space deny rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    @GET
    @Path("myspaces")
    public Response request(@Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            List<Space> mySpaces = spaceService.getAccessibleSpaces(userId);

            JSONArray jsonArray = new JSONArray();

            for (Space space : mySpaces) {
                JSONObject json = new JSONObject();
                json.put("name", space.getName());
                json.put("spaceId", space.getId());
                json.put("displayName", space.getDisplayName());
                json.put("spaceUrl", space.getUrl());
                json.put("members", space.getMembers().length);
                jsonArray.put(json);
            }

            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            LOG.error("Error in space deny rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }

    }

    @GET
    @Path("public")
    public Response getPublicSpaces(@Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {
            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(SpaceService.class);
            ListAccess<Space> publicSpaces = spaceService.getPublicSpacesWithListAccess(userId);

            JSONArray jsonArray = new JSONArray();

            Space[] spaces = publicSpaces.load(0, publicSpaces.getSize());
            if (spaces != null && spaces.length > 0) {
                for (Space space : spaces) {

                    if (space.getVisibility().equals(Space.HIDDEN))
                        continue;
                    if (space.getRegistration().equals(Space.CLOSE))
                        continue;

                    JSONObject json = new JSONObject();
                    json.put("name", space.getName());
                    json.put("displayName", space.getDisplayName());
                    json.put("spaceId", space.getId());
                    jsonArray.put(json);
                }
            }

            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
        } catch (Exception e) {
            LOG.error("Error in space invitation rest service: " + e.getMessage(), e);
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