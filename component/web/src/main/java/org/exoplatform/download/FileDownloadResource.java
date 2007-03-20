/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.download;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class FileDownloadResource extends DownloadResource{
  
  private String path_ ;
  
  public FileDownloadResource(String path, String resourceMimeType) {
    this(null, path, resourceMimeType) ;
  }
  
  public FileDownloadResource(String downloadType, String path, String resourceMimeType) {
    super(downloadType,resourceMimeType) ;
    path_ = path ;
  }
  
  public InputStream getInputStream() throws IOException {
    FileInputStream is = new FileInputStream(path_) ;
    return is ;
  }
}