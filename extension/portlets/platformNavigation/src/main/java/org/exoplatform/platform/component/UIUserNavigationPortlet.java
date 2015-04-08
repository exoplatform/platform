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
import org.exoplatform.platform.navigation.component.utils.DashboardUtils;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 14/11/12
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class,

        template = "app:/groovy/platformNavigation/portlet/UIUserNavigationPortlet/UIUserNavigationPortlet.gtmpl"
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
    public static String DEFAULT_TAB_NAME = "Tab_Default";
    private static final String USER ="/user/"  ;
    private static final String WIKI_HOME = "/WikiHome";
    private static final String WIKI_REF ="wiki" ;
    private static final String NOTIFICATION_SETTINGS = "NotificationSettingsPortlet";

    public UIUserNavigationPortlet() throws Exception {
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        toolbarFilterConfig = builder.build();
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

    public String getProfileLink() {
        return LinkProvider.getUserProfileUri(getOwnerRemoteId());
    }

}
