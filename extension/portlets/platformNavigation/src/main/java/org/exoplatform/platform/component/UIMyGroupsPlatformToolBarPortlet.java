package org.exoplatform.platform.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIMyGroupsPlatformToolBarPortlet/UIMyGroupsPlatformToolBarPortlet.gtmpl")
public class UIMyGroupsPlatformToolBarPortlet extends UIPortletApplication {

  private OrganizationService organizationService = null;
  private String userId = null;
  private boolean groupNavigationPermitted = false;

  public UIMyGroupsPlatformToolBarPortlet() throws Exception {
    organizationService = getApplicationComponent(OrganizationService.class);
    UserACL userACL = getApplicationComponent(UserACL.class);
    //groupNavigationPermitted is set to true if the user is the super user or have the administration rights
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
  }

  //return group navigation that does not include any space navigation
  public List<PageNavigation> getGroupNavigations() throws Exception {
    String remoteUser = getUserId();
    UserPortalConfig userPortalConfig = Util.getUIPortalApplication().getUserPortalConfig();
    List<PageNavigation> allNavigations = userPortalConfig.getNavigations();
    List<PageNavigation> computedNavigations = new ArrayList<PageNavigation>();
    for (PageNavigation navigation : allNavigations) {
      if ((navigation.getOwnerType().equals(PortalConfig.GROUP_TYPE)) && (navigation.getOwnerId().indexOf("spaces") < 0)) {
        computedNavigations.add(PageNavigationUtils.filter(navigation, remoteUser));
      }
    }
    return computedNavigations;
  }

  public PageNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedNode();
  }

  private String getUserId() {
    if (userId == null) {
      userId = Util.getPortalRequestContext().getRemoteUser();
    }
    return userId;
  }

  public boolean hasPermission() throws Exception {
    return groupNavigationPermitted;
  }

}
