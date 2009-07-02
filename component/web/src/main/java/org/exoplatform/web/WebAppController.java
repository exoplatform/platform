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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.helper.WindowInfosContainer;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.command.CommandHandler;
/**
 * Created by The eXo Platform SAS
 * Mar 21, 2007  
 * 
 * The WebAppController is the entry point of the eXo web framework
 * 
 * It also stores WebRequestHandlers, Attributes and deployed Applications
 * 
 */
public class WebAppController {
  
  protected static Log log = ExoLogger.getLogger("portal:WebAppController");  
  
  private HashMap<String, Object>  attributes_ ;
  private HashMap<String, Application>  applications_ ;
  private HashMap<String, WebRequestHandler> handlers_ ;
  
  /**
   * The WebAppControler along with the PortalRequestHandler defined in the init() method of the
   * PortalController servlet (controller.register(new PortalRequestHandler())) also add the
   * CommandHandler object that will listen for the incoming /command path in the URL
   * 
   * @throws Exception
   */
  public WebAppController() throws Exception {
    applications_ = new HashMap<String, Application>() ;
    attributes_ = new HashMap<String, Object>() ;
    handlers_ = new HashMap<String, WebRequestHandler>() ;
    register(new CommandHandler()) ;
  }
  
  @SuppressWarnings("unused")
  public Object  getAttribute(String name, Object value) { return attributes_.get(name) ; }
  
  @SuppressWarnings("unchecked")
  public <T extends Application> T getApplication(String appId) { 
    return (T) applications_.get(appId) ; 
  }
  
  public List<Application> getApplicationByType(String type) {
    List<Application> applications = new ArrayList<Application>() ;
    for(Application app : applications_.values()) {
      if(app.getApplicationType().equals(type)) applications.add(app) ;
    }
    return applications ;
  }
  
  public void removeApplication(String appId) { 
    applications_.remove(appId) ; 
  }
  
  public void addApplication(Application app) {
    applications_.put(app.getApplicationId(), app) ;
  }
  
  public  void register(WebRequestHandler handler) throws Exception {
    for(String path :  handler.getPath()) handlers_.put(path, handler) ;
  }
  
  public void  unregister(String[] paths) {
    for(String path :  paths) handlers_.remove(path) ;
  }
  
  /**
   * This is the first method - in the eXo web framework - reached by incoming HTTP request, it acts like a
   * servlet service() method
   * 
   * According to the servlet path used the correct handler is selected and then executed.
   * 
   * The event "exo.application.portal.start-http-request" and "exo.application.portal.end-http-request" are also sent 
   * through the ListenerService and several listeners may listen to it.
   * 
   * Finally a WindowsInfosContainer object using a ThreadLocal (from the portlet-container product) is created 
   */
  public void service(HttpServletRequest req, HttpServletResponse res) throws Exception {
    WebRequestHandler handler = handlers_.get(req.getServletPath()) ;
    if(log.isDebugEnabled()) {
      log.debug("Servlet Path: " + req.getServletPath());    
      log.debug("Handler used for this path: " + handler);
    }
    if(handler != null) {
      ExoContainer portalContainer = ExoContainerContext.getCurrentContainer();
      List<ComponentRequestLifecycle> components = 
        portalContainer.getComponentInstancesOfType(ComponentRequestLifecycle.class) ;
      try {
        for(ComponentRequestLifecycle component : components) {
          component.startRequest(portalContainer);
        }
        WindowInfosContainer.createInstance(portalContainer, req.getSession().getId(), req.getRemoteUser());
        
        handler.execute(this, req, res) ;
      } finally {
        WindowInfosContainer.setInstance(null);
        for(ComponentRequestLifecycle component : components) {
          try {
            component.endRequest(portalContainer);
          } catch (Exception e) {
            log.warn("An error occured while calling the endRequest method", e);
          }
        }
      }      
    }
  }
}