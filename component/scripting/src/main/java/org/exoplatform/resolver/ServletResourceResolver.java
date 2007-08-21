/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.resolver ;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
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