package org.exoplatform.platform.common.rest.services.SuggestSpacesPortlet;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.container.ExoContainer;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;


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

    // The owner of the rest component
    private final ExoContainer container;

    public SpaceRestServices(ExoContainerContext ctx) {
        this.container = ctx.getContainer();
    }

    @GET
    @Path("suggestions")
    public Response getSuggestions(@Context SecurityContext sc, @Context UriInfo uriInfo) {
        
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonGlobal = new JSONObject();
            
            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
            ListAccess<Space> suggestedSpacesLA = spaceService.getPublicSpacesWithListAccess(userId);

            // new create system with no spaces
            int size = suggestedSpacesLA.getSize();
            if (size == 0) {
              jsonGlobal.put("items",jsonArray);
              jsonGlobal.put("noConnections", 0);
              return Response.ok(jsonGlobal.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
            }
            
            IdentityManager identityManager = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId, false);
            ListAccess<Identity> connectionsLA = identityManager.getConnectionsWithListAccess(identity);
            
            final Map<Space, Integer> spacesWithMemberNum = new HashMap<Space, Integer>();
            int maxConnectionsToLoad = 100;
            int maxSpacesToLoad = 50;
            int maxSuggestions = 10;
            int totalConnections = connectionsLA.getSize();
            Random random = new Random();
            Identity[] connections;
            if (totalConnections > maxConnectionsToLoad) {
               int startIndex = random.nextInt(totalConnections - maxConnectionsToLoad);
               connections = connectionsLA.load(startIndex, maxConnectionsToLoad);
            } else {
               connections = connectionsLA.load(0, totalConnections);
            }
            Space[] suggestedSpaces;
            if (size > maxSpacesToLoad) {
               int startIndex = random.nextInt(size - maxSpacesToLoad);
               suggestedSpaces = suggestedSpacesLA.load(startIndex, maxSpacesToLoad);
            } else {
               suggestedSpaces = suggestedSpacesLA.load(0, size);
            }
            for (Space space : suggestedSpaces) {
              for (Identity connector : connections) {
                //
                if (Space.HIDDEN.equals(space.getVisibility()))
                  continue;
                if (Space.CLOSE.equals(space.getRegistration()))
                  continue;
                if (!spaceService.isMember(space, connector.getRemoteId())) 
                  continue;
                
                //
                Integer value = spacesWithMemberNum.get(space);
                
                if (value == null) {
                  value = new Integer(1);
                } else {
                  value = new Integer(value.intValue() + 1);
                }
                spacesWithMemberNum.put(space, value);
              }
            }
            
            if (!spacesWithMemberNum.isEmpty()) {
              NavigableMap<Integer, List<Space>> groupByCommonConnections = new TreeMap<Integer, List<Space>>();
              // This for loop allows to group the suggestions by total amount of common connections
              for (Space space : spacesWithMemberNum.keySet()) {
                Integer commonSpaces = spacesWithMemberNum.get(space);
                List<Space> spaces = groupByCommonConnections.get(commonSpaces);
                if (spaces == null) {
                  spaces = new ArrayList<Space>();
                  groupByCommonConnections.put(commonSpaces, spaces);
                }
                spaces.add(space);
              }
              int suggestionLeft = maxSuggestions;
              // We iterate over the suggestions starting from the suggestions with the highest amount common
              // connections
              main: for (Integer key : groupByCommonConnections.descendingKeySet()) {
                List<Space> spaces = groupByCommonConnections.get(key);
                for (Space space : spaces) {
                  JSONObject json = buildJSONObject(space, key);
                  jsonArray.put(json);
                  // We stop once we have enough suggestions
                  if (--suggestionLeft == 0)
                    break main;
                }
              }
            } else {
              // Propose the last spaces
              List<Space> lastSpaces = spaceService.getLastSpaces(10);
              for (Space space : lastSpaces) {
                if (Space.HIDDEN.equals(space.getVisibility()))
                  continue;
                if (Space.CLOSE.equals(space.getRegistration()))
                  continue;
                if (spaceService.isMember(space, identity.getRemoteId())) 
                  continue;
                if (spaceService.isPendingUser(space, identity.getRemoteId())) 
                   continue;
                if (spaceService.isInvitedUser(space, identity.getRemoteId())) 
                  continue;
                JSONObject json = buildJSONObject(space, 0);
                jsonArray.put(json);
              }
            }
            
            jsonGlobal.put("items",jsonArray);
            jsonGlobal.put("noConnections", spacesWithMemberNum.size());
            return Response.ok(jsonGlobal.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();

        } catch (Exception e) {
            LOG.error("Error in space invitation rest service: " + e.getMessage(), e);
            return Response.ok("error").cacheControl(cacheControl).build();
        }
    }

    private JSONObject buildJSONObject(Space space, int k) throws JSONException {
      String avatar = space.getAvatarUrl();
      if (avatar == null) {
          avatar = "/eXoSkin/skin/images/Default/social/skin/ShareImages/UserAvtDefault.png";
      }
      
      String spaceType = "";
      if (space.getRegistration() == null || space.getRegistration().equals(Space.OPEN)) {
          spaceType = "Public";
      } else {
          spaceType = "Private";
      }
      
      JSONObject json = new JSONObject();
      json.put("name", space.getPrettyName());
      json.put("spaceId", space.getId());
      json.put("displayName", space.getDisplayName());
      json.put("spaceUrl", space.getUrl());
      json.put("avatarUrl", avatar);
      json.put("registration", space.getRegistration());
      json.put("members", space.getMembers() == null ? 0 : space.getMembers().length);
      json.put("privacy", spaceType);
      json.put("number", k);
      json.put("createdDate", space.getCreatedTime());
      return json;
    }

    @GET
    @Path("accept/{spaceName}")
    public Response accept(@PathParam("spaceName") String spaceName, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
            }

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);

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

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
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

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
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

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
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

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
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

            SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
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
