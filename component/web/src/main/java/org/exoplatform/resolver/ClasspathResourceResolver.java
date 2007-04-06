/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
  
  public String getResourceScheme() {  return "classpath:/" ; }
  
}