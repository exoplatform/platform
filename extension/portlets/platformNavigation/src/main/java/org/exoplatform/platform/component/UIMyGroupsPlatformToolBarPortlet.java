package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
  private List<UserNode> setupMenuUserNodes = null;

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
    UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
    // builder.withAuthorizationCheck().withVisibility(Visibility.DISPLAYED,
    // Visibility.HIDDEN).withTemporalCheck();
    myGroupsFilterConfig = builder.build();
    setupMenuUserNodes = menuConfiguratorService.getSetupMenuItems(getUserPortal());
  }

  // return group navigation that does not include any space navigation
  public List<UserNavigation> getGroupNavigations() throws Exception {
    UserPortal userPortal = getUserPortal();
    List<UserNavigation> allNavigations = userPortal.getNavigations();
    List<UserNavigation> computedNavigations = new ArrayList<UserNavigation>();
    for (UserNavigation navigation : allNavigations) {
      if ((navigation.getKey().getTypeName().equals(PortalConfig.GROUP_TYPE))
          && (navigation.getKey().getName().indexOf("spaces") < 0)) {
        computedNavigations.add(navigation);
      }
    }
    return computedNavigations;
  }

  public UserNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  public Collection<UserNode> getUserNodes(UserNavigation nav) {
    UserPortal userPortall = getUserPortal();
    if (nav != null) {
      try {
        UserNode rootNode = userPortall.getNode(nav, Scope.ALL, myGroupsFilterConfig, null);
        return rootNode.getChildren();
      } catch (Exception exp) {
        log.warn(nav.getKey().getName() + " has been deleted");
      }
    }
    return Collections.emptyList();
  }

  public boolean isUserNavigationInSetupMenu(Collection<UserNode> userNodes) {
    //    TODO
    return false;
  }

  public boolean isUserNodeInSetupMenu(UserNode userNode) {
    String pageReference = userNode.getPageRef();
    for (UserNode userNodeTmp : setupMenuUserNodes) {
      if (userNodeTmp.getPageRef().equals(pageReference)) {
        return true;
      }
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
