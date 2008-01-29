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
package org.exoplatform.portal.application;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.portal.webui.util.SessionProviderFactory;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS
 * Author : Hoa Pham	
 *          hoa.pham@exoplatform.com
 * Jan 28, 2008    
 */
public class SessionProviderListener implements HttpSessionListener {
  
  protected static Log log = ExoLogger.getLogger("portal:SessionProviderListener");
  
  public void sessionCreated(HttpSessionEvent event) {
  }

  public void sessionDestroyed(HttpSessionEvent event) {   
    try{
      HttpSession session = event.getSession() ;      
      ServletContext context = session.getServletContext() ;    
      PortalContainer pcontainer = 
        RootContainer.getInstance().getPortalContainer(context.getServletContextName()) ;
      PortalContainer.setInstance(pcontainer) ;       
      SessionProviderService providerService = 
        (SessionProviderService)pcontainer.getComponentInstanceOfType(SessionProviderService.class) ;
//    remove all SystemSessionProvider & AnonimSessionProvider
      String sessionId = session.getId();
      String systemKey = sessionId.concat(SessionProviderFactory.SYSTEM_PROVIDER);
      String anonimKey = sessionId.concat(SessionProviderFactory.ANONIM_PROVIDER) ;      
      providerService.removeSessionProvider(sessionId) ;      
      providerService.removeSessionProvider(systemKey) ;      
      providerService.removeSessionProvider(anonimKey) ;       
      PortalContainer.setInstance(null) ;           

    }catch (Exception ex) {
      log.error("Error while destroying jcr sessions",ex);
    }finally {
      PortalContainer.setInstance(null) ;
    }    
  }

}
