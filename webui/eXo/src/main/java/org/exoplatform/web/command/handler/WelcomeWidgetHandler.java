/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.UserWidgetStorage;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Dec 9, 2006  
 */
public class WelcomeWidgetHandler extends Command {

  private String uploadId ;

  @SuppressWarnings("unused")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    try{
      PortalContainer container  = PortalContainer.getInstance();
      
      UploadService uploadService = (UploadService)container.getComponentInstanceOfType(UploadService.class) ;
      UploadResource upResource = uploadService.getUploadResource(uploadId);
      if(upResource == null) return ;
      
      String instantId = "avatar";
      String widgetType = "WelcomeWidget";
      String userName = req.getRemoteUser();
      System.out.println("========> upRsource: " + upResource);
      UserWidgetStorage service = 
        (UserWidgetStorage)container.getComponentInstanceOfType(UserWidgetStorage.class) ;
      File file = new File(upResource.getStoreLocation());
      FileInputStream inputStream =  new FileInputStream(file);
      FileChannel fchan = inputStream.getChannel();
      long fsize = fchan.size();       
      ByteBuffer buff = ByteBuffer.allocate((int)fsize);        
      fchan.read(buff);
      buff.rewind();      
      byte[] data = buff.array();
      service.save(userName, widgetType, instantId, data);
      buff.clear();      
      fchan.close();        
      inputStream.close();  
     
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Throwable  e) {e.printStackTrace();
    }
  }
}