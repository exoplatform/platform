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
package org.exoplatform.web.command.handler;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      
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