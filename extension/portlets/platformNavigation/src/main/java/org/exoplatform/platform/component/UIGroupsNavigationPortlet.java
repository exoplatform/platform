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

import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

import java.util.*;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 22/11/12
 */

@ComponentConfig(
        lifecycle =
                UIApplicationLifecycle.class,
        template =
                "app:/groovy/platformNavigation/portlet/UIGroupsNavigationPortlet/UIGroupsNavigationPortlet.gtmpl"
      )

public class UIGroupsNavigationPortlet extends UIPortletApplication {

    private MenuConfiguratorService menuConfiguratorService;
    private  UserNodeFilterConfig myGroupsFilterConfig;
    private  List<String> setupMenuPageReferences = null;
    private   List<UserNavigation> navigationsToDisplay = new ArrayList<UserNavigation>();
    private  Map<String, Collection<UserNode>> nodesToDisplay = new HashMap<String, Collection<UserNode>>();
    private  Map<String, Collection<UserNode>> cachedValidChildrenNodesToDisplay = new HashMap<String, Collection<UserNode>>();

    public static boolean collapse=true;

    public UIGroupsNavigationPortlet() throws Exception {
        menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
        setupMenuPageReferences = menuConfiguratorService.getSetupMenuPageReferences();
        myGroupsFilterConfig = menuConfiguratorService.getMyGroupsFilterConfig();
    }

    @Override
    public void processRender(WebuiRequestContext context) throws Exception {
        readNavigationsAndCache();
        super.processRender(context);
    }
    @Override
    public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
        readNavigationsAndCache();
        super.processRender(app, context);
    }

    private  void readNavigationsAndCache() {
        UserPortal userPortal = getUserPortal();
        List<UserNavigation> allNavigations = userPortal.getNavigations();

        navigationsToDisplay.clear();
        nodesToDisplay.clear();
        cachedValidChildrenNodesToDisplay.clear();
        for (UserNavigation navigation : allNavigations) {
            UserNode rootNode = userPortal.getNode(navigation, Scope.ALL, myGroupsFilterConfig, null);
            if ((navigation.getKey().getTypeName().equals(PortalConfig.GROUP_TYPE))
                    && (navigation.getKey().getName().indexOf("spaces") < 0)) {
                Collection<UserNode> children = getUXPNodesNotInSetupMenu(rootNode.getChildren(), 0);
                if (children == null || children.isEmpty()) {
                    continue;
                }
                navigationsToDisplay.add(navigation);
                nodesToDisplay.put(navigation.getKey().getName(), children);
            }
        }
    }

    public List<UserNavigation> getGroupNavigations() {

        return navigationsToDisplay;
    }

    public Collection<UserNode> getValidUserNodes(UserNavigation nav) {
        return nodesToDisplay.get(nav.getKey().getName());
    }

    public Collection<UserNode> getValidChildren(UserNode node) {
        return cachedValidChildrenNodesToDisplay.get(node.getId());
    }


    public  Collection<UserNode> getUXPNodesNotInSetupMenu(Collection<UserNode> userNodes, int childLevel) {
        childLevel++;
        if (userNodes == null || userNodes.isEmpty() || childLevel > 2) {
            return null;
        }
        Collection<UserNode> validNodes = new ArrayList<UserNode>();
        for (UserNode userNode : userNodes) {
            Collection<UserNode> validChidNodes = getUXPNodesNotInSetupMenu(userNode.getChildren(), childLevel);
            cachedValidChildrenNodesToDisplay.put(userNode.getId(), validChidNodes);

            if (userNode.getPageRef() != null && !isUserNodeInSetupMenu(userNode)) {
                validNodes.add(userNode);
                continue;
            }
            if (validChidNodes != null && !validChidNodes.isEmpty()) {
                validNodes.add(userNode);
            }
        }
        return validNodes;
    }

    public  boolean isUserNodeInSetupMenu(UserNode userNode) {
        String pageReference = userNode.getPageRef().format();
        if (pageReference != null && !pageReference.isEmpty()) {
            return setupMenuPageReferences.contains(pageReference);
        }
        return false;
    }

    public static UserPortal getUserPortal() {
        UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
        return portalConfig.getUserPortal();
    }

    public boolean isSelectedNavigation(String name) throws Exception {
        UIPortal uiPortal = Util.getUIPortal();
        UserNode selectedNode = uiPortal.getSelectedUserNode();
        if (selectedNode.getName().equals(name)) return true;
        else return false;
    }
    public boolean isUnfoldedNavigation(UserNode node) throws Exception {
        UIPortal uiPortal = Util.getUIPortal();
        UserNode selectedNode = uiPortal.getSelectedUserNode();
        if (node.getChild(selectedNode.getName())!=null) return true;
        else return false;
    }
}