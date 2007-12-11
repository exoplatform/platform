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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Mar 15, 2006
 */
public class FileResourceResolver extends ResourceResolver {
  
  static String FILE_PREFIX = "file:"  ;
  
  public URL getResource(String url) throws Exception {
    String path = removeScheme(url) ;
    File file = new File(path) ;
    if(file.exists() && file.isFile())  return file.toURL() ;
    return null ; 
  }
  
  public InputStream getInputStream(String url) throws Exception  {
    String path = removeScheme(url) ;
    File file = new File(path) ;
    if(file.exists() && file.isFile())  {
      FileInputStream is = new FileInputStream(file) ;
      return is;
    }
    return null ; 
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
    return path ;
  }
  
  public boolean isModified(String url, long lastAccess) {
    File file = new File(getRealPath(url)) ;
    if(file.exists() && file.lastModified() > lastAccess)   return true ;  
    return false ;
  }
  
  public String getResourceScheme() {  return "file:" ; }
  
}