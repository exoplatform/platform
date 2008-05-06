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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Mar 15, 2006
 */
public class ClasspathResourceResolver extends ResourceResolver {
  
  public URL getResource(String url) throws Exception {
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    return cl.getResource(removeScheme(url));  
  }
  
  public InputStream getInputStream(String url) throws Exception  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    return cl.getResourceAsStream(removeScheme(url)); 
  }
  
  public List<URL> getResources(String url) throws Exception {
    ArrayList<URL>  urlList = new ArrayList<URL>() ;
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    Enumeration<URL> e = cl.getResources(removeScheme(url));
    while(e.hasMoreElements()) urlList.add(e.nextElement()) ;
    return urlList ;
  }

  public List<InputStream> getInputStreams(String url) throws Exception {
    ArrayList<InputStream>  inputStreams = new ArrayList<InputStream>() ;
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    Enumeration<URL> e = cl.getResources(removeScheme(url));
    while(e.hasMoreElements()) inputStreams.add(e.nextElement().openStream()) ;
    return inputStreams ;
  }

  @SuppressWarnings("unused")
  public boolean isModified(String url, long lastAccess) {
    return false ;
  }
  
  public String getResourceScheme() {  return "classpath:" ; }
  
}