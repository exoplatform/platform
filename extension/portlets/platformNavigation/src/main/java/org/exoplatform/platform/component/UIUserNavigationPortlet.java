/**
 * Copyright ( C ) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.component;

import org.apache.commons.lang.ArrayUtils;
import org.exoplatform.commons.notification.NotificationUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.platform.navigation.component.breadcrumb.UserNavigationHandlerService;
import org.exoplatform.platform.navigation.component.utils.DashboardUtils;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.user.UserStateService;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.image.ImageUtils;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.webui.UIAvatarUploader;
import org.exoplatform.social.webui.UIBannerUploader;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.social.webui.composer.PopupContainer;
import org.exoplatform.social.webui.space.UISpaceMenu;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.exception.MessageException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class,

        template = "app:/groovy/platformNavigation/portlet/UIUserNavigationPortlet/UIUserNavigationPortlet.gtmpl",
        events = {
                @EventConfig(listeners = UIUserNavigationPortlet.ChangeBannerActionListener.class),
                @EventConfig(listeners = UIUserNavigationPortlet.DeleteBannerActionListener.class),
                @EventConfig(listeners = UIUserNavigationPortlet.ChangeAvatarActionListener.class)
        }
)
public class UIUserNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UIUserNavigationPortlet.class);
    public static final String ACTIVITIES_URI= "activities";
    public static final String PROFILE_URI= "profile";
    public static final String CONNEXIONS_URI= "connections";
    public static final String WIKI_URI= "wiki";
    public static final String DASHBOARD_URI= "dashboard";
    private UserNodeFilterConfig toolbarFilterConfig;
    private static final String POPUP_AVATAR_UPLOADER = "UIAvatarUploaderPopup";
    private final static String POPUP_BANNER_UPLOADER = "UIPopupBannerUploader";
    public static String DEFAULT_TAB_NAME = "Tab_Default";
    private static final String USER ="/user/"  ;
    private static final String WIKI_HOME = "/WikiHome";
    private static final String WIKI_REF ="wiki" ;
    private static final String NOTIF_REF ="notifications" ;
    private static final String NOTIFICATION_SETTINGS = "NotificationSettingsPortlet";
    private static final String EDIT_PROFILE_NODE = "edit-profile";

    public static final String OFFLINE_STATUS        = "offline";
    public static final String OFFLINE_TITLE         = "UIUserNavigationPortlet.label.offline";
    public static final String USER_STATUS_TITLE     = "UIUserNavigationPortlet.label.";

    private UserNavigationHandlerService userService           = null;

    public UIUserNavigationPortlet() throws Exception {
        userService = getApplicationComponent(UserNavigationHandlerService.class);

        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        toolbarFilterConfig = builder.build();

        PopupContainer popupContainer = createUIComponent(PopupContainer.class, null, null);
        addChild(popupContainer);
    }

    public boolean isSelectedUserNavigation(String nav) throws Exception {
        UIPortal uiPortal = Util.getUIPortal();
        UserNode selectedNode = uiPortal.getSelectedUserNode();
        if (selectedNode.getURI().contains(nav)) return true;
        if (NOTIFICATION_SETTINGS.equals(nav) && "notifications".equals(selectedNode.getURI())) return true;
        //case dashbord
        String requestUrl = Util.getPortalRequestContext().getRequest().getRequestURL().toString();
        if(DASHBOARD_URI.equals(nav) && requestUrl.contains(DashboardUtils.getDashboardURL())) return true;
        //
        return false;
    }

    public boolean isProfileOwner() {
        return Utils.getViewerRemoteId().equals(getOwnerRemoteId());
    }

    public static String getOwnerRemoteId() {
        String currentUserName = org.exoplatform.platform.navigation.component.utils.NavigationUtils.getCurrentUser();
        if (currentUserName == null || currentUserName.equals("")) {
            return Utils.getViewerRemoteId();
        }
        return currentUserName;
    }

    public Profile getOwnerProfile() {
        return Utils.getOwnerIdentity(true).getProfile();
    }

    public String getAvatarURL(Profile profile) {
        String ownerAvatar = profile.getAvatarUrl();
        if (ownerAvatar == null || ownerAvatar.isEmpty()) {
            ownerAvatar = LinkProvider.PROFILE_DEFAULT_AVATAR_URL;
        }
        return ownerAvatar;
    }

    protected boolean isUserUrl() throws Exception {
        List<String> uris = userService.loadUserNavigation();
        UserNavigation nav = getSelectedNode();
        SiteType navType = nav.getKey().getType();
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        String uri = node.getURI();
        String currentURL = Util.getPortalRequestContext().getRequest().getRequestURL().toString();
        if (uris.contains(uri) || navType.equals(SiteType.USER) || currentURL.endsWith(getNotificationsURL()) ||
                currentURL.contains(getWikiURL())) {
            return true;
        } else {
            return false;
        }
    }

    protected UserNavigation getSelectedNode() throws Exception {
        UserNode node = Util.getUIPortal().getSelectedUserNode();
        UserNavigation nav = getUserPortal().getNavigation(node.getNavigation().getKey());
        return nav;
    }

    private static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    protected boolean isEditProfilePage() throws Exception {
        String uri = Util.getUIPortal().getSelectedUserNode().getURI();
        if (uri.endsWith(EDIT_PROFILE_NODE)) {
            return true;
        }

        return false;
    }

    //////////////////////////////////////////////////////////
    /**/                                                  /**/
    /**/         //utils METHOD//                         /**/
    /**/                                                  /**/
    //////////////////////////////////////////////////////////

    public String[] getUserNodesAsList() {
        String[] userNodeList=(String[])ArrayUtils.add(null, PROFILE_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, ACTIVITIES_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, CONNEXIONS_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, WIKI_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, DASHBOARD_URI);
        if (CommonsUtils.isFeatureActive(NotificationUtils.FEATURE_NAME)) {
          userNodeList=(String[])ArrayUtils.add(userNodeList, NOTIFICATION_SETTINGS);
        }
        return userNodeList;
    }

    public String[] getURLAsList() throws Exception {
        String[] urlList=(String[])ArrayUtils.add(null, getProfileLink());
        urlList=(String[])ArrayUtils.add(urlList, getactivitesURL());
        urlList=(String[])ArrayUtils.add(urlList, getrelationURL());
        urlList=(String[])ArrayUtils.add(urlList, getWikiURL());
        urlList=(String[])ArrayUtils.add(urlList, DashboardUtils.getDashboardURL());
        if (CommonsUtils.isFeatureActive(NotificationUtils.FEATURE_NAME)) {
          urlList=(String[])ArrayUtils.add(urlList, getNotificationsURL());
        }
        return urlList;
    }
    
    //////////////////////////////////////////////////////////
    /**/                                                  /**/
    /**/         //GET URL METHOD//                       /**/
    /**/                                                  /**/
    //////////////////////////////////////////////////////////

    public String getNotificationsURL() {
      return LinkProvider.getUserNotificationSettingUri(getOwnerRemoteId());
    }

    public String getactivitesURL() {
        return LinkProvider.getUserActivityUri(getOwnerRemoteId());
    }

    public String getrelationURL() {
        return LinkProvider.getUserConnectionsYoursUri(getOwnerRemoteId());
    }

    public String getWikiURL() {
        return NavigationURLUtils.getURLInCurrentPortal(WIKI_REF)+USER +getOwnerRemoteId()+WIKI_HOME;
    }

    protected StatusInfo getStatusInfo() {
        Profile currentProfile = getOwnerProfile();
        StatusInfo si = new StatusInfo();
        ResourceBundle rb = PortalRequestContext.getCurrentInstance().getApplicationResourceBundle();
        UserStateService stateService = getApplicationComponent(UserStateService.class);
        boolean isOnline = stateService.isOnline(currentProfile.getIdentity().getRemoteId());
        if (isOnline) {
            String status = stateService.getUserState(currentProfile.getIdentity().getRemoteId()).getStatus();
            si.setCssName(StatusIconCss.getIconCss(status));
            si.setTitle(rb.getString(USER_STATUS_TITLE + status));
        } else {
            si.setCssName(StatusIconCss.getIconCss(OFFLINE_STATUS));
            si.setTitle(rb.getString(OFFLINE_TITLE));
        }

        return si;
    }

    enum StatusIconCss {
        DEFAULT("", ""),
        ONLINE("online", "uiIconUserOnline"),
        OFFLINE("offline", "uiIconUserOffline"),
        AVAILABLE("available", "uiIconUserAvailable"),
        INVISIBLE("invisible", "uiIconUserInvisible"),
        AWAY("away", "uiIconUserAway"),
        DONOTDISTURB("donotdisturb", "uiIconUserDonotdisturb");

        private final String key;
        private final String iconCss;

        StatusIconCss(String key, String iconCss) {
            this.key = key;
            this.iconCss = iconCss;
        }
        String getKey() {
            return this.key;
        }
        public String getIconCss() {
            return iconCss;
        }
        public static String getIconCss(String key) {
            for (StatusIconCss iconClass : StatusIconCss.values()) {
                if (iconClass.getKey().equals(key)) {
                    return iconClass.getIconCss();
                }
            }
            return DEFAULT.getIconCss();
        }
    }

    class StatusInfo {
        private String title;
        private String cssName;
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getCssName() {
            return cssName;
        }
        public void setCssName(String cssName) {
            this.cssName = cssName;
        }
    }

    public String getProfileLink() {
        return LinkProvider.getUserProfileUri(getOwnerRemoteId());
    }


    public static class ChangeAvatarActionListener extends EventListener<UIUserNavigationPortlet> {

        @Override
        public void execute(Event<UIUserNavigationPortlet> event) throws Exception {
            UIUserNavigationPortlet portlet = event.getSource();
            PopupContainer popupContainer = portlet.getChild(PopupContainer.class);
            popupContainer.activate(UIAvatarUploader.class, 500, POPUP_AVATAR_UPLOADER);
            event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer);
        }
    }

    public static class DeleteBannerActionListener extends EventListener<UIUserNavigationPortlet> {

        @Override
        public void execute(Event<UIUserNavigationPortlet> event) throws Exception {
            UIUserNavigationPortlet portlet = event.getSource();
            portlet.removeProfileBanner();

            event.getRequestContext().addUIComponentToUpdateByAjax(portlet);
        }
    }

    private void removeProfileBanner() throws MessageException {
        Profile p = Utils.getOwnerIdentity().getProfile();
        p.removeProperty(Profile.BANNER);
        p.setBannerUrl(null);
        p.setListUpdateTypes(Arrays.asList(Profile.UpdateType.BANNER));

        Utils.getIdentityManager().updateProfile(p);
    }

    public static class ChangeBannerActionListener extends EventListener<UIUserNavigationPortlet> {

        @Override
        public void execute(Event<UIUserNavigationPortlet> event) throws Exception {
            UIUserNavigationPortlet portlet = event.getSource();
            PopupContainer popupContainer = portlet.getChild(PopupContainer.class);
            popupContainer.activate(UIBannerUploader.class, 500, POPUP_BANNER_UPLOADER);
            event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer);
        }
    }
}
