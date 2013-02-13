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
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.navigation.component.utils.DashboardUtils;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.webui.UISocialGroupSelector;
import org.exoplatform.social.webui.URLUtils;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.web.controller.QualifiedName;
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

    private static Log LOG = ExoLogger.getLogger(UIUserNavigationPortlet.class);

    public UIUserNavigationPortlet() throws Exception {
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        toolbarFilterConfig = builder.build();
    }

    public boolean isSelectedUserNavigation(String nav) throws Exception {
        UIPortal uiPortal = Util.getUIPortal();
        UserNode selectedNode = uiPortal.getSelectedUserNode();
        if (selectedNode.getURI().contains(nav)) return true;
        else if(Util.getPortalRequestContext().getRequest().getRequestURL().toString().contains(nav))   return true;
        else return false;
    }

    public boolean isProfileOwner() {
        return Utils.getViewerRemoteId().equals(getOwnerRemoteId());
    }

    public static String getOwnerRemoteId() {
        String currentUserName = getCurrentUser();
        if (currentUserName == null || currentUserName.equals("")) {
            return Utils.getViewerRemoteId();
        }
        return currentUserName;
    }

    public static String getCurrentUser() {
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        IdentityManager idm = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
        PortalRequestContext request = Util.getPortalRequestContext() ;
        String currentPath = request.getControllerContext().getParameter(QualifiedName.parse("gtn:path"));
        String []splitCurrentUser = currentPath.split("/");
        String currentUserName = currentPath.split("/")[splitCurrentUser.length - 1];
        try {
            if ((currentUserName != null)&& (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null))  return currentUserName;
            else if (((currentUserName = currentPath.split("/")[splitCurrentUser.length-2]) != null)&&
                    (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null)) {
                        return currentUserName;
            }
        } catch (Exception e) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Could not found Identity of user " + currentUserName);
            }
            return null;
        }
        return null;
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
        return userNodeList;
    }

    public String[] getURLAsList() throws Exception {
        String[] urlList=(String[])ArrayUtils.add(null, getProfileLink());
        urlList=(String[])ArrayUtils.add(urlList, getactivitesURL());
        urlList=(String[])ArrayUtils.add(urlList, getrelationURL());
        urlList=(String[])ArrayUtils.add(urlList, getWikiURL());
        urlList=(String[])ArrayUtils.add(urlList, DashboardUtils.getDashboardURL());
        return urlList;
    }

    //////////////////////////////////////////////////////////
    /**/                                                  /**/
    /**/         //GET URL METHOD//                       /**/
    /**/                                                  /**/
    //////////////////////////////////////////////////////////


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
