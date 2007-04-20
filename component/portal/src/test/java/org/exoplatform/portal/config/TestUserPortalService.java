/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 2, 2005
 */
public class TestUserPortalService extends BasicTestCase {
  
  public TestUserPortalService(String name) {
    super(name);    
  }
  
  public void testService() throws Exception {
    PortalContainer pcontainer = PortalContainer.getInstance() ;
    UserPortalConfigService service = 
      (UserPortalConfigService) pcontainer.getComponentInstanceOfType(UserPortalConfigService.class);
    assertTrue("Service Cannot be null",  service != null) ;
    assertPortalConfigOperation(service, "??") ;
  }
  
  void assertPortalConfigOperation(UserPortalConfigService service, String portalName) throws Exception {
    
  }
  
  void assertPageOperation(UserPortalConfigService service, String portalName) throws Exception {
    
  }
  
  void assertPageNavigation(UserPortalConfigService service, String portalName) throws Exception {
    
  }
}
