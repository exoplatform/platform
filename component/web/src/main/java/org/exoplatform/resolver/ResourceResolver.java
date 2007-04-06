/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.resolver ;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Mar 15, 2006
 */
abstract public class ResourceResolver {
  
  abstract public URL getResource(String url) throws Exception ;
  abstract public InputStream getInputStream(String url) throws Exception  ;
  
  abstract public List<URL> getResources(String url) throws Exception ;
  abstract public List<InputStream> getInputStreams(String url) throws Exception  ;
  
  @SuppressWarnings("unused")
  public String getWebAccessPath(String url) {
    throw new RuntimeException("This method is not supported") ;
  }
  
  abstract public String getResourceScheme() ;
  
  @SuppressWarnings("unused")
  public String getRealPath(String url) {
    throw new RuntimeException("unsupported method") ;
  }
  
  public String createResourceId(String url) {  return hashCode() + ":" +  url ; }
  
  public boolean isResolvable(String url) {
    return url.startsWith(getResourceScheme()) ;
  }
  
  abstract public boolean isModified(String url, long lastAccess) ;
  
  protected String removeScheme(String url) {
    String scheme = getResourceScheme() ;
    if(url.startsWith(scheme)) {
      return url.substring(scheme.length()) ; 
    }
    return url ;
  }
}