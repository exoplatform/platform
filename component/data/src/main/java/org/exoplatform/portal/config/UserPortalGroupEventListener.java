/**
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.
 * Please look at license.txt in info directory for more license detail.
 **/
package org.exoplatform.portal.config;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupEventListener;
/**
 * Author : Tuan Nguyen
 *          tuan08@groups.sourceforge.net
 * Wed, Feb 18, 2004 @ 21:33 
 */
public class UserPortalGroupEventListener extends GroupEventListener {

  private SharedConfigDAO sharedConfigDAO_;
  
  public UserPortalGroupEventListener(SharedConfigDAO cservice) {
    sharedConfigDAO_ = cservice ;
  }
  
  public void postDelete(Group group) throws Exception {
    SharedPortal cp = sharedConfigDAO_.getSharedPortal(group.getId()) ;
    if(cp != null) sharedConfigDAO_.removeSharedPortal(cp) ;

    SharedNavigation cn = sharedConfigDAO_.getSharedNavigation(group.getId()) ;
    if(cn != null) sharedConfigDAO_.removeSharedNavigation(cn) ;
  }
  
}