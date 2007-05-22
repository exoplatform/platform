/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.portletcontainer.helper.WindowInfosContainer;
import org.exoplatform.web.application.Application;
import org.exoplatform.web.command.CommandHandler;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
public class WebAppController {
  private HashMap<String, Object>  attributes_ ;
  private HashMap<String, Application>  applications_ ;
  private HashMap<String, WebRequestHandler> handlers_ ;
  
  public WebAppController() throws Exception {
    applications_ = new HashMap<String, Application>() ;
    attributes_ = new HashMap<String, Object>() ;
    handlers_ = new HashMap<String, WebRequestHandler>() ;
    register(new CommandHandler()) ;
  }
  
  public Object  getAttribute(String name, Object value) {
    return attributes_.get(name) ;
  }
  
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
  
  public void service(HttpServletRequest req, HttpServletResponse res) throws Exception {
    System.out.println("\n\n ==> Servelt Path " +  req.getServletPath());
    WebRequestHandler handler = handlers_.get(req.getServletPath()) ;
    System.out.println("\n\n ==> Handler " +  handler);
    if(handler != null) {
      PortalContainer portalContainer = PortalContainer.getInstance() ;
      List<ComponentRequestLifecycle> components = 
        portalContainer.getComponentInstancesOfType(ComponentRequestLifecycle.class) ;
      ListenerService lservice = 
        (ListenerService) portalContainer.getComponentInstanceOfType(ListenerService.class) ;
      lservice.broadcast("exo.application.portal.start-http-request", this, req) ;
      for(ComponentRequestLifecycle component : components) {
        component.startRequest(portalContainer);
      }
      WindowInfosContainer.createInstance(portalContainer, req.getSession().getId(), req.getRemoteUser());
      
      handler.execute(this, req, res) ;
      
      for(ComponentRequestLifecycle component : components) {
        component.endRequest(portalContainer);
      }
      lservice.broadcast("exo.application.portal.end-http-request", this, req) ;
    }
  }
}