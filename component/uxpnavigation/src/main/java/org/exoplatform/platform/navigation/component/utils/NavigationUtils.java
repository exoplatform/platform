/***************************************************************************
 * Copyright (C) 2003-2014 eXo Platform SAS.
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
 * 
 */
package org.exoplatform.platform.navigation.component.utils;


import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.web.controller.QualifiedName;

public class NavigationUtils {
    private static final Log LOG = ExoLogger.getLogger(NavigationUtils.class);

    public static String getCurrentUser() {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      IdentityManager idm = (IdentityManager) container.getComponentInstanceOfType(IdentityManager.class);
      PortalRequestContext request = Util.getPortalRequestContext() ;
      String currentPath = request.getControllerContext().getParameter(QualifiedName.parse("gtn:path"));
      String []splitCurrentUser = currentPath.split("/");
      String currentUserName = currentPath.split("/")[splitCurrentUser.length - 1];
      try {
        if ((currentUserName != null)&& (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null)) return currentUserName;
        else if (((currentUserName = currentPath.split("/")[splitCurrentUser.length-2]) != null)&&
            (idm.getOrCreateIdentity(OrganizationIdentityProvider.NAME, currentUserName, false) != null)) {
          return currentUserName;
        }
      } catch (Exception e) {
        if(LOG.isDebugEnabled()) {
          LOG.debug("Could not found Identity of user " + currentUserName);
        }
        return null;
      }
      return null;
    }

    public static String getCurrentLoginUser() {
      return ConversationState.getCurrent().getIdentity().getUserId();
    }
}
