import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.relationship.model.Relationship.Type;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.URI;
import org.exoplatform.social.core.activity.model.Activity;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Path("suggestion")
public class Suggestion {

  private RelationshipManager relationshipManager = null;

  private IdentityManager identityManager = null;

  private static final CacheControl cacheControl;
  static {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }

  @GET
  @Path("my-friends")
  @Produces("application/json")
  public Response myContacts(@Context SecurityContext sc, @Context UriInfo uriInfo) {
    try {
      String viewerId = getUserId(sc, uriInfo);
      if(viewerId == null) {
        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
      }

      IdentityManager identityManager = getIdentityManager();
      RelationshipManager relationshipManager = getRelationshipManager();

      ActivityManager activityManager = (ActivityManager)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ActivityManager.class);

      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, viewerId);
      Profile profile = identity.getProfile();
      List<Relationship> confirmedContacts = relationshipManager.getContacts(identity);

      List<Object> contacts = new ArrayList<Object>(confirmedContacts.size());

      for(Relationship contact : confirmedContacts){
        Identity contactIdentity = contact.getSender();
        if(viewerId.equals(contactIdentity.getRemoteId())) {
           contactIdentity = contact.getReceiver();
        }
        profile = contactIdentity.getProfile();

        ContactBean contactBean = new ContactBean();
        contactBean.setId(contactIdentity.getRemoteId());
        contactBean.setFullName(profile.getFullName());
        contactBean.setAvatarUrl(profile.getAvatarImageSource());

        contacts.add(contactBean);
      }

      MessageBean data = new MessageBean();
      data.setData(contacts);
      return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch(Exception e) {
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  @GET
  @Path("suggested-friends")
  @Produces("application/json")
  public Response suggestedFriends(@Context SecurityContext sc, @Context UriInfo uriInfo) {
    try {
      String viewerId = getUserId(sc, uriInfo);
      if(viewerId == null) {
        return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
      }

      IdentityManager identityManager = getIdentityManager();
      RelationshipManager relationshipManager = getRelationshipManager();

      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, viewerId);
      Map<String, Object> suggestedFriendsMap = new HashMap<String, Object>();

      Profile profile = null;
      List<Relationship> mfConfirmedContacts = new ArrayList<Relationship>();
      List<Identity> mfConfirmedContactIdentities = new ArrayList<Identity>(getAllConfirmFriends(identity).values());

      for(Identity mfIdentity : mfConfirmedContactIdentities) {
        if (!mfIdentity.getRemoteId().equals(viewerId)) {
          mfConfirmedContacts = relationshipManager.getContacts(mfIdentity);

          for(Relationship contact : mfConfirmedContacts) {
            Identity contactIdentity = (mfIdentity.getRemoteId().equals(contact.getSender().getRemoteId())) ? contact.getReceiver() : contact.getSender();

            if (!suggestedFriendsMap.containsKey(contactIdentity.getRemoteId())) {
              Type ourRelationship = relationshipManager.getConnectionStatus(identity, contactIdentity);

              if (ourRelationship == Type.ALIEN) {
                profile = contactIdentity.getProfile();

                ContactBean contactBean = new ContactBean();
                contactBean.setId(contactIdentity.getRemoteId());
                contactBean.setFullName(profile.getFullName());
                contactBean.setAvatarUrl(profile.getAvatarImageSource());
                suggestedFriendsMap.put(contactBean.getId(), contactBean);
              }
            }
          }
        }
      }
      MessageBean data = new MessageBean();
      List<Object> suggestedFriends = new ArrayList<Object>(suggestedFriendsMap.values());

      data.setData(suggestedFriends);
      return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch(Exception e) {
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  @GET
  @Path("/send-invitation/{id}")
  public Response add(@Context SecurityContext sc, @PathParam("id") String id) throws Exception {
      String name = sc.getUserPrincipal().getName();

      IdentityManager identityManager = getIdentityManager();
      RelationshipManager relationshipManager = getRelationshipManager();

      Identity currentIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, name);
      Identity requestedIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, id);

      relationshipManager.invite(currentIdentity, requestedIdentity);

      return Response.ok(requestedIdentity.getProfile().getFullName(), new MediaType("application", "json")).build();
  }

  @GET
  @Path("/get-mutual-friends/{sfId}")
  public Response sendInvitation(@Context SecurityContext sc, @Context UriInfo uriInfo,@PathParam("sfId") String sfId) {
    String viewerId = getUserId(sc, uriInfo);
    if(viewerId == null) return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();

    IdentityManager identityManager = getIdentityManager();
    Identity myConfirmedContact = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, viewerId);
    Identity sfIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, sfId);
    List<Object> mutualFriends = new ArrayList<Object>();

    try {
      Map<String, Identity> myContacts = getAllConfirmFriends(myConfirmedContact);
      Map<String, Identity> sfContacts = getAllConfirmFriends(sfIdentity);
      mutualFriends = getMutualFriends(myContacts, sfContacts);

      MessageBean data = new MessageBean();
      data.setData(mutualFriends);

      return Response.ok(data, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    } catch(Exception e) {
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  private List<Object> getMutualFriends(Map<String, Identity> friendIds, Map<String, Identity> friendsFriendsIds) {
    List<Object> mutuals = new ArrayList<Object>();
    Identity ffi = null;
    List<String> keys = new ArrayList<String>(friendsFriendsIds.keySet());
    for (String key : keys) {
      if (friendIds.containsKey(key)) {
        ffi = friendsFriendsIds.get(key);
        Profile profile = ffi.getProfile();
        ContactBean contactBean = new ContactBean();
        contactBean.setId(ffi.getRemoteId());
        contactBean.setFullName(profile.getFullName());
        contactBean.setAvatarUrl(profile.getAvatarImageSource());
        mutuals.add(contactBean);
        continue;
      }
    }
    return mutuals;
  }

  private Map<String, Identity> getAllConfirmFriends(Identity identity) throws Exception {
    Map<String, Identity> identities = new HashMap<String, Identity>();
    List<Relationship> confirmedContacts = relationshipManager.getContacts(identity);
    Identity friendIdentity = null;
    for(Relationship myConfirmedContact : confirmedContacts) {
      friendIdentity = myConfirmedContact.getSender();
      if (identity.getRemoteId().equals(friendIdentity.getRemoteId())) {
         friendIdentity = myConfirmedContact.getReceiver();
      }
      identities.put(friendIdentity.getRemoteId(), friendIdentity);
    }
    return identities;
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

  private RelationshipManager getRelationshipManager() {
    if (relationshipManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      relationshipManager = (RelationshipManager) container.getComponentInstanceOfType(RelationshipManager.class);
    }
    return relationshipManager;
  }

  private IdentityManager getIdentityManager() {
    if (identityManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      identityManager = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
    }
    return identityManager;
  }
}

public class ContactBean{
  private String id;
  private String fullName;
  private String avatarUrl;
  private String position;
  private String latestActivity;
  private List<String> mutualFriends_ ;

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  public String getAvatarUrl() {
    return avatarUrl;
  }
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
  public String getPosition() {
    return position;
  }
  public void setPosition(String position) {
    this.position = position;
  }
  public String getLatestActivity() {
    return latestActivity;
  }
  public void setLatestActivity(String latestActivity) {
    this.latestActivity = latestActivity;
  }
}

public class MessageBean {
  private List<Object> data;

  public void setData(List<Object> list) {
    this.data = list;
  }
  public List<Object> getData() {
    return data;
  }
}
