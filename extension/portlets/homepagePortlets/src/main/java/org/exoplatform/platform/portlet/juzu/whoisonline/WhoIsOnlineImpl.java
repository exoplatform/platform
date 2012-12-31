package org.exoplatform.platform.portlet.juzu.whoisonline;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnlineImpl implements  WhoIsOnline {
    private static Log log = ExoLogger.getLogger(WhoIsOnlineImpl.class);

  public List<User> getFriends(String userId) {
      try {


          if(userId == null) {
              return null;
          }

          ForumService forumService = (ForumService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
          IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
          RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
          ActivityManager activityManager = (ActivityManager)  ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ActivityManager.class);
          Identity myIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
          List<String> users = forumService.getOnlineUsers();

          if(users.size()>18){
              users=users.subList(0,17);
          }

          List<Profile> parameters = new ArrayList<Profile>();
          List<User> listUser=new ArrayList<User>();
          for (String user : users) {

                 User utilisateur = new User(user);
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
              utilisateur.setAvatar(avatar);
              String position = userProfile.getPosition();
              if (position == null) {position = "";}
              utilisateur.setPosition(position);
              utilisateur.setFullName(userProfile.getFullName())  ;
              utilisateur.setId(userProfile.getId()) ;
              utilisateur.setProfileUrl(userProfile.getUrl()) ;
              utilisateur.setActivity(activitiesList.get(0).getTitle());
              listUser.add(utilisateur);
              log.info(userProfile.getFullName());
          }


          return listUser;

      }
      catch (Exception e) {
          log.error("Error in who's online rest service: " + e.getMessage(), e);
          return null;
      }

    //throw new UnsupportedOperationException("Not implemented");
  }
}
