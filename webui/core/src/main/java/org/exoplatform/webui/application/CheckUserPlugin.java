/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

import org.exoplatform.portal.config.UserACL.UserACLPlugin;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 7, 2007
 */
public class CheckUserPlugin extends UserACLPlugin {

  public boolean hasRoleAdmin() {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    return context.isUserInRole("admin");
  }

}
