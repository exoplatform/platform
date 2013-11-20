package org.exoplatform.platform.portlet.juzu.whoisonline;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnlineImpl implements WhoIsOnline {
    private static final Log log = ExoLogger.getLogger(WhoIsOnlineImpl.class);
    private static final int MAX_USER = 17;
    private static final int INDEX_USER = 18;

    public List<User> getFriends(String userId) {
        List<User> userOnLineList = new ArrayList<User>();
        if (userId == null) return userOnLineList;
        
        try {
            ForumService forumService = (ForumService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
            List<String> users = forumService.getOnlineUsers();
            if (users.contains(userId)) {
                users.remove(userId);
            }
            Collections.reverse(users);
            if (users.size() > MAX_USER) {
                users = users.subList(0, INDEX_USER);
            }

            User userOnLine = null;
            
            for (String user : users) {
                userOnLine = new User(user);
                Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user,false);
                Profile userProfile = userIdentity.getProfile();
                userOnLine.setId(userProfile.getId());
                userOnLine.setProfileUrl(LinkProvider.getUserProfileUri(userIdentity.getRemoteId()));
                userOnLine.setAvatar(userProfile.getAvatarImageSource() != null ? userProfile.getAvatarImageSource() : LinkProvider.PROFILE_DEFAULT_AVATAR_URL);
                userOnLineList.add(userOnLine);
            }
            return userOnLineList;

        } catch (Exception e) {
            log.error("Error while checking logged users [WhoIsOnLine rendering phase] :" + e.getMessage(), e);
            return null;
        }
    }
}
