/**
 * Copyright (C) 2009 eXo Platform SAS.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
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
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

@ComponentConfig(lifecycle = UIApplicationLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIUserPlatformToolBarSitePortlet/UIUserPlatformToolBarSitePortlet.gtmpl")
public class UIUserPlatformToolBarSitePortlet extends UIPortletApplication {
  public Log log = ExoLogger.getExoLogger(UIUserPlatformToolBarSitePortlet.class);

  private UserACL userACL = null;
  private UserNodeFilterConfig userFilterConfig;

  public UIUserPlatformToolBarSitePortlet() throws Exception {
    UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
    builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
    userFilterConfig = builder.build();
    userACL = getApplicationComponent(UserACL.class);
  }

  public boolean hasEditOrCreatePortalPermission() throws Exception {
    List<String> AllowedToEditPortalNames = getAllowedToEditPortalNames();
    return userACL.hasCreatePortalPermission() || AllowedToEditPortalNames.size() > 0;
  }

  private List<String> getAllowedToEditPortalNames() throws Exception {
    List<String> allowedPortalList = new ArrayList<String>();

    UserPortalConfigService dataStorage = getApplicationComponent(UserPortalConfigService.class);

    List<String> portals = dataStorage.getAllPortalNames();
    for (String portalName : portals) {
      try {
        UserPortalConfig portalConfig = dataStorage.getUserPortalConfig(portalName, getRemoteUser(),
            PortalRequestContext.USER_PORTAL_CONTEXT);
        if (portalConfig != null && userACL.hasEditPermission(portalConfig.getPortalConfig())) {
          allowedPortalList.add(portalName);
        } else {
          if (log.isDebugEnabled()) {
            log.debug(getRemoteUser() + " has no permission to access " + portalName);
          }
        }
      } catch (Exception exception) {
        log.warn("Can't access to the portal " + portalName, exception);
      }
    }
    return allowedPortalList;
  }

  public List<String> getAllPortalNames() throws Exception {
    List<String> allowedPortalList = new ArrayList<String>();

    UserPortalConfigService dataStorage = getApplicationComponent(UserPortalConfigService.class);

    List<String> portals = dataStorage.getAllPortalNames();
    for (String portalName : portals) {
      try {
        UserPortalConfig portalConfig = dataStorage.getUserPortalConfig(portalName, getRemoteUser(),
            PortalRequestContext.USER_PORTAL_CONTEXT);
        if (portalConfig != null) {
          allowedPortalList.add(portalName);
        } else {
          if (log.isDebugEnabled()) {
            log.debug(getRemoteUser() + " has no permission to access " + portalName);
          }
        }
      } catch (Exception exception) {
        log.warn("Can't access to the portal " + portalName);
      }
    }
    return allowedPortalList;
  }

  public String getCurrentPortal() {
    return Util.getPortalRequestContext().getPortalOwner();
  }

  public String getPortalURI(String portalName) {
    NodeURL url = Util.getPortalRequestContext().createURL(NodeURL.TYPE);
    url.setResource(new NavigationResource(portalName));
    return url.toString();
  }

  public UserNavigation getCurrentPortalNavigation() throws Exception {
    return getNavigation(SiteKey.portal(getCurrentPortal()));

  }

  private String getRemoteUser() {
    return Util.getPortalRequestContext().getRemoteUser();
  }

  public UserNode getSelectedPageNode() throws Exception {
    return Util.getUIPortal().getSelectedUserNode();
  }

  private UserPortal getUserPortal() {
    PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
    return portalRequestContext.getUserPortal();
  }

  private UserNavigation getNavigation(SiteKey userKey) {
    UserPortal userPortal = getUserPortal();
    return userPortal.getNavigation(userKey);
  }

  public Collection<UserNode> getUserNodes(UserNavigation nav) {
    UserPortal userPortall = getUserPortal();
    if (nav != null) {
      try {
        UserNode rootNode = userPortall.getNode(nav, Scope.ALL, userFilterConfig, null);
        return rootNode.getChildren();
      } catch (Exception exp) {
        log.warn(nav.getKey().getName() + " has been deleted");
      }
    }
    return Collections.emptyList();
  }
}