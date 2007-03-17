/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.download.DownloadResource;
import org.exoplatform.download.DownloadService;
import org.exoplatform.portal.application.PortalApplication;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 9, 2006  
 */
public class DownloadRequestHandler implements RequestHandler {
  
  public void execute(PortalApplication app, HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setHeader("Cache-Control", "private max-age=600, s-maxage=120");
    String resourceId = request.getParameter("resourceId") ;      
    ExoContainer container = app.getApplicationServiceContainer();
    DownloadService dservice = (DownloadService)container.getComponentInstanceOfType(DownloadService.class) ;
    DownloadResource dresource = dservice.getDownloadResource(resourceId);
    if(dresource == null){
      response.setContentType("text/plain") ;                
      response.getWriter().write("NO DOWNDLOAD RESOURCE CONTENT  OR YOU DO NOT HAVE THE RIGHT TO ACCESS THE CONTENT") ;
      return;
    }
    if(dresource.getDownloadName() != null ){
      response.setHeader("Content-Disposition", "attachment;filename="+dresource.getDownloadName());
    }
    response.setContentType(dresource.getResourceMimeType()) ;
    InputStream is = dresource.getInputStream() ;
    byte[] buf = new byte[is.available()] ;
    is.read(buf) ;
    response.setContentType(dresource.getResourceMimeType()) ;          
    response.getOutputStream().write(buf) ;        
    is.close();
  }  

}