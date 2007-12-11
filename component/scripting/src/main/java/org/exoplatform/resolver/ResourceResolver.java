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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by The eXo Platform SAS
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
  
  public byte[] getResourceContentAsBytes(String url) throws Exception {
    InputStream is = getInputStream(url) ;
    BufferedInputStream buffer = new BufferedInputStream(is);    
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] data  = new byte[buffer.available()];      
    int available = -1;
    while( (available = buffer.read(data)) > -1){
      output.write(data, 0, available);
    }   
    return output.toByteArray();
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