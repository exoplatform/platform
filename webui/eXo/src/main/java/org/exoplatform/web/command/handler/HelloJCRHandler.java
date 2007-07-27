/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;


import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Ba Uoc
 *          thuy.le@exoplatform.com
 * July 24, 2007
 */
public class HelloJCRHandler extends Command {
  
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    res.setContentType("text/xml") ;
    PrintWriter out = res.getWriter() ;
    out.println("Hello from server") ;
    System.out.println("Client request") ;
  }
}