package org.exoplatform.platform.component;

/**
 * Created by IntelliJ IDEA.
 * User: khemais.menzli
 * Date: 31 ao�t 2010
 * Time: 13:16:17
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIMySpacePlatformToolBarPortlet/UIMySpacePlatformToolBarPortlet.gtmpl", events = { @EventConfig(listeners = UIMySpacePlatformToolBarPortlet.NavigationChangeActionListener.class) })
public class UIMySpacePlatformToolBarPortlet extends UIPortletApplication {
  private static final String SPACE_SETTINGS = "settings";

  private SpaceService spaceService = null;
  private OrganizationService organizationService = null;
  private String userId = null;
  private boolean groupNavigationPermitted = false;
  private UserNodeFilterConfig mySpaceFilterConfig;

  /**
   * constructor
   * 
   * @throws Exception
   */
  public UIMySpacePlatformToolBarPortlet() throws Exception {
    try {
      spaceService = getApplicationComponent(SpaceService.class);
    } catch (Exception exception) {
      // spaceService should be "null" because the Social profile isn't
      // activated
    }
    organizationService = getApplicationComponent(OrganizationService.class);
    UserACL userACL = getApplicationComponent(UserACL.class);
    // groupNavigationPermitted is set to true if the user is the super
    // user
    if (getUserId().equals(userACL.getSuperUser())) {
      groupNavigationPermitted = true;
    } else {
      Collection memberships = organizationService.getMembershipHandler().findMembershipsByUser(getUserId());
      for (Object object : memberships) {
        Membership membership = (Membership) object;
        if (membership.getMembershipType().equals(userACL.getAdminMSType())) {
          groupNavigationPermitted = true;
          break;
        }
      }
    }
    UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
    // builder.withAuthorizationCheck().withVisibility(Visibility.DISPLAYED,
    // Visibility.HIDDEN).withTemporalCheck();
    mySpaceFilterConfig = builder.build();
  }

  public List<UserNavigation> getGroupNavigations() throws Exception {
    String remoteUser = getUserId();
    UserPortal userPortal = getUserPortal();
    List<UserNavigation> allNavigations = userPortal.getNavigations();
    List<UserNavigation> computedNavigations = null;
    if (spaceService != null) {
      computedNavigations = new ArrayList<UserNavigation>(allNavigations);
      List<Space> spaces = spaceService.getAccessibleSpaces(remoteUser);
      Iterator<UserNavigation> navigationItr = computedNavigations.iterator();
      String ownerId;
      String[] navigationParts;
      Space space;
      while (navigationItr.hasNext()) {
        ownerId = navigationItr.next().getKey().getName();
        if (ownerId.startsWith("/spaces/")) {
          navigationParts = ownerId.split("/");
          if (navigationParts.length < 3) {
            continue;
          }
          space = spaceService.getSpaceByUrl(navigationParts[2]);
          if (space == null)
            navigationItr.remove();
          if (!navigationParts[1].equals("spaces") && !spaces.contains(space))
            navigationItr.remove();
        } else { // not spaces navigation
          navigationItr.remove();
        }
      }
    }
    return computedNavigations;
  }

  public boolean isRender(PageNode spaceNode, PageNode applicationNode) throws SpaceException {
    if (spaceService != null) {
      String remoteUser = getUserId();
      String spaceUrl = spaceNode.getUri();
      if (spaceUrl.contains("/")) {
        spaceUrl = spaceUrl.split("/")[0];
      }
      Space space = spaceService.getSpaceByUrl(spaceUrl);
      if (space != null) {
        if (spaceService.hasSettingPermission(space, remoteUser)) {
          return true;
        }
        if (SPACE_SETTINGS.equals(applicationNode.getName())) {
          return false;
        }
      }
    }
    return true;
  }

  public UserNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  /**
   * gets remote user Id
   * 
   * @return userId
   */
  private String getUserId() {
    if (userId == null) {
      userId = Util.getPortalRequestContext().getRemoteUser();
    }
    return userId;
  }

  boolean renderSpacesLink() throws Exception {
    UserNavigation nav = getCurrentPortalNavigation();
    Collection<UserNode> userNodes = getUserNodes(nav);
    for (UserNode node : userNodes) {
      if (node.getURI().equals("spaces")) {
        return true;
      }
    }
    return false;
  }

  private UserNavigation getCurrentPortalNavigation() {
    List<UserNavigation> userNavigation = getUserPortal().getNavigations();
    for (UserNavigation nav : userNavigation) {
      if (nav.getKey().getType().equals(SiteType.PORTAL)) {
        return nav;
      }
    }
    return null;
  }

  private List<UserNavigation> getAllGroupUserNavigation() {
    List<UserNavigation> groupNavigation = new LinkedList<UserNavigation>();
    List<UserNavigation> userNavigation = getUserPortal().getNavigations();
    for (UserNavigation nav : userNavigation) {
      if (nav.getKey().getType().equals(SiteType.GROUP)) {
        groupNavigation.add(nav);
      }
    }
    return groupNavigation;
  }

  public UserNavigation getCurrentUserNavigation() throws Exception {
    WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance();
    return getNavigation(SiteKey.user(rcontext.getRemoteUser()));
  }

  private UserNavigation getNavigation(SiteKey userKey) {
    UserPortal userPortal = getUserPortal();
    return userPortal.getNavigation(userKey);
  }

  private UserPortal getUserPortal() {
    UIPortalApplication uiPortalApplication = Util.getUIPortalApplication();
    return uiPortalApplication.getUserPortalConfig().getUserPortal();
  }

  public Collection<UserNode> getUserNodes(UserNavigation nav) {
    UserPortal userPortall = getUserPortal();
    if (nav != null) {
      try {
        UserNode rootNode = userPortall.getNode(nav, Scope.ALL, mySpaceFilterConfig, null);
        return rootNode.getChildren();
      } catch (Exception exp) {
        log.warn(nav.getKey().getName() + " has been deleted");
      }
    }
    return Collections.emptyList();
  }

  private UserNode getSelectedNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  public boolean hasPermission() throws Exception {
    return groupNavigationPermitted;
  }

  public static class NavigationChangeActionListener extends EventListener<UIMySpacePlatformToolBarPortlet> {

    @Override
    public void execute(Event<UIMySpacePlatformToolBarPortlet> event) throws Exception {
      // This event is only a trick for updating the MySpacePlatformToolBar
      // Portlet
    }

  }

}