/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.application.Application;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
public class WebController {
  private HashMap<String, Object>  attributes_ ;
  private HashMap<String, Application>  applications_ ;
  private HashMap<String, WebRequestHandler> handlers_ ;
  
  public WebController() {
    attributes_ = new HashMap<String, Object>() ;
    handlers_ = new HashMap<String, WebRequestHandler>() ;
  }
  
  public Object  getAttribute(String name, Object value) {
    return attributes_.get(name) ;
  }
  
  public Application getApplication(String appId) { return applications_.get(appId) ; }
  
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
    if(handler != null) handler.execute(this, req, res) ;
  }
}