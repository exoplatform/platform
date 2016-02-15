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

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UICompanyNavigationPortlet/UICompanyNavigationPortlet.gtmpl")
public class UICompanyNavigationPortlet extends UIPortletApplication {
    private static final Log LOG = ExoLogger.getExoLogger(UICompanyNavigationPortlet.class);

    private UserACL userACL = null;
    private UserNodeFilterConfig userFilterConfig;

    public UICompanyNavigationPortlet() throws Exception {
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        userFilterConfig = builder.build();
        userACL = getApplicationComponent(UserACL.class);

    }

    public UserNavigation getCurrentPortalNavigation() throws Exception {
        return getNavigation(SiteKey.portal(getCurrentPortal()));
    }

    private UserNavigation getNavigation(SiteKey userKey) {
        UserPortal userPortal = getUserPortal();
        return userPortal.getNavigation(userKey);
    }

    private UserPortal getUserPortal() {
        PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
        return portalRequestContext.getUserPortal();
    }

    public String getCurrentPortal() {
        return Util.getPortalRequestContext().getPortalOwner();
    }

    public Collection<UserNode> getUserNodes(UserNavigation nav) {
        UserPortal userPortall = getUserPortal();
        if (nav != null) {
            try {
                UserNode rootNode = userPortall.getNode(nav, Scope.ALL, userFilterConfig, null);
                return rootNode.getChildren();
            } catch (Exception exp) {
                LOG.warn(nav.getKey().getName() + " has been deleted");
            }
        }
        return Collections.emptyList();
    }

    public UserNode getSelectedPageNode() throws Exception {
        return Util.getUIPortal().getSelectedUserNode();
    }

    public Boolean isSelectedPageNode(UserNode node) throws Exception {

        UserNode selectedNode = Util.getUIPortal().getSelectedUserNode();
        if (selectedNode != null) {
            if (node.getURI().equals(selectedNode.getURI())) {
                return true;
            }
            List<String> uris = new ArrayList<String>();
            for (UserNode child : node.getChildren()) {
                uris.add(child.getURI());
            }
            if (uris != null && !uris.isEmpty()) {
                if (uris.contains(selectedNode.getURI())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

}
