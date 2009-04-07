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
package org.exoplatform.webui.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SAS
 * Jun 6, 2006
 * 
 * Represents an upload form
 */
@ComponentConfig(template = "system:/groovy/webui/form/UIFormUploadInput.gtmpl")    
public class UIFormUploadInput extends UIFormInputBase<String> {
  /**
   * The current upload id
   */
  private String uploadId_ ;
  /**
   * The resource to upload
   */
  private UploadResource uploadResource_ ;
  
  /**
   * The auto upload feature
   */
  private boolean isAutoUpload = false;
  
  /**
   * The maximum size of the file to upload, in MB
   */
  //private int limitMB_;
  
  public UIFormUploadInput(String name, String bindingExpression) {
    super(name, bindingExpression, String.class);
    uploadId_ = Integer.toString(Math.abs(hashCode())) ;
    UploadService service = getApplicationComponent(UploadService.class) ;
    service.addUploadLimit(uploadId_, null); // Use the limit set by the service. Warning, the service can allow no size limit (value to 0)
    setComponentConfig(UIFormUploadInput.class, null) ;
  }
  
  public UIFormUploadInput(String name, String bindingExpression, int limit) {
    super(name, bindingExpression, String.class);
    uploadId_ = Integer.toString(Math.abs(hashCode())) ;
    UploadService service = getApplicationComponent(UploadService.class) ;
    service.addUploadLimit(uploadId_, Integer.valueOf(limit)); // Use the limit set by constructor.
    setComponentConfig(UIFormUploadInput.class, null) ;
  }
  
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    uploadResource_ = null ;
    boolean hasUpload = "true".equals(input) ;
    if(hasUpload) {
      UploadService service = getApplicationComponent(UploadService.class) ;
      uploadResource_ = service.getUploadResource(uploadId_) ;
    }
  }
  
  public InputStream getUploadDataAsStream() throws Exception {
    if(uploadResource_ == null) return null;
    File file = new File(uploadResource_.getStoreLocation());
    return new FileInputStream(file);
  }
  
  @Deprecated
  // use getUploadDataAsStream() menthod instead
  public byte[] getUploadData() throws Exception {
    if(uploadResource_ == null) return null;
    File file = new File(uploadResource_.getStoreLocation());
    FileInputStream inputStream =  new FileInputStream(file);
    FileChannel fchan = inputStream.getChannel();
    long fsize = fchan.size();       
    ByteBuffer buff = ByteBuffer.allocate((int)fsize);        
    fchan.read(buff);
    buff.rewind();      
    byte[] data = buff.array();      
    buff.clear();      
    fchan.close();        
    inputStream.close();       
    return data;
  }
  
  public String getUploadId() { return uploadId_; }
  
  public String getActionUpload(){
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    WebuiRequestContext pcontext = (WebuiRequestContext)context.getParentAppRequestContext();
    if(pcontext == null) pcontext = context;
    String uploadAction = pcontext.getRequestContextPath() + "/command?";
    uploadAction += "type=org.exoplatform.web.command.handler.UploadHandler";
    uploadAction += "&uploadId=" + uploadId_+"&action=upload" ;
    return uploadAction;
  }

  public UploadResource getUploadResource() { return uploadResource_; }

  public boolean isAutoUpload() {return isAutoUpload; }

  public void setAutoUpload(boolean isAutoUpload) {this.isAutoUpload = isAutoUpload;}
  
//  public void setLimit(int size) { limitMB_ = size; }
//  
//  public int getLimit() { return limitMB_; }

}