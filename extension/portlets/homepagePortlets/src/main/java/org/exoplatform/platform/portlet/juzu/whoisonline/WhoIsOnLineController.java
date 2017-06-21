package org.exoplatform.platform.portlet.juzu.whoisonline;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.user.UserStateModel;
import org.exoplatform.services.user.UserStateService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
public class WhoIsOnLineController {
    private static final int MAX_USER = 17;
    private static final int INDEX_USER = 18;

    private static final String INVISIBLE = "invisible";

    private static final Log LOG = ExoLogger.getLogger(WhoIsOnLineController.class);

    @Inject
    @Path("index.gtmpl")
    Template index;

    @View
    public Response.Content index() {
        return index.ok();
    }

    @Resource
    public Response.Content onlineFriends() {
        String userId = ConversationState.getCurrent().getIdentity().getUserId();

        JSONArray json = getOnlineFriends(userId);

        return Response.ok(json.toString()).withMimeType("application/json; charset=UTF-8").withHeader("Cache-Control", "no-cache");
    }

    private JSONArray getOnlineFriends(String userId) {
        JSONArray jsonArray = new JSONArray();
        if (userId == null) return jsonArray;

        try {
            ExoContainer container = ExoContainerContext.getCurrentContainer();
            IdentityManager identityManager = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
            UserStateService userStateService = (UserStateService) container.getComponentInstanceOfType(UserStateService.class);
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
                json.put("avatar", userProfile.getAvatarImageSource() != null ? userProfile.getAvatarImageSource() : LinkProvider.PROFILE_DEFAULT_AVATAR_URL);
                jsonArray.put(json);
            }
            return jsonArray;

        } catch (Exception e) {
            return jsonArray;
        }
    }
}
