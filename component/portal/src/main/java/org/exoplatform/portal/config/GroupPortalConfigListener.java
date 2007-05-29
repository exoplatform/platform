/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupEventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * May 29, 2007  
 */
public class GroupPortalConfigListener extends GroupEventListener {
  
//  private DataStorage dataStorage_;  
  
  public GroupPortalConfigListener() throws Exception {
//    dataStorage_ = dataStorage; 
  }

  public void preDelete(Group group) throws Exception {
    System.out.println("\n\n == > prepare remove group "+group.getId()+"\n\n");
    // user data Storage get navigation and page then remove it
  }

}
