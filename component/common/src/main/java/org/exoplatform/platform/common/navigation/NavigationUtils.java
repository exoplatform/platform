/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.navigation;

import org.exoplatform.portal.config.model.*;
import org.exoplatform.portal.mop.Described;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.description.DescriptionService;
import org.exoplatform.portal.mop.navigation.*;
import java.util.*;

/**
 * @author <a href="mailto:kmenzli@exoplatform.com">Khemais MENZLI</a>
 * @version $Revision$
 * Date: 06/03/13
 */

public class NavigationUtils {
    private NavigationUtils(){}

    public static PageNavigation loadPageNavigation(String userId, NavigationService navigationService, DescriptionService descriptionService)
    {
        NavigationContext navigation = navigationService.loadNavigation(SiteKey.user(userId));
        if (navigation == null) return null;

        NodeContext<NodeContext<?>> node = loadNode(navigationService, navigation, null);
        if (node == null) return null;


        return createPageNavigation(descriptionService, navigation, node);

    }

    public static NodeContext<NodeContext<?>> loadNode(NavigationService navigationService, NavigationContext navigation,
                                                       String navUri) {
        if (navigation == null)
            return null;

        if (navUri != null) {
            String[] path = trim(navUri.split("/"));
            NodeContext<NodeContext<?>> node = navigationService.loadNode(NodeModel.SELF_MODEL, navigation,
                    GenericScope.branchShape(path, Scope.ALL), null);
            for (String name : path) {
                node = node.get(name);
                if (node == null)
                    break;
            }

            return node;
        } else {
            return navigationService.loadNode(NodeModel.SELF_MODEL, navigation, Scope.ALL, null);
        }
    }

    public static PageNavigation createPageNavigation(DescriptionService service, NavigationContext navigation, NodeContext<NodeContext<?>> node) {
        PageNavigation pageNavigation = new PageNavigation();
        pageNavigation.setPriority(navigation.getState().getPriority());
        pageNavigation.setOwnerType(navigation.getKey().getTypeName());
        pageNavigation.setOwnerId(navigation.getKey().getName());

        ArrayList<PageNode> children = new ArrayList<PageNode>(node.getNodeCount());
        for (NodeContext<?> child : node.getNodes()) {
            @SuppressWarnings("unchecked")
            NodeContext<NodeContext<?>> childNode = (NodeContext<NodeContext<?>>) child;
            children.add(createPageNode(service, childNode));
        }

        NavigationFragment fragment = new NavigationFragment();
        fragment.setNodes(children);
        pageNavigation.addFragment(fragment);

        return pageNavigation;
    }

    private static PageNavigation createFragmentedPageNavigation(DescriptionService service, NavigationContext navigation,
                                                                 NodeContext<NodeContext<?>> node) {
        PageNavigation pageNavigation = new PageNavigation();
        pageNavigation.setPriority(navigation.getState().getPriority());
        pageNavigation.setOwnerType(navigation.getKey().getTypeName());
        pageNavigation.setOwnerId(navigation.getKey().getName());

        ArrayList<PageNode> children = new ArrayList<PageNode>(1);
        children.add(createPageNode(service, node));

        NavigationFragment fragment = new NavigationFragment();
        StringBuilder parentUri = new StringBuilder("");
        getPath(node.getParent(), parentUri);
        fragment.setParentURI(parentUri.toString());
        fragment.setNodes(children);

        pageNavigation.addFragment(fragment);

        return pageNavigation;
    }


    private static void getPath(NodeContext<NodeContext<?>> node, StringBuilder parentUri) {
        if (node == null)
            return;
        if (node.getParent() == null)
            return; // since "default" is the root node, we ignore it

        parentUri.insert(0, node.getName()).insert(0, "/");
        getPath(node.getParent(), parentUri);
    }

    private static PageNode createPageNode(DescriptionService service, NodeContext<NodeContext<?>> node)
    {
        PageNode pageNode = new PageNode();
        pageNode.setName(node.getName());

        if (node.getState().getLabel() == null) {
            Map<Locale, Described.State> descriptions = service.getDescriptions(node.getId());
            if (descriptions != null && !descriptions.isEmpty()) {
                I18NString labels = new I18NString();
                for (Map.Entry<Locale, Described.State> entry : descriptions.entrySet()) {
                    labels.add(new LocalizedString(entry.getValue().getName(), entry.getKey()));
                }

                pageNode.setLabels(labels);
            }
        } else {
            pageNode.setLabel(node.getState().getLabel());
        }

        pageNode.setIcon(node.getState().getIcon());
        long startPublicationTime = node.getState().getStartPublicationTime();
        if (startPublicationTime != -1) {
            pageNode.setStartPublicationDate(new Date(startPublicationTime));
        }

        long endPublicationTime = node.getState().getEndPublicationTime();
        if (endPublicationTime != -1) {
            pageNode.setEndPublicationDate(new Date(endPublicationTime));
        }

        pageNode.setVisibility(node.getState().getVisibility());
        pageNode.setPageReference(node.getState().getPageRef() != null ? node.getState().getPageRef().format() : null);

        if (node.getNodes() != null) {
            ArrayList<PageNode> children = new ArrayList<PageNode>(node.getNodeCount());
            for (NodeContext<?> child : node.getNodes()) {
                @SuppressWarnings("unchecked")
                NodeContext<NodeContext<?>> childNode = (NodeContext<NodeContext<?>>) child;
                children.add(createPageNode(service, childNode));
            }

            pageNode.setChildren(children);
        } else {
            pageNode.setChildren(new ArrayList<PageNode>(0));
        }

        return pageNode;
    }

    private static String[] trim(String[] array) {
        List<String> trimmed = new ArrayList<String>(array.length);
        for (String s : array) {
            if (s != null && !"".equals(s)) {
                trimmed.add(s);
            }
        }

        return trimmed.toArray(new String[trimmed.size()]);
    }
}
