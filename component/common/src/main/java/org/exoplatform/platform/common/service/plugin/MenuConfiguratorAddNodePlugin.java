/**
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.platform.common.service.plugin;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.portal.config.model.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="hzekri@exoplatform.com">hzekri</a>
 * @date 18/07/13
 */
public class MenuConfiguratorAddNodePlugin extends BaseComponentPlugin {
    private ConfigurationManager configurationManager;
    private MenuConfiguratorService menuConfiguratorService;
    private String navPath;
    private PageNode targetNav;
    private String isChild;
    private static final String EXTENDED_SETUP_NAVIGATION_FILE = "extended.setup.navigation.file";
    private static final String TARGET_NODE_CONFIG = "target.node.config";
    private static final String IS_CHILD = "isChild";

    private static final Log LOG = ExoLogger.getLogger(MenuConfiguratorAddNodePlugin.class);

    public MenuConfiguratorAddNodePlugin(InitParams initParams, ConfigurationManager configurationManager, MenuConfiguratorService menuConfiguratorService) {
        this.configurationManager = configurationManager;
        this.menuConfiguratorService = menuConfiguratorService;
        if (initParams.containsKey(EXTENDED_SETUP_NAVIGATION_FILE)) {
            navPath = initParams.getValueParam(EXTENDED_SETUP_NAVIGATION_FILE).getValue();
        }
        if (initParams.containsKey(TARGET_NODE_CONFIG)) {
            targetNav = (PageNode) initParams.getObjectParam(TARGET_NODE_CONFIG).getObject();
        }
        if (initParams.containsKey(IS_CHILD)) {
            isChild = initParams.getValueParam(IS_CHILD).getValue();
        }
    }


    public void execute() {
        NavigationFragment extendedFragment = null;
        List<PageNode> setupPageNodes = menuConfiguratorService.getSetupMenuOriginalPageNodes();
        if (isChild == null || isChild.isEmpty()) {
            isChild = "false";
            LOG.info("isChild param is not set, default value will be used");
        }
        if (navPath != null && !navPath.isEmpty()) {
            try {
                UnmarshalledObject<PageNavigation> extendedObj = ModelUnmarshaller.unmarshall(PageNavigation.class,
                        configurationManager.getInputStream(navPath));
                PageNavigation extendedPageNav = extendedObj.getObject();
                extendedFragment = extendedPageNav.getFragment();
                if (targetNav == null) {
                    for (PageNode pageNode1 : extendedFragment.getNodes()) {
                        setupPageNodes.add(pageNode1);
                    }
                } else {
                    if (!(targetNav.getName() == null || targetNav.getPageReference() == null
                            || targetNav.getName().isEmpty() || targetNav.getPageReference().isEmpty())) {
                        boolean addedNav = insertExtendedNodes(setupPageNodes, targetNav, isChild, extendedFragment);
                        if (addedNav == false) {
                            LOG.warn("Navigation with path " + navPath + " not added : target node not found");
                        }
                    } else {
                        LOG.warn("Navigation with path " + navPath + " not added : Both name and pageReference should be specified for the target node" );
                    }
                }
            } catch (Exception E) {
                LOG.error("Can not load or read the file with path " + navPath + " Please check the path or the file structure ", E);
            }
        } else {
            LOG.warn("Path for extended setup navigation file not mentioned");
        }
    }

    private boolean insertExtendedNodes(List<PageNode> setupPageNodes, PageNode targetNavigation, String isChild, NavigationFragment frag) {
        boolean isFound = false;
        for (PageNode pageNode : setupPageNodes) {

            if (pageNode.getName().equals(targetNavigation.getName())
                    && pageNode.getPageReference().equals(targetNavigation.getPageReference())) {

                if (isChild.equals("true")) {
                    List<PageNode> L = pageNode.getChildren();
                    if (L == null) {
                        L = new ArrayList();
                    }
                    for (PageNode pageNode1 : frag.getNodes()) {
                        L.add(pageNode1);
                    }
                    pageNode.setChildren((ArrayList<PageNode>) L);

                } else {
                    if (!(isChild.equals("false"))) {
                        LOG.warn("isChild param should be set to true or false");
                    }
                    int i = setupPageNodes.indexOf(pageNode);

                    for (PageNode pageNode1 : frag.getNodes()) {
                        i++;
                        setupPageNodes.add(i, pageNode1);
                    }
                }
                isFound = true;
                break;
            }
            List<PageNode> L = pageNode.getChildren();
            if (L != null) {
                isFound = insertExtendedNodes(L, targetNavigation, isChild, frag);
                if (isFound) {
                    break;
                }
            }
        }
        return isFound;
    }

}
