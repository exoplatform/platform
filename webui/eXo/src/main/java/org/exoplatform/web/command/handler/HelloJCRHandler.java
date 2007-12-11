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