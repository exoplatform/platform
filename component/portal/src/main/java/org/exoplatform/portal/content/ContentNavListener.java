/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.content.model.ContentNavigation;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 *          tung.pham@exoplatform.com
 * Jun 1, 2007  
 */
public class ContentNavListener extends UserEventListener {

  public ContentNavListener() throws Exception {
  }
  
  public void preDelete(User user) throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    ContentDAO contentService = (ContentDAO) container.getComponentInstanceOfType(ContentDAO.class) ;
    String owner = user.getUserName() ;
    ContentNavigation content = contentService.get(owner) ;
    if (content != null) contentService.remove(owner) ;
  }
  

}
