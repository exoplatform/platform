import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
import org.exoplatform.social.webui.profile.UIProfileUserSearch;


@Path("friendSuggestion")
public class FriendSuggestion {
  private RelationshipManager relationshipManager;

  /** Stores IdentityManager instance. */
  private IdentityManager identityManager = null;

  /** Stores UIProfileUserSearch instance. */
  UIProfileUserSearch uiProfileUserSearchRelation = null;

  @GET
  @Path("/getSuggestedFriends")
  public Response getSuggestedFriends(@Context SecurityContext sc) throws Exception {
    String name = sc.getUserPrincipal().getName();
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OrganizationService orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    IdentityManager im = getIdentityManager();
    RelationshipManager relm = getRelationshipManager();
    
    Identity currentIdentity = im.getOrCreateIdentity(OrganizationIdentityProvider.NAME, name);

    // Get identities level 1
    List<Identity> relationIdLevel1 = new ArrayList<Identity>();
    List<Relationship> allRelations = relm.getContacts(currentIdentity);
    for (Relationship rel : allRelations) {
      Identity id = (currentIdentity.getId() == (rel.getSender()).getId()) ? rel.getReceiver() : rel.getSender();
      relationIdLevel1.add(id);
    }

    // Get identities level 2 (suggested Identities)
    List<Object> suggestedUserProfiles = new ArrayList<Object>();
    List<Identity> suggestedIdentities = new ArrayList<Identity>();
    for (Identity identity : relationIdLevel1) {
        Identity id = im.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identity.getRemoteId());
        List<Relationship> allRels = relm.getContacts(id);
        for (Relationship rel : allRels) {
            Identity ids = (currentIdentity.getId() == (rel.getSender()).getId()) ? rel.getReceiver() : rel.getSender();
            if (!ids.getRemoteId().equals(id.getRemoteId())) suggestedIdentities.add(ids);
        }
    }

    for (Identity identity : suggestedIdentities) {
      FriendProfile friendProfile = new FriendProfile();
      friendProfile.setId(identity.getRemoteId());
      friendProfile.setFullName(identity.getProfile().getFullName());
      friendProfile.setAvatarPath(identity.getProfile().getAvatarImageSource());
      List<Relationship> idenRelations = relm.getContacts(identity);
      List<Identity> relationIdLevel3 = new ArrayList<Identity>();
      for (Relationship rel : idenRelations) {
        Identity id = (identity.getId() == (rel.getSender()).getId()) ? rel.getReceiver() : rel.getSender();
        relationIdLevel3.add(id);
      }
      friendProfile.setMutualFriends(getMutualFriends(name, relationIdLevel1, relationIdLevel3));
      boolean exists = false ;
      Relationship rel;
      rel = relm.getRelationship(identity, currentIdentity);
      Relationship.Type contactStatus = null;
      if (rel != null) {
        contactStatus = rel.getStatus();
      }
                
      for (int i = 0; i < suggestedUserProfiles.size(); i++) {
          if (suggestedUserProfiles.get(i).getId().equals(friendProfile.getId())) {
              exists = true;
              break;
          }
      }
      if ((!friendProfile.getId().equals(name)) && (contactStatus != Relationship.Type.PENDING) && (contactStatus != Relationship.Type.CONFIRM) && (contactStatus != Relationship.Type.IGNORE) && !exists) suggestedUserProfiles.add(friendProfile);
    }

    MessageBean bean = new MessageBean();
    bean.setData(suggestedUserProfiles);
    
    return Response.ok(bean, new MediaType("application", "json")).build();
  }
  
  @GET
  @Path("/getProfile/{id}")
  public Response getProfile(@PathParam("id") String id) throws Exception {
    FriendProfile fp = new FriendProfile();
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OrganizationService orgService = (OrganizationService) container.getComponentInstanceOfType(OrganizationService.class);
    IdentityManager im = getIdentityManager();
    RelationshipManager relm = getRelationshipManager();
    Identity identity = im.getOrCreateIdentity(OrganizationIdentityProvider.NAME, id);
    fp.setId(id);
    fp.setFullName(identity.getProfile().getFullName());
    fp.setAvatarPath(identity.getProfile().getAvatarImageSource());
    return Response.ok(fp, new MediaType("application", "json")).build();
  }
  
  private List<String> getMutualFriends(String name, List<Identity> friendIds, List<Identity> friendsFriendsIds) {
    List<String> mutuals = new ArrayList<String>();
    for (int i = 0; i < friendIds.size(); i++) {
        String us = friendIds.get(i).getRemoteId();
        if (!us.equals(name)) {
        for (int j=0; j < friendsFriendsIds.size(); j++) {
            if (us.equals(friendsFriendsIds.get(j).getRemoteId())) {
                mutuals.add(friendIds.get(i).getRemoteId());
                continue;
            }    
        } 
        }
    }
    return mutuals;
  }

  private RelationshipManager getRelationshipManager() {
    if (relationshipManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      relationshipManager = (RelationshipManager) container
          .getComponentInstanceOfType(RelationshipManager.class);
    }
    return relationshipManager;
  }

  private IdentityManager getIdentityManager() {
    if (identityManager == null) {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      identityManager = (IdentityManager) container
          .getComponentInstanceOfType(IdentityManager.class);
    }
    return identityManager;
  }
  
}
  
public class FriendProfile {
  private String Id_ ;
  
  private String fullName_ ;
  
  private List<String> mutualFriends_ ;
  
  private String avatarPath_ ;
  
  public void setFullName(String fullName) {
      fullName_ = fullName;
  }
  
  public String getFullName() {
      return fullName_;
  }
  
  public void setId(String id) {
      Id_ = id;
  }
  
  public String getId() {
    return Id_;  
  }
  
  public void setAvatarPath(String avatarPath) {
      avatarPath_ = avatarPath;
  }
  
  public String getAvatarPath() {
      return avatarPath_;
  }

  public void setMutualFriends(List<String> mutualFriends) {
    mutualFriends_ = mutualFriends;    
  }

  public List<String> getMutualFriends() {
   return mutualFriends_;
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
