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
package org.exoplatform.portal.webui.util;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

/**
 * Created by The eXo Platform SAS
 * Author : Hoa Pham	
 *          hoa.pham@exoplatform.com
 * Jan 28, 2008  
 */
public class SessionProviderFactory {
  
  public final static String SYSTEM_PROVIDER = ":/system".intern();
  public final static String ANONIM_PROVIDER = ":/anonim".intern();  
  
  public static SessionProvider createSystemProvider() {   
    String key = Util.getPortalRequestContext().getSessionId().concat(SYSTEM_PROVIDER);
    return createSessionProvider(key) ;
  }    

  public static SessionProvider createSessionProvider() {    
    String key = Util.getPortalRequestContext().getSessionId();
    return createSessionProvider(key) ;    
  }
  
  public static SessionProvider createAnonimProvider() {
    String key = Util.getPortalRequestContext().getSessionId().concat(ANONIM_PROVIDER) ;
    return createSessionProvider(key) ;
  } 

  private static SessionProvider createSessionProvider(String key) {    
    SessionProviderService service = 
      (SessionProviderService)PortalContainer.getComponent(SessionProviderService.class) ;    
    SessionProvider sessionProvider = null ;    
    try{
      sessionProvider = service.getSessionProvider(key) ;      
    }catch (NullPointerException e) {
      if(key.indexOf(SYSTEM_PROVIDER)>0) {
        sessionProvider = SessionProvider.createSystemProvider() ;               
      }else if(key.indexOf(ANONIM_PROVIDER)>0) {
        sessionProvider = SessionProvider.createAnonimProvider() ;      
      }else {
        sessionProvider = new SessionProvider(null) ;               
      }
      service.setSessionProvider(key,sessionProvider) ;
    }
    return sessionProvider ;
  }

}
