/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
abstract public class WebRequestHandler {
  
  public void onInit(WebController controller) throws Exception{
    
  }
  
  abstract public String[] getPath() ;
  abstract public void execute(WebController app,  HttpServletRequest req, HttpServletResponse res) throws Exception ;
  
  public void onDestroy(WebController controler) throws Exception {
    
  }

}