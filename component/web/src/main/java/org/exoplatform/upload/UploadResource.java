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
package org.exoplatform.upload;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class UploadResource {
  
  final static public int UPLOADING_STATUS = 0 ; 
  final static public int UPLOADED_STATUS = 1 ; 
  final static public int FAILED_STATUS = 2 ;
  
  private String uploadId_ ;
  private String fileName_ ;
  private String mimeType_ ;
  private String storeLocation_ ;
  private double uploadedSize_ = 0 ;
  private double estimatedSize_ = 0 ;
  //private int limitMB_ = UploadService.uploadLimitMB_;
  private int status_ = UPLOADING_STATUS ;
  
  public UploadResource(String uploadId){
    uploadId_ = uploadId ;
  }
  
  public UploadResource(String uploadId, String fileName) {
    fileName_ = fileName ;
    uploadId_ = uploadId ;
  }

  public String getUploadId() { return uploadId_ ; }
 
  public String getFileName() {  return fileName_ ; } 
  public void setFileName(String fileName){ fileName_ = fileName; }
  
  public String getMimeType() { return mimeType_ ; }
  public void setMimeType(String mimeType) { mimeType_ = mimeType; }
  
  public String getStoreLocation() { return storeLocation_ ; }
  public void   setStoreLocation(String path) { storeLocation_ = path ; }
  
  public double  getUploadedSize() { return uploadedSize_ ; }
  public void addUploadedBytes(double size) { uploadedSize_ += size ; }
  
  public double getEstimatedSize() { return estimatedSize_ ; }
  public void setEstimatedSize(double size) { estimatedSize_ = size ; }
  
  public int getStatus() { return status_ ; }
  public void setStatus(int status) { status_ = status ; }

	//public int getLimitMB() { return limitMB_; }
	//
	//public void setLimitMB(int limitMB_) { this.limitMB_ = limitMB_; }

}