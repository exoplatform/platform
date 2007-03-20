/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.download;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class ClasspathDownloadResource extends DownloadResource{
  
  private String  resource_ ;
  
  public ClasspathDownloadResource(String resource, String resourceMimeType) {
    this(null, resource, resourceMimeType) ;
  }
  
  public ClasspathDownloadResource(String type, String resource, String resourceMimeType) {
    super(type, resourceMimeType) ;
    resource_ = resource ;
  }
  
  public InputStream getInputStream() throws IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    InputStream  is = cl.getResourceAsStream(resource_) ;    
    return is ;
  }
}