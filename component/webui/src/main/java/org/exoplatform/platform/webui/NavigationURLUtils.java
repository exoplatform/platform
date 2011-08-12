/**
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
package org.exoplatform.platform.webui;

import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.web.url.navigation.NavigationResource;
import org.exoplatform.web.url.navigation.NodeURL;

/**
 * Contains some common methods for using as utility.<br>
 */
public class NavigationURLUtils {

  /**
   * Get the uri.
   * 
   * @param url
   * @return
   * @since 1.2.1
   */
  public static String getURLInCurrentPortal(String uri) {
    RequestContext ctx = RequestContext.getCurrentInstance();
    NodeURL nodeURL = ctx.createURL(NodeURL.TYPE);
    NavigationResource resource = new NavigationResource(SiteType.PORTAL, Util.getPortalRequestContext().getPortalOwner(), uri);
    return nodeURL.setResource(resource).toString();
  }

  /**
   * Get the uri.
   * 
   * @param url
   * @return
   * @since 1.2.1
   */
  public static String getCurrentPortalURL() {
    RequestContext ctx = RequestContext.getCurrentInstance();
    NodeURL nodeURL = ctx.createURL(NodeURL.TYPE);
    NavigationResource resource = new NavigationResource(SiteKey.portal(Util.getPortalRequestContext().getPortalOwner()), null);
    return nodeURL.setResource(resource).toString();
  }

  /**
   * Get the uri.
   * 
   * @param url
   * @return
   * @since 1.2.1
   */
  public static String getURL(SiteKey siteKey, String uri) {
    RequestContext ctx = RequestContext.getCurrentInstance();
    NodeURL nodeURL = ctx.createURL(NodeURL.TYPE);
    NavigationResource resource = new NavigationResource(siteKey, uri);
    return nodeURL.setResource(resource).toString();
  }

  /**
   * Get the space url.
   * 
   * @param node
   * @return
   * @since 1.2.1
   */
  public static String getURL(UserNode node) {
    RequestContext ctx = RequestContext.getCurrentInstance();
    NodeURL nodeURL = ctx.createURL(NodeURL.TYPE);
    return nodeURL.setNode(node).toString();
  }
}