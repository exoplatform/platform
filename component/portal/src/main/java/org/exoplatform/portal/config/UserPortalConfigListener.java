/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 29, 2007  
 */
public class UserPortalConfigListener extends UserEventListener {
  
  public UserPortalConfigListener() throws Exception {
  }

  public void preDelete(User user) throws Exception {
    PortalContainer container  = PortalContainer.getInstance() ;
    DataStorage dataStorage = (DataStorage)container.getComponentInstanceOfType(DataStorage.class) ;
    System.out.println("\n\n == > prepare remove user "+dataStorage+" : "+user.getUserName()+"\n\n");
    // user data Storage get navigation and page then remove it
  }

}
