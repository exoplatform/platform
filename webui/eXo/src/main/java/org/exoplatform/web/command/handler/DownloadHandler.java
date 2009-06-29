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

import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
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
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    DownloadService dservice = (DownloadService)container.getComponentInstanceOfType(DownloadService.class) ;
    DownloadResource dresource = dservice.getDownloadResource(resourceId);
    if(dresource == null){
      res.setContentType("text/plain") ;                
      res.getWriter().write("NO DOWNDLOAD RESOURCE CONTENT  OR YOU DO NOT HAVE THE RIGHT TO ACCESS THE CONTENT") ;
      return;
    }
    String userAgent = req.getHeader("User-Agent");
    if(dresource.getDownloadName() != null ){
      if (userAgent != null && userAgent.contains("MSIE")) {
        res.setHeader("Content-Disposition", "attachment;filename=\""+URLEncoder.encode(dresource.getDownloadName(), "UTF-8")+"\"");
      } else {
        res.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + URLEncoder.encode(dresource.getDownloadName(), "UTF-8") + "");
      }
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