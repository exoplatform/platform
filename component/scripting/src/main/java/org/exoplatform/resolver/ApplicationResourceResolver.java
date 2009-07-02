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
package org.exoplatform.resolver;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.services.log.Log;
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
