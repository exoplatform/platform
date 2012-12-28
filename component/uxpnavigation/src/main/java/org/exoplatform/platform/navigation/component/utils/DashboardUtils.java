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

package org.exoplatform.platform.navigation.component.utils;

import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.application.WebuiRequestContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 22/11/12
 */
public class DashboardUtils {
    //////////////////////////////////////////////////////////
    /**/                                                 /**/
    /**/                 //DASHBOARD//                   /**/
    /**/                                                 /**/
    //////////////////////////////////////////////////////////


    public DashboardUtils() {
    }

    private static UserNodeFilterConfig toolbarFilterConfig;
    private static final Log LOG = ExoLogger.getExoLogger(DashboardUtils.class);

    static {
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        toolbarFilterConfig = builder.build();
    }


    public static String getDashboardURL() throws Exception {
        RequestContext ctx = RequestContext.getCurrentInstance();
        NodeURL dashboardUrl = ctx.createURL(NodeURL.TYPE);
        UserNode dashboardNode = findDashboardNode();
        if (dashboardNode != null) {
            return (dashboardUrl.setNode(dashboardNode).toString());
        } else {
            dashboardUrl.setResource(new NavigationResource(SiteType.USER, getCurrentUser(), null));
            return (dashboardUrl.toString());
        }
    }

    public static UserNavigation getCurrentUserNavigation() throws Exception {
        return getNavigation(SiteKey.user(getCurrentUser()));

    }

    public static String getCurrentUser() {
        WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance();
        return rcontext.getRemoteUser();
    }

    private static UserNavigation getNavigation(SiteKey userKey) {
        UserPortal userPortal = getUserPortal();
        return userPortal.getNavigation(userKey);
    }

    /*private static boolean isWebOSNode(UserNode userNode) throws Exception {
        if (userNode == null) {
            return false;
        }
        String pageRef = userNode.getPageRef();
        if (pageRef == null) {
            return false;
        }
        ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
        DataStorage ds = (DataStorage) exoContainer.getComponentInstanceOfType(DataStorage.class);
        Page page = ds.getPage(pageRef);
        return page == null || UIDesktopPage.DESKTOP_FACTORY_ID.equals(page.getFactoryId());
    }     */

    public static Collection<UserNode> getUserNodes(UserNavigation nav) {
        UserPortal userPortall = getUserPortal();
        if (nav != null) {
            try {
                UserNode rootNode = userPortall.getNode(nav, Scope.CHILDREN, toolbarFilterConfig, null);
                return filterWebOSNode(rootNode.getChildren());
            } catch (Exception exp) {
                LOG.warn(nav.getKey().getName() + " has been deleted");
            }
        }
        return Collections.emptyList();
    }

    private static Collection<UserNode> filterWebOSNode(Collection<UserNode> pageNodes) throws Exception {
        if (pageNodes == null || pageNodes.size() == 0) {
            return pageNodes;
        }
        List<UserNode> tempNodes = new ArrayList<UserNode>(pageNodes);
        UserNode webOSNode = null;
        for (UserNode node : tempNodes) {
           /* if (isWebOSNode(node)) {
                webOSNode = node;
            } */
        }
        if (webOSNode != null) {
            tempNodes.remove(tempNodes);
        }
        return tempNodes;
    }

    public static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    private static UserNode findDashboardNode() throws Exception {
        Collection<UserNode> nodes = getUserNodes(getCurrentUserNavigation());
        if (nodes == null) {
            return null;

        } else {
            for (UserNode node : nodes) {
              /*  if (!isWebOSNode(node)) {
                    return node;
                }  */
            }
            return null;
        }
    }
}
