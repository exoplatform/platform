/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.command.Command;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          thuy.le@exoplatform.com
 * Dec 9, 2006  
 */
public class WidgetHandler extends Command {
  
  private String componentId ;
  
  @SuppressWarnings("unused")
  public void execute(WebAppController controller,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    System.out.println("\n\n\n----------------------------->WidgetHandler");
  }
  
}