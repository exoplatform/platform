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
