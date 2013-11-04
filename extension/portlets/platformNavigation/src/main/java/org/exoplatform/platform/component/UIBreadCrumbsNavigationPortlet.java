package org.exoplatform.platform.component;

import org.exoplatform.platform.common.navigation.NavigationUtils;
import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.platform.navigation.component.breadcrumb.UserNavigationHandlerService;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.webui.UIAvatarUploader;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 26/11/12
 */

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIBreadCrumbsNavigationPortlet/UIBreadCrumbsNavigationPortlet.gtmpl",
        events = {
                @EventConfig(listeners = UIBreadCrumbsNavigationPortlet.ChangePictureActionListener.class)
        }

)
public class UIBreadCrumbsNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UIBreadCrumbsNavigationPortlet.class);
    private final String POPUP_AVATAR_UPLOADER = "UIBreadCrumbPopupAvatarUploader";
    private SpaceService spaceService = null;
    private OrganizationService orgService = null;
    private UserNavigationHandlerService userService = null;
    private MenuConfiguratorService menuConfiguratorService;
    private UserPortalConfigService portalConfigService;
    private List<UserNode> setupMenuUserNodes = null;
    private List<PageNode> setupMenuPageNodes = null;
    private Map<String, Boolean> pagePermissionsMap = new HashMap<String, Boolean>();
    private static final String USER = "/user/";
    private static final String WIKI_HOME = "/WikiHome";
    private static final String WIKI_REF = "wiki";

    public UIBreadCrumbsNavigationPortlet() throws Exception {
        spaceService = getApplicationComponent(SpaceService.class);
        orgService = getApplicationComponent(OrganizationService.class);
        userService = getApplicationComponent(UserNavigationHandlerService.class);
        UIPopupWindow uiPopup = createUIComponent(UIPopupWindow.class, null, POPUP_AVATAR_UPLOADER);
        uiPopup.setWindowSize(510, 0);
        addChild(uiPopup);
    }


    public boolean isUseAjax() throws Exception {
        WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
        PortletRequest prequest = context.getRequest();
        PortletPreferences prefers = prequest.getPreferences();
        return Boolean.valueOf(prefers.getValue("useAJAX", "true"));
    }

    public UserNavigation getSelectedNode() throws Exception {
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        UserPortal userPortal = Util.getPortalRequestContext().getUserPortalConfig().getUserPortal();
        UserNavigation nav = userPortal.getNavigation(node.getNavigation().getKey());
        return nav;
    }

    public String getSpacename(String SpaceUrl) throws Exception {
        Space space = spaceService.getSpaceByUrl(SpaceUrl);
        if (space != null) {
            String spaceNAme = space.getDisplayName();
            return spaceNAme;

        } else return "";
    }

    public String getSpaceUrl() throws Exception {
        String spaceUrl = null;
        UserNavigation nav = getSelectedNode();
        String ownerId = nav.getKey().getName();
        if (ownerId.contains("/spaces/")) {
         Space space=   spaceService.getSpaceByGroupId(ownerId)  ;
            if(space==null){
                return spaceUrl ;
            }
            return space.getUrl();
        }
        return spaceUrl;
    }

    public String getImageSource(String url) throws Exception {
        Space space = spaceService.getSpaceByUrl(url);
        
        if (space == null) {
          return LinkProvider.SPACE_DEFAULT_AVATAR_URL;
        }
        
        String spaceAvatar = space.getAvatarUrl();
        return  (spaceAvatar == null || spaceAvatar.isEmpty()) ?  LinkProvider.SPACE_DEFAULT_AVATAR_URL : spaceAvatar;
    }

    public String getUserFullName(String userName) throws Exception {

        // --- Full Name to be loaded fromConversationState
        String fullName = "";

        // --- Load the Full name from the ConversationState else use Social API to load firstName and lastName
        fullName = NavigationUtils.getUserFromConversationState(true);

        //--- return the FullName from ConversationState if exist
        if (NavigationUtils.present(fullName)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("IdentityManager : loading ["+userName+"] from Conversation State");
            }
            return fullName;
        }
        //---Load User from Social Identity
        Identity viewerIdentity = Utils.getViewerIdentity(true);
        if (viewerIdentity != null) {
            fullName = viewerIdentity.getProfile().getFullName();
            if (LOG.isDebugEnabled()) {
                LOG.debug("IdentityManager : loading ["+userName+"] from social Profile");
            }
        }
        return fullName;
    }


    public String getAvatarURL(String username) {
        Identity identity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                username, true);
        String ownerAvatar = identity.getProfile().getAvatarUrl();
        if (ownerAvatar == null || ownerAvatar.isEmpty()) {
            ownerAvatar = LinkProvider.PROFILE_DEFAULT_AVATAR_URL;
        }
        return ownerAvatar;
    }

    public String getProfileURL(String username) {
        Identity identity = Utils.getIdentityManager().getOrCreateIdentity(OrganizationIdentityProvider.NAME,
                username, true);
        String ownerProfile = identity.getProfile().getUrl();

        return ownerProfile;
    }

    public boolean isUserUrl() throws Exception {
        String urlPath = Util.getPortalRequestContext().getRequest().getRequestURI();
        List<String> uris = userService.loadUserNavigation();
        UserNavigation nav = getSelectedNode();
        SiteType navType = nav.getKey().getType();
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        String uri = node.getURI();
        if (uris.contains(uri) || navType.equals(SiteType.USER) || Util.getPortalRequestContext().getRequest().getRequestURL().toString().contains(getWikiURL())) {
            return true;
        } else return false;

    }

    public String getWikiURL() {
        return NavigationURLUtils.getURLInCurrentPortal(WIKI_REF) + USER + getRemoteUserId() + WIKI_HOME;
    }

    public static String getRemoteUserId() {
        String currentUserName =  RequestContext.getCurrentInstance().getRemoteUser();
        if (currentUserName == null || currentUserName.equals("")) {
            LOG.warn("Can't check the remote user id associated to the current http request");
            return null;
        }
        return currentUserName;
    }

    public boolean isGroupUrl() throws Exception {
        menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
        portalConfigService = getApplicationComponent(UserPortalConfigService.class);
        setupMenuUserNodes = menuConfiguratorService.getSetupMenuItems(getUserPortal());
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        boolean isAdminUrl = false;
        for (UserNode menuNode : setupMenuUserNodes) {
            if (menuNode.getURI().equals(node.getURI()) && menuNode.getPageRef().equals(node.getPageRef())) {
                isAdminUrl = true;
                break;
            }
        }

        if(isAdminUrl || node.getURI().equals("search"))  {
            return true;
        }else {
        return false;
        }
    }

    public static String getEncodedResolvedLabel() throws Exception {
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        return node.getResolvedLabel();
    }

    public static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    public boolean isSpaceUrl() throws Exception {
        String urlPath = Util.getPortalRequestContext().getRequest().getRequestURI();
        UserNavigation nav = getSelectedNode();
        SiteType navType = nav.getKey().getType();
        if (urlPath.contains(":spaces:") && navType.equals(SiteType.GROUP)) {
            return true;
        } else return false;

    }

    public boolean isOwner() {
        String currentUserName = org.exoplatform.platform.navigation.component.utils.NavigationUtils.getCurrentUser();
        if (currentUserName != null && !currentUserName.equals("")) {
            return currentUserName.equals(Utils.getViewerRemoteId());
        } else
            return true;
    }

    public static class ChangePictureActionListener extends EventListener<UIBreadCrumbsNavigationPortlet> {

        @Override
        public void execute(Event<UIBreadCrumbsNavigationPortlet> event) throws Exception {
            UIBreadCrumbsNavigationPortlet uiProfileNavigation = event.getSource();
            UIPopupWindow uiPopup = uiProfileNavigation.getChild(UIPopupWindow.class);
            UIAvatarUploader uiAvatarUploader = uiProfileNavigation.createUIComponent(UIAvatarUploader.class, null, null);
            uiPopup.setUIComponent(uiAvatarUploader);
            uiPopup.setShow(true);
        }
    }
}