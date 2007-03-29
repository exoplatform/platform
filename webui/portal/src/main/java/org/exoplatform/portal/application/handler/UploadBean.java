/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 29, 2007  
 */
public  class UploadBean {
  private String uploadId;
  private double percent;
  
  public UploadBean() {      
  }
  public UploadBean(String uploadId, int percent) {
    this.uploadId = uploadId;     
    this.percent = percent;
  }
  
  public double getPercent() {
    return percent;
  }
  public void setPercent(double percent) {
    this.percent = percent;
  }
  public String getUploadId() {
    return uploadId;
  }
  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }
  
  
}
