package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIMyGroupsPlatformToolBarPortlet/UIMyGroupsPlatformToolBarPortlet.gtmpl")
public class UIMyGroupsPlatformToolBarPortlet extends UIPortletApplication {

  private OrganizationService organizationService = null;
  private MenuConfiguratorService menuConfiguratorService;
  private String userId = null;
  private boolean groupNavigationPermitted = false;
  private UserNodeFilterConfig myGroupsFilterConfig;
  private List<String> setupMenuPageReferences = null;
  private List<UserNavigation> navigationsToDisplay = new ArrayList<UserNavigation>();
  // first level of valid user nodes <SiteName, list of valid nodes>
  private Map<String, Collection<UserNode>> nodesToDisplay = new HashMap<String, Collection<UserNode>>();

  // valid children nodes of a selected user node <user node id, list of
  // valid children nodes>
  private Map<String, Collection<UserNode>> cachedValidChildrenNodesToDisplay = new HashMap<String, Collection<UserNode>>();

  public UIMyGroupsPlatformToolBarPortlet() throws Exception {
    organizationService = getApplicationComponent(OrganizationService.class);
    menuConfiguratorService = getApplicationComponent(MenuConfiguratorService.class);
    UserACL userACL = getApplicationComponent(UserACL.class);
    // groupNavigationPermitted is set to true if the user is the super
    // user or have the administration rights
    if (getUserId().equals(userACL.getSuperUser())) {
      groupNavigationPermitted = true;
    } else {
      Collection<?> memberships = organizationService.getMembershipHandler().findMembershipsByUser(getUserId());
      for (Object object : memberships) {
        Membership membership = (Membership) object;
        // groupNavigationPermitted is set to true if the user is a manager
        // of group != spaces
        if (membership.getMembershipType().equals(userACL.getAdminMSType()) && membership.getGroupId().indexOf("spaces") < 0) {
          groupNavigationPermitted = true;
          break;
        }
      }
    }
    setupMenuPageReferences = menuConfiguratorService.getSetupMenuPageReferences();
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

  private void readNavigationsAndCache() {
    UserPortal userPortal = getUserPortal();
    List<UserNavigation> allNavigations = userPortal.getNavigations();
    // Compute the list of UserNavigations that have navigation nodes not
    // set in 'SetupMenu'
    navigationsToDisplay.clear();
    nodesToDisplay.clear();
    cachedValidChildrenNodesToDisplay.clear();
    for (UserNavigation navigation : allNavigations) {
      UserNode rootNode = userPortal.getNode(navigation, Scope.ALL, myGroupsFilterConfig, null);
      if ((navigation.getKey().getTypeName().equals(PortalConfig.GROUP_TYPE))
          && (navigation.getKey().getName().indexOf("spaces") < 0)) {
        Collection<UserNode> children = getNodesNotInSetupMenu(rootNode.getChildren());
        if (children == null || children.isEmpty()) {
          continue;
        }
        navigationsToDisplay.add(navigation);
        nodesToDisplay.put(navigation.getKey().getName(), children);
      }
    }
  }

  /**
   * @return group navigation that does not include any space navigation
   */
  public List<UserNavigation> getGroupNavigations() {
    return navigationsToDisplay;
  }

  public UserNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  public Collection<UserNode> getValidUserNodes(UserNavigation nav) {
    return nodesToDisplay.get(nav.getKey().getName());
  }

  public Collection<UserNode> getValidChildren(UserNode node) {
    return cachedValidChildrenNodesToDisplay.get(node.getId());
  }

  public Collection<UserNode> getNodesNotInSetupMenu(Collection<UserNode> userNodes) {
    if (userNodes == null || userNodes.isEmpty()) {
      return null;
    }
    Collection<UserNode> validNodes = new ArrayList<UserNode>();
    for (UserNode userNode : userNodes) {
      // Compute valid child nodes
      // Attention: this instruction have to be here in order to compute
      // the valid child nodes of all user nodes recursively and cache the
      // result
      Collection<UserNode> validChidNodes = getNodesNotInSetupMenu(userNode.getChildren());
      cachedValidChildrenNodesToDisplay.put(userNode.getId(), validChidNodes);

      // Test if this node have a "page reference" not set in 'Setup Menu'
      if (userNode.getPageRef() != null && !userNode.getPageRef().isEmpty() && !isUserNodeInSetupMenu(userNode)) {
        validNodes.add(userNode);
        continue;
      }
      // Test if one node's child have a "page reference" not set in 'Setup
      // Menu'
      if (validChidNodes != null && !validChidNodes.isEmpty()) {
        validNodes.add(userNode);
      }
    }
    return validNodes;
  }

  public boolean isUserNodeInSetupMenu(UserNode userNode) {
    String pageReference = userNode.getPageRef();
    if (pageReference != null && !pageReference.isEmpty()) {
      return setupMenuPageReferences.contains(pageReference);
    }
    return false;
  }

  public UserNode getSelectedNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  private String getUserId() {
    if (userId == null) {
      userId = Util.getPortalRequestContext().getRemoteUser();
    }
    return userId;
  }

  public static UserPortal getUserPortal() {
    UserPortalConfig portalConfig = Util.getPortalRequestContext().getUserPortalConfig();
    return portalConfig.getUserPortal();
  }

  public boolean hasPermission() throws Exception {
    return groupNavigationPermitted;
  }

}
