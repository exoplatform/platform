/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by The eXo Platform SAS
 * Mar 21, 2007  
 * 
 * Abstract calss that one must implement if it want to provide a dedicated handler for a custom servlet path
 * 
 * In case of portal the path is /portal but you could return your own from the getPath() method and hence the 
 * WebAppController would use your own handler
 * 
 * The execute method is to be overideen and the buisness logic should be handled here
 */
abstract public class WebRequestHandler {
  
  public void onInit(WebAppController controller) throws Exception{
    
  }
  
  abstract public String[] getPath() ;
  abstract public void execute(WebAppController app,  HttpServletRequest req, HttpServletResponse res) throws Exception ;
  
  public void onDestroy(WebAppController controler) throws Exception {
    
  }

}