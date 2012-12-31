package org.exoplatform.platform.portlet.juzu.whoisonline;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;

import java.util.ArrayList;
import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class WhoIsOnlineImpl implements  WhoIsOnline {
    private static Log log = ExoLogger.getLogger(WhoIsOnlineImpl.class);

  public List<Profile> getFriends(String userId) {
      try {


          if(userId == null) {
              return null;
          }

          ForumService forumService = (ForumService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
          IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
          RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);

          Identity myIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
          List<String> users = forumService.getOnlineUsers();



          List<Profile> parameters = new ArrayList<Profile>();
          for (String user : users) {


              Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);

              if (relationshipManager.getStatus(userIdentity, myIdentity) == null)
                  continue;
              else if (!relationshipManager.getStatus(userIdentity, myIdentity).equals(Relationship.Type.CONFIRMED))
                  continue;

              //if user is not a contact, skip him

              Profile userProfile = userIdentity.getProfile();
              parameters.add(userProfile);
              log.info(userProfile.getFullName());
          }

          return parameters;

      }
      catch (Exception e) {
          log.error("Error in who's online rest service: " + e.getMessage(), e);
          return null;
      }

    //throw new UnsupportedOperationException("Not implemented");
  }
}
