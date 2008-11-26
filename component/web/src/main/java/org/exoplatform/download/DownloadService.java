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
           + "type=org.exoplatform.web.command.handler.DownloadHandler&amp;resourceId=" + id  ;
  }
  
  @SuppressWarnings("serial")
  static class Cache extends LinkedHashMap<String, DownloadResource> {
    
    int maxSize_ = 100 ;
    
    public Cache(int maxSize) {
      maxSize_  = maxSize ;
    }
    
    @SuppressWarnings("unused")
    protected boolean removeEldestEntry(Map.Entry<String, DownloadResource> eldest) {
      return size() > maxSize_ ;
    }
  }
}