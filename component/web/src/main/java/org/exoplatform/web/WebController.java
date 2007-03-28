/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
public class WebController {
  private HashMap<String, Object>  attributes_ ;
  
  public WebController() {
    attributes_ = new HashMap<String, Object>() ;
  }
  
  public Object  getAttribute(String name, Object value) {
    return attributes_.get(name) ;
  }
  
  public  void register(WebRequestHandler handler) {
    
  }
  
  public void  unregister(String path) {
    
  }
  
  public void service(HttpServletRequest req, HttpServletResponse res) throws Exception {
    
  }
}
