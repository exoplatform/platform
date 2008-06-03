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
package org.exoplatform.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.auth.AuthenticationService;
import org.exoplatform.services.security.Identity;

/**
 * Created by The eXo Platform SAS
 * May 17, 2007  
 */
public class SetCurrentIdentityListener extends Listener<WebAppController, HttpServletRequest> {
  
  protected static Log log = ExoLogger.getLogger("authentication:SetCurrentIdentityListener");
  
  public void onEvent(Event<WebAppController, HttpServletRequest> event)  throws Exception {
    PortalContainer container =  PortalContainer.getInstance() ;
    AuthenticationService authService = 
      (AuthenticationService)  container.getComponentInstanceOfType(AuthenticationService.class) ;
    String remoteUser = event.getData().getRemoteUser() ;
    if(remoteUser != null) {
      Identity identity =  authService.getIdentityBySessionId(remoteUser) ;
      if(identity == null) {
        throw new Exception("Cannot find the identity for user " + remoteUser) ;
      }
      authService.setCurrentIdentity(identity) ;
    }
    if(log.isDebugEnabled())
      log.debug("Set Identity for user " + remoteUser);
  }
}