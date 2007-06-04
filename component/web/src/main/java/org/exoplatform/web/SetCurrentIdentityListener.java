/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.organization.auth.AuthenticationService;
import org.exoplatform.services.organization.auth.Identity;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 17, 2007  
 */
public class SetCurrentIdentityListener extends Listener<WebAppController, HttpServletRequest> {
  
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
//    System.out.println("\n\nSet Identity for user " + remoteUser);
  }
}