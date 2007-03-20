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
abstract public class DownloadResource {
  
  private String downloadType_ ;
  
  private String downloadName_;
  
  private String resourceMimeType_ ;
  private long liveTime_ ;
  private int limit_ ;
  private int accessCounter_ ;
  
  public DownloadResource(String resourceMimeType) {
    this(null, resourceMimeType) ;
  }
  
  public DownloadResource(String downloadType, String resourceMimeType) {
    downloadType_ =  downloadType ;
    resourceMimeType_  = resourceMimeType;
  }
  
  public String getDownloadType() { return downloadType_ ; }
  
  public String getDownloadName() { return downloadName_ ; }
  
  public void setDownloadName( String name) { downloadName_ = name;}
  
  public String getResourceMimeType() { return resourceMimeType_ ; }
  
  public long getLiveTime() { return liveTime_ ; }
  public void setLiveTime(long t) { liveTime_ = t; }
  
  public int getLimit() { return limit_ ; }
  public void setLimit(int n) { limit_ = n ; }
  
  public int getAccessCounter() { return accessCounter_ ; }
  public void setAccessCounter(int c) { accessCounter_ = c ; }
  
  abstract public InputStream getInputStream() throws IOException ;
 
}