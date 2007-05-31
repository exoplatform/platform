/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 22, 2007
 */
abstract public class Command {
  
  public String[] getRequiredMemberships() { return null ; }

  abstract public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception ;
}