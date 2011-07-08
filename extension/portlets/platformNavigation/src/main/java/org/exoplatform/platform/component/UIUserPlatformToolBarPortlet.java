/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.platform.component;

import java.util.Collection;
import java.util.Collections;

import org.exoplatform.platform.component.social.UINavigationComposer;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.navigation.Scope;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Portlet manages profile.<br>
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class,

template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolBarPortlet/UIUserPlatformToolBarPortlet.gtmpl")
public class UIUserPlatformToolBarPortlet extends UIPortletApplication {

  private String currentPortalName = null;
  private boolean socialPortal = false;

  public UIUserPlatformToolBarPortlet() throws Exception {
    if (isSocialPortal()) {
      addChild(UINavigationComposer.class, null, null);
    }
  }

  public User getUser() throws Exception {
    OrganizationService service = getApplicationComponent(OrganizationService.class);
    String userName = Util.getPortalRequestContext().getRemoteUser();
    User user = service.getUserHandler().findUserByName(userName);
    return user;
  }

  private String getCurrentPortalName() {
    return Util.getPortalRequestContext().getPortalOwner();
  }

  public boolean isSocialPortal() {
    if (currentPortalName != null && getCurrentPortalName().equals(currentPortalName)) {
      return socialPortal;
    }
    currentPortalName = getCurrentPortalName();
    UserPortal userPortal = getUserPortal();
    UserNavigation userNavigation = userPortal.getNavigation(SiteKey.portal(currentPortalName));
    UserNode portalNode = userPortal.getNode(userNavigation, Scope.CHILDREN, null, null);
    socialPortal = portalNode.getChild("spaces") != null;
    return socialPortal;
  }

  private UserPortal getUserPortal() {
    UIPortalApplication uiPortalApplication = Util.getUIPortalApplication();
    return uiPortalApplication.getUserPortalConfig().getUserPortal();
  }

  public Collection<UserNode> getUserNodes(UserNavigation nav) {
    UserPortal userPortall = getUserPortal();
    if (nav != null) {
      try {
        UserNode rootNode = userPortall.getNode(nav, Scope.CHILDREN, null, null);
        return rootNode.getChildren();
      } catch (Exception exp) {
        log.warn(nav.getKey().getName() + " has been deleted");
      }
    }
    return Collections.emptyList();
  }
}