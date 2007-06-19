/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.download.DownloadResource;
import org.exoplatform.download.DownloadService;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : LeBienThuy  
 *          thuy.le@exoplatform.com
 * Dec 9, 2006  
 */
public class DownloadHandler extends Command {
  
  private String resourceId;

  @SuppressWarnings("unused")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    res.setHeader("Cache-Control", "private max-age=600, s-maxage=120");
    ExoContainer container =  PortalContainer.getInstance();
    DownloadService dservice = (DownloadService)container.getComponentInstanceOfType(DownloadService.class) ;
    DownloadResource dresource = dservice.getDownloadResource(resourceId);
    if(dresource == null){
      res.setContentType("text/plain") ;                
      res.getWriter().write("NO DOWNDLOAD RESOURCE CONTENT  OR YOU DO NOT HAVE THE RIGHT TO ACCESS THE CONTENT") ;
      return;
    }
    if(dresource.getDownloadName() != null ){
      res.setHeader("Content-Disposition", "attachment;filename="+dresource.getDownloadName());
    }
    res.setContentType(dresource.getResourceMimeType()) ;
    InputStream is = dresource.getInputStream() ;
    byte[] buf = new byte[is.available()] ;
    is.read(buf) ;
    res.setContentType(dresource.getResourceMimeType()) ;          
    res.getOutputStream().write(buf) ;        
    is.close();
  }

  public String getResourceId() { return resourceId; }  

}