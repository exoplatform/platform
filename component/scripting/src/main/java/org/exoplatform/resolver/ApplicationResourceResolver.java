/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.resolver;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;


/**
 * This class is an aggregation of ResourceResolver object and extends itself the ResourceResover class.
 * 
 * Hence every call to this ResourceResolver will in fact be delegated to one of the resolver it aggregates.
 * 
 * Created by The eXo Platform SAS
 * Oct 24, 2006
 */
public class ApplicationResourceResolver extends ResourceResolver {
  
  protected static Log log = ExoLogger.getLogger("portal:ApplicationResourceResolver");  
  
  private Map<String, ResourceResolver>  resolvers_ = new  HashMap<String, ResourceResolver>();
  
  /**
   * There are by default 2 resolvers already aggregated: 
   *  1) FileResourceResolver
   *  2) ClasspathResourceResolver
   */
  public ApplicationResourceResolver() {
    addResourceResolver(new FileResourceResolver()) ;
    addResourceResolver(new ClasspathResourceResolver()) ;
  }
  
  public ResourceResolver  getResourceResolverByScheme(String scheme) {
    return  resolvers_.get(scheme) ;
  }
  
  public ResourceResolver getResourceResolver(String url) {
    String scheme = "app:" ;
    int index  = url.indexOf(":") ;
    if(index > 0) scheme = url.substring(0, index + 1) ;
    if(log.isDebugEnabled())
      log.debug("Try to extract resource resolver for the url: " + url);	
    return resolvers_.get(scheme) ; 
  }
  
  public  void addResourceResolver(ResourceResolver resolver) {
    if(log.isDebugEnabled())
      log.debug("Add a resource resolver for the scheme: " + resolver.getResourceScheme());
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
