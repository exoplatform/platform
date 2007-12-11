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