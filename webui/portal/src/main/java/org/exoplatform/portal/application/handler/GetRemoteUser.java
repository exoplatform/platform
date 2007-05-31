/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application.handler;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 31, 2007  
 */
public class GetRemoteUser extends Command {
  
  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    Writer writer = res.getWriter();
    
    try {
      writer.append(req.getRemoteUser());
    } catch (Exception e) {
      e.printStackTrace() ;
      throw new IOException(e.getMessage());
    }
  }
}
