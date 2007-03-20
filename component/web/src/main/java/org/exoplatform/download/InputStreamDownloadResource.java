/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.download;

import java.io.InputStream;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class InputStreamDownloadResource extends DownloadResource {
  private InputStream is_ ;
  
  public InputStreamDownloadResource(InputStream is, String resourceMimeType) {
    this(null, is, resourceMimeType) ;
  }
  
  public InputStreamDownloadResource(String downloadType, InputStream is, String resourceMimeType) {
    super(downloadType,resourceMimeType) ;
    is_ = is ;
  }
  
  public InputStream getInputStream()  {  return is_ ; }
}