/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.download;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PortalContainerInfo;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class DownloadService {
  
  private Cache downloadResources_ ;
  private Map<String, DownloadResource>  defaultResources_ ;
  private PortalContainerInfo pinfo_ ;
  
  public DownloadService(PortalContainerInfo pinfo, InitParams params) throws Exception {
    int maxSize = Integer.parseInt(params.getValueParam("download.resource.cache.size").getValue()) ;
    downloadResources_ = new Cache(maxSize) ;
    defaultResources_ = new HashMap<String, DownloadResource>() ;
    pinfo_ =  pinfo ;
  }
  
  public void addDefaultDownloadResource(DownloadResource resource) {
    defaultResources_.put(resource.getDownloadType(), resource) ; 
  }
  
  public String addDownloadResource(DownloadResource resource) {
    String id = Integer.toString(resource.hashCode()) ;
    if(resource.getDownloadType() != null) id = resource.getDownloadType() + ":/" + id ;
    downloadResources_.put(id, resource) ;   
    return id;
  }
  
  public DownloadResource getDownloadResource(String id) {
    DownloadResource resource = downloadResources_.get(id) ;    
    if(resource != null)   {
      downloadResources_.remove(id) ;
      return resource ;
    }
    String[] temp = id.split(":") ;
    if(temp.length > 1) {
      String downloadType =  temp[0] ;
      resource = defaultResources_.get(downloadType) ;
    }
    return resource ;
  }
  
  public String getDownloadLink(String id)  {
    return "/" + pinfo_.getContainerName() + "/command?"
           + "type=org.exoplatform.web.command.handler.DownloadHandler&resourceId=" + id  ;
  }
  
  @SuppressWarnings("serial")
  static class Cache extends LinkedHashMap<String, DownloadResource> {
    
    int maxSize_ = 100 ;
    
    public Cache(int maxSize) {
      maxSize_  = maxSize ;
    }
    
    @SuppressWarnings("unused")
    protected boolean removeEldestEntry(Map.Entry eldest) {
      return size() > maxSize_ ;
    }
  }
}