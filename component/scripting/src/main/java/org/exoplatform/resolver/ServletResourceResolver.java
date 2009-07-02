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
package org.exoplatform.resolver ;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS
 * Mar 15, 2006
 */
public class ServletResourceResolver extends ResourceResolver {
  
  protected static Log log = ExoLogger.getLogger("portal:ServletResourceResolver");
  
  private ServletContext scontext_ ;
  private String scheme_  ;
  
  public ServletResourceResolver(ServletContext context, String scheme) {
    scontext_ =  context ;
    scheme_ = scheme ;
  }
  
  public URL getResource(String url) throws Exception {
    String path = removeScheme(url) ;
    return scontext_.getResource(path) ; 
  }
  
  public InputStream getInputStream(String url) throws Exception  {
    String path = removeScheme(url) ;
    return scontext_.getResourceAsStream(path)  ; 
  }
  
  public List<URL> getResources(String url) throws Exception {
    ArrayList<URL>  urlList = new ArrayList<URL>() ;
    urlList.add(getResource(url)) ;
    return urlList ;
  }
  
  public List<InputStream> getInputStreams(String url) throws Exception {
    ArrayList<InputStream>  inputStreams = new ArrayList<InputStream>() ;
    inputStreams.add(getInputStream(url)) ;
    return inputStreams ;
  }
  
  public String getRealPath(String url) {
    String path = removeScheme(url) ;
    return scontext_.getRealPath(path);
  }
  
  public boolean isModified(String url, long lastAccess) {
    File file = new File(getRealPath(url)) ;
    if(log.isDebugEnabled())
      log.debug(url + ": " + file.lastModified() + " " + lastAccess) ;
    if(file.exists() && file.lastModified() > lastAccess) {
      return true ;
    }    
    return false ;
  }
  
  public String getWebAccessPath(String url) {
    if(log.isDebugEnabled())
      log.debug("GET WEB ACCESS " +  url);
    return "/" + scontext_.getServletContextName() + removeScheme(url) ; 
  }
  
  public String getResourceScheme() { return scheme_ ; }
  
}