/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.json.JSONService;
import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 9, 2006  
 */
public class UploadRequestHandler implements RequestHandler {
  
  public void execute(PortalApplication app, HttpServletRequest req, HttpServletResponse res) throws Exception {
    ExoContainer container = app.getApplicationServiceContainer() ;
    UploadService service = (UploadService)container.getComponentInstanceOfType(UploadService.class) ;
    String actionValue  = req.getParameter("action");
    if(actionValue == null ||  actionValue.length() < 1) return;
    UploadServiceAction  action = UploadServiceAction.valueOf(actionValue.toUpperCase());
    if(action == UploadServiceAction.PROGRESS){
      Writer  writer = res.getWriter();
      String [] uploadId = req.getParameterValues("uploadId");
      if(uploadId == null) return;        
      StringBuilder value = new StringBuilder();
//      value.append("{percent : {");
      JSONService jsonService = null;
      jsonService = new JSONService();
          
      ArrayList<UploadBean> uploadBeans = new ArrayList<UploadBean>();
      
      for(int i=0; i<uploadId.length; i++){       
        UploadResource upResource = service.getUploadResource(uploadId[i]);
        if(upResource == null) continue;
        double percent =  100;
        if(upResource.getStatus() == UploadResource.UPLOADING_STATUS){
          percent = (upResource.getUploadedSize()*100)/upResource.getEstimatedSize();
        }
        UploadBean uploadBean = new UploadBean();
        uploadBean.setUploadId(uploadId[i]);
        uploadBean.setPercent(percent);
        uploadBeans.add(uploadBean);
        StringBuilder tmp = new StringBuilder(); 
        jsonService.toJSONScript(uploadBean, tmp, 0);
        System.out.println("\n********************************" + tmp + "\n*************************\n" );
      }       
      jsonService.toJSONScript(uploadBeans, value, 0);
      System.out.println("\n+++++++++++++\n" + value + "\n++++++++++++++\n" );
      writer.append(value);
      

    }else if(action == UploadServiceAction.UPLOAD){
      service.createUploadResource(req) ;       
    }else if(action == UploadServiceAction.DELETE){
      String uploadId =  req.getParameter("uploadId") ;
      service.removeUpload(uploadId);
    }else if(action == UploadServiceAction.ABORT){
      String uploadId =  req.getParameter("uploadId") ;
      UploadResource upResource = service.getUploadResource(uploadId);
      if(upResource != null) upResource.setStatus(UploadResource.UPLOADED_STATUS) ;
    }   
  }
  
   
  static enum  UploadServiceAction {
    PROGRESS, UPLOAD, DELETE, ABORT
  }
}

/*

package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.application.PortalApplication;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;


public class UploadRequestHandler implements RequestHandler {
  
  public void execute(PortalApplication app, HttpServletRequest req, HttpServletResponse res) throws IOException {
    ExoContainer container = app.getApplicationServiceContainer() ;
    UploadService service = (UploadService)container.getComponentInstanceOfType(UploadService.class) ;
    String actionValue  = req.getParameter("action");
    if(actionValue == null ||  actionValue.length() < 1) return;
    UploadServiceAction  action = UploadServiceAction.valueOf(actionValue.toUpperCase());
    if(action == UploadServiceAction.PROGRESS){
      Writer  writer = res.getWriter();
      String [] uploadId = req.getParameterValues("uploadId");
      if(uploadId == null) return;        
      StringBuilder value = new StringBuilder();
      value.append("{percent : {");
      for(int i=0; i<uploadId.length; i++){
        UploadResource upResource = service.getUploadResource(uploadId[i]);
        if(upResource == null) continue;
        double percent =  100;
        if(upResource.getStatus() == UploadResource.UPLOADING_STATUS){
          percent = (upResource.getUploadedSize()*100)/upResource.getEstimatedSize();
        }
        value.append("\"").append(uploadId[i]).append("\":");
        value.append("\"").append((int)percent).append("\"");
        if(i < uploadId.length - 1) value.append(',');
      }       
      value.append("}}");
      writer.append(value);        
    }else if(action == UploadServiceAction.UPLOAD){
      service.createUploadResource(req) ;       
    }else if(action == UploadServiceAction.DELETE){
      String uploadId =  req.getParameter("uploadId") ;
      service.removeUpload(uploadId);
    }else if(action == UploadServiceAction.ABORT){
      String uploadId =  req.getParameter("uploadId") ;
      UploadResource upResource = service.getUploadResource(uploadId);
      if(upResource != null) upResource.setStatus(UploadResource.UPLOADED_STATUS) ;
    }      
    
  }
  
  static enum  UploadServiceAction {
    PROGRESS, UPLOAD, DELETE, ABORT
  }
} 
*/