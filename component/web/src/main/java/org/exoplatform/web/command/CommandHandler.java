/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
public class CommandHandler extends WebRequestHandler {
  
  public String[] getPath() { return new String[] { "/command"} ; }
  
  public void execute(WebAppController app,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    System.out.println("IN COMMAND " + req.getServletPath());
    System.out.println("IN COMMAND " + req.getPathInfo());
    Map props = req.getParameterMap() ;
  }
  
  
  /**
   * This method should use the java reflection to create the command object according to the command
   * type, then  populate the command  properties  
   * 
   * @param command  The command class type 
   * @param props    list of the properties that should be set in the command object
   * @return         The command object instance
   * @throws Exception
   */
  public Command createCommand(String command, Map props) throws Exception  {
    return null ;
  }

}