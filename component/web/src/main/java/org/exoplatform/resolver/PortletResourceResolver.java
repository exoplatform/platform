/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.resolver ;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Mar 15, 2006
 */
public class PortletResourceResolver extends ResourceResolver {
  
  private PortletContext pcontext_ ;
  private String scheme_ ;
  
  public PortletResourceResolver(PortletContext context, String scheme) {
    pcontext_ =  context ;
    scheme_ = scheme ;
  }
  
  public URL getResource(String url) throws Exception {
    String path = removeScheme(url) ;
    return pcontext_.getResource(path) ; 
  }
  
  public InputStream getInputStream(String url) throws Exception  {
    String path = removeScheme(url) ;
    return pcontext_.getResourceAsStream(path)  ; 
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
    return pcontext_.getRealPath(path);
  }
  
  public boolean isModified(String url, long lastAccess) {
    File file = new File(getRealPath(url)) ;
    //System.out.println(url + ": " + file.lastModified() + " " + lastAccess) ;
    if(file.exists() && file.lastModified() > lastAccess) {
      return true ;
    }    
    return false ;
  }
  
  public String getWebAccessPath(String url) {
    return "/" + pcontext_.getPortletContextName() + removeScheme(url) ; 
  }
  
  public String getResourceScheme() { return scheme_ ; }
  
}