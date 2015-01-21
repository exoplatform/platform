package org.exoplatform.platform.component;

import java.util.List;
import java.util.ResourceBundle;

import org.exoplatform.platform.common.service.MenuConfiguratorService;
import org.exoplatform.platform.navigation.component.breadcrumb.UserNavigationHandlerService;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 26/11/12
 */

@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "app:/groovy/platformNavigation/portlet/UIBreadCrumbsNavigationPortlet/UIBreadCrumbsNavigationPortlet.gtmpl"
)
public class UIBreadCrumbsNavigationPortlet extends UIPortletApplication {

  private UserNavigationHandlerService userService           = null;
  private static final String          USER                  = "/user/";

  private static final String          WIKI_HOME             = "/WikiHome";

  private static final String          WIKI_REF              = "wiki";
  private static final String          EDIT_PROFILE_NODE     = "edit-profile";
  private static final String          PROFILE_PATH          = "/profile";
  private static final String          MY_PROFILE_TITLE      = "UIBreadCrumbsNavigationPortlet.title.MyProfile";

  public UIBreadCrumbsNavigationPortlet() throws Exception {
    userService = getApplicationComponent(UserNavigationHandlerService.class);
  }

  @Override
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    if (isUserUrl() && isOnProfilePage() || isEditProfilePage()) {
      if (isOwner() || isEditProfilePage()) {
        ResourceBundle resApp = context.getApplicationResourceBundle();
        String title = resApp.getString(MY_PROFILE_TITLE);
        Util.getPortalRequestContext().getRequest().setAttribute(PortalRequestContext.REQUEST_TITLE, title);        
      } else {
        Util.getPortalRequestContext().getRequest()
        .setAttribute(PortalRequestContext.REQUEST_TITLE, getOwnerProfile().getFullName());
      }
      
    }
    super.processRender(app, context);
  }
  
  protected UserNavigation getSelectedNode() throws Exception {
    UserNode node = Util.getUIPortal().getSelectedUserNode();
    UserNavigation nav = getUserPortal().getNavigation(node.getNavigation().getKey());
    return nav;
  }

  protected String getSpacename(String SpaceUrl) throws Exception {
    Space space = Utils.getSpaceService().getSpaceByUrl(SpaceUrl);
    if (space != null) {
      String spaceNAme = space.getDisplayName();
      return spaceNAme;
    } else {
      return "";
    }
  }

  protected String getSpaceUrl() throws Exception {
    String spaceUrl = null;
    UserNavigation nav = getSelectedNode();
    String ownerId = nav.getKey().getName();
    if (ownerId.contains("/spaces/")) {
      Space space = Utils.getSpaceService().getSpaceByGroupId(ownerId);
      if (space == null) {
        return spaceUrl;
      }
      return space.getUrl();
    }
    return spaceUrl;
  }

  protected String getImageSource(String url) throws Exception {
    Space space = Utils.getSpaceService().getSpaceByUrl(url);

    if (space == null) {
      return LinkProvider.SPACE_DEFAULT_AVATAR_URL;
    }

    String spaceAvatar = space.getAvatarUrl();
    return (spaceAvatar == null || spaceAvatar.isEmpty()) ? LinkProvider.SPACE_DEFAULT_AVATAR_URL : spaceAvatar;
  }

  protected String getAvatarURL(Profile profile) {
    String ownerAvatar = profile.getAvatarUrl();
    if (ownerAvatar == null || ownerAvatar.isEmpty()) {
      ownerAvatar = LinkProvider.PROFILE_DEFAULT_AVATAR_URL;
    }
    return ownerAvatar;
  }

  protected boolean isUserUrl() throws Exception {
    List<String> uris = userService.loadUserNavigation();
    UserNavigation nav = getSelectedNode();
    SiteType navType = nav.getKey().getType();
    UserNode node = Util.getUIPortal().getSelectedUserNode();
    String uri = node.getURI();
    if (uris.contains(uri) || navType.equals(SiteType.USER) ||
        Util.getPortalRequestContext().getRequest().getRequestURL().toString().contains(getWikiURL())) {
      return true;
    } else
      return false;
  }

  protected boolean isEditProfilePage() throws Exception {
    String uri = Util.getUIPortal().getSelectedUserNode().getURI();
    if (uri.endsWith(EDIT_PROFILE_NODE)) {
      return true;
    }
    
    return false;  
  }

  private boolean isOnProfilePage() throws Exception {
    System.out.println(Util.getPortalRequestContext().getRequest().getRequestURL().toString());
    return Util.getPortalRequestContext().getRequest().getRequestURL().toString().contains(PROFILE_PATH);
  }
  
  private String getWikiURL() {
    return NavigationURLUtils.getURLInCurrentPortal(WIKI_REF) + USER + getOwnerProfile().getIdentity().getRemoteId() + WIKI_HOME;
  }

  private Profile getOwnerProfile() {
    return Utils.getOwnerIdentity(true).getProfile();
  }

  protected boolean isGroupUrl() throws Exception {
    List<UserNode> setupMenuUserNodes = getApplicationComponent(MenuConfiguratorService.class).getSetupMenuItems(getUserPortal());
    UserNode node = Util.getUIPortal().getSelectedUserNode();
    boolean isAdminUrl = false;
    for (UserNode menuNode : setupMenuUserNodes) {
      if (menuNode.getURI().equals(node.getURI()) && menuNode.getPageRef().equals(node.getPageRef())) {
        isAdminUrl = true;
        break;
      }
    }
    if (isAdminUrl || node.getURI().equals("search")) {
      return true;
    } else {
      return false;
    }
  }

  protected static String getEncodedResolvedLabel() throws Exception {
    UserNode node = Util.getUIPortal().getSelectedUserNode();
    UserPortal userPortal = getUserPortal();
    UserNavigation nav = userPortal.getNavigation(node.getNavigation().getKey());
    UserNode targetNode = userPortal.resolvePath(nav, null, node.getURI());
    return targetNode.getResolvedLabel();
  }

  private static UserPortal getUserPortal() {
    return Util.getPortalRequestContext().getUserPortalConfig().getUserPortal();
  }

  protected boolean isSpaceUrl() throws Exception {
    String urlPath = Util.getPortalRequestContext().getRequest().getRequestURI();
    UserNavigation nav = getSelectedNode();
    SiteType navType = nav.getKey().getType();
    if (urlPath.contains(":spaces:") && navType.equals(SiteType.GROUP)) {
      return true;
    } else {
      return false;
    }
  }

  protected boolean isOwner() {
    return Utils.isOwner();
  }
}