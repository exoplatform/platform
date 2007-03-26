/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.templates.groovy;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Oct 24, 2006
 */
public class ApplicationResourceResolver extends ResourceResolver {
  private Map<String, ResourceResolver>  resolvers_ = new  HashMap<String, ResourceResolver>();
  
  public ApplicationResourceResolver() {
    addResourceResolver(new FileResourceResolver()) ;
    addResourceResolver(new ClasspathResourceResolver()) ;
  }
  
  public ResourceResolver  getResourceResolverByScheme(String scheme) {
    return  resolvers_.get(scheme) ;
  }
  
  public ResourceResolver  getResourceResolver(String url) {
    String scheme = "app:" ;
    int index  = url.indexOf(":") ;
    if(index > 0) scheme = url.substring(0, index + 1) ;
    //System.out.println("===>> GET" + url);
    return resolvers_.get(scheme) ; 
  }
  
  public  void addResourceResolver(ResourceResolver resolver) {
    resolvers_.put(resolver.getResourceScheme(), resolver) ;
  }
  
  public URL getResource(String url) throws Exception {
    return  getResourceResolver(url).getResource(url);
  }
  
  public InputStream getInputStream(String url) throws Exception  {
    return  getResourceResolver(url).getInputStream(url);
  }
  
  public List<URL> getResources(String url) throws Exception {
    return  getResourceResolver(url).getResources(url);
  }
  
  public List<InputStream> getInputStreams(String url) throws Exception  {
    return  getResourceResolver(url).getInputStreams(url);
  }
  
  public boolean isModified(String url, long lastAccess) {
    return  getResourceResolver(url).isModified(url, lastAccess);
  }
  
  public boolean isResolvable(String url) {
    return getResourceResolver(url) != null  ;
  }
  
  public String getResourceIdPrefix() {  return Integer.toString(hashCode()) ; }
  
  public String createResourceId(String url) {  return hashCode() + ":" +  url ; }

  public String getResourceScheme() { return "app:" ;}
}
