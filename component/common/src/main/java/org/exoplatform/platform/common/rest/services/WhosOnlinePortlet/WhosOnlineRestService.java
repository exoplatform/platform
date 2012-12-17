package org.exoplatform.platform.common.rest.services.WhosOnlinePortlet;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import java.util.List;


@Path("/homepage/intranet/online/")
@Produces("application/json")
public class WhosOnlineRestService implements ResourceContainer {
  
  private static Log log = ExoLogger.getLogger(WhosOnlineRestService.class);
  
  private static final CacheControl cacheControl;
  static {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }
  
  @GET
  @Path("contacts")
  public Response getOnlineContacts(@Context SecurityContext sc, @Context UriInfo uriInfo) {
    
    try {
      
      String userId = getUserId(sc, uriInfo);
      if(userId == null) {
        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
      }
           
      ForumService forumService = (ForumService)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
      IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
      RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);


      ActivityManager activityManager = (ActivityManager)  ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ActivityManager.class);
      Identity myIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);

      List<String> users = forumService.getOnlineUsers();

        if(users.size()>18){
      users=  users.subList(0,17) ;
        }
      JSONArray jsonArray = new JSONArray();

      for (String user : users) {


        Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);
        List<ExoSocialActivity> activitiesList= activityManager.getActivities(userIdentity);
        if (relationshipManager.getStatus(userIdentity, myIdentity) == null)
        	continue;	
        else if (!relationshipManager.getStatus(userIdentity, myIdentity).equals(Relationship.Type.CONFIRMED))
            continue;

        //if user is not a contact, skip him             
         
        Profile userProfile = userIdentity.getProfile();  
        String avatar = userProfile.getAvatarImageSource();
            if (avatar == null) {avatar = "/social-resources/skin/ShareImages/Avatar.gif"; }
        String position = userProfile.getPosition();
            if (position == null) {position = "";}   
        
        JSONObject json = new JSONObject();                    
        json.put("id", user);
        json.put("name", userProfile.getFullName());
        json.put("avatarUrl", avatar);
        json.put("profileUrl", userProfile.getUrl());
        json.put("title", position);
        json.put("activity",activitiesList.get(0).getTitle());
        jsonArray.put(json);    
      }
      
      return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
      
    }
    catch (Exception e) {
      log.error("Error in who's online rest service: " + e.getMessage(), e);
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