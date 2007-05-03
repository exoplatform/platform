/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.services.portletcontainer.helper.WindowInfosContainer;
import org.exoplatform.web.application.Application;
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
  
  public WebAppController() {
    applications_ = new HashMap<String, Application>() ;
    attributes_ = new HashMap<String, Object>() ;
    handlers_ = new HashMap<String, WebRequestHandler>() ;
  }
  
  public Object  getAttribute(String name, Object value) {
    return attributes_.get(name) ;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Application> T getApplication(String appId) { 
    return (T) applications_.get(appId) ; 
  }
  
  @SuppressWarnings("unchecked")
  public void removeApplication(String appId) { 
    applications_.remove(appId) ; 
  }
  
  public void addApplication(Application app) {
    applications_.put(app.getApplicationId(), app) ;
  }
  
  public  void register(WebRequestHandler handler) {
    for(String path :  handler.getPath()) handlers_.put(path, handler) ;
  }
  
  public void  unregister(String[] paths) {
    for(String path :  paths) handlers_.remove(path) ;
  }
  
  public void service(HttpServletRequest req, HttpServletResponse res) throws Exception {
    WebRequestHandler handler = handlers_.get(req.getServletPath()) ;
    if(handler != null) {
      PortalContainer portalContainer = PortalContainer.getInstance() ;
      List<ComponentRequestLifecycle> components = 
        portalContainer.getComponentInstancesOfType(ComponentRequestLifecycle.class) ;
      for(ComponentRequestLifecycle component : components) {
        component.startRequest(portalContainer);
      }
      WindowInfosContainer.createInstance(portalContainer, req.getSession().getId(), req.getRemoteUser());
      
      handler.execute(this, req, res) ;
      
      for(ComponentRequestLifecycle component : components) {
        component.endRequest(portalContainer);
      }
    }
  }
}