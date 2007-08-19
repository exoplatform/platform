/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.helper.WindowInfosContainer;
import org.exoplatform.web.WebAppController;

/**
 * Created by The eXo Platform SAS        
 * Date: Jan 25, 2003
 * Time: 5:25:52 PM
 */
public class PortalSessionListener implements HttpSessionListener {
	
  protected static Log log = ExoLogger.getLogger("portal:PortalSessionListener");
  
  public PortalSessionListener() {
  
  }
  
  public void sessionCreated(HttpSessionEvent event) {
  }

  /**
   * This method is called when a HTTP session of a Portal instance is destroyed. 
   * By default the session time is 30 minutes.
   * 
   * In this method, we:
   * 1) first get the portal instance name from where the session is removed.
   * 2) Get the correct instance object from the Root container
   * 3) Put the portal instance in the Portal ThreadLocal
   * 4) Get the main entry point (WebAppController) from the current portal container 
   * 5) Extract from the WebAppController the PortalApplication object which is the entry point to
   *    the StateManager object
   * 6) Expire the portal session stored in the StateManager
   * 7) Finally, removes the WindowInfos object from the WindowInfosContainer container
   * 8) Flush the threadlocal for the PortalContainer
   * 
   */
  public void sessionDestroyed(HttpSessionEvent event) {
    try {
      String portalContainerName = event.getSession().getServletContext().getServletContextName() ;
      log.warn("Destroy session from " + portalContainerName + " portal");
      RootContainer rootContainer = RootContainer.getInstance() ;
      PortalContainer portalContainer = rootContainer.getPortalContainer(portalContainerName) ;
      PortalContainer.setInstance(portalContainer); 
      WebAppController controller = 
        (WebAppController)portalContainer.getComponentInstanceOfType(WebAppController.class) ;
      PortalApplication portalApp =  controller.getApplication(PortalApplication.PORTAL_APPLICATION_ID) ;
      portalApp.getStateManager().expire(event.getSession().getId(), portalApp) ;
      
      WindowInfosContainer.removeInstance(portalContainer, event.getSession().getId());
    } catch(Exception ex) {
      log.error("Error while destroying a portal session",ex);
    } finally {
      PortalContainer.setInstance(null) ;
    }
  }
  
}