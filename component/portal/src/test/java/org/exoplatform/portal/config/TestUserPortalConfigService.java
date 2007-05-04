/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.test.BasicTestCase;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 23, 2006
 */
public class TestUserPortalConfigService extends UserPortalServiceTestBase {
  
  private UserPortalConfigService service_; 
  
  @SuppressWarnings("unused")
  
  public TestUserPortalConfigService(String name){
    super(name);
  }
  
  public void setUp() throws Exception {
    if(service_ != null) return;
    PortalContainer manager  = PortalContainer.getInstance();      
    service_ = (UserPortalConfigService) manager.getComponentInstanceOfType(UserPortalConfigService.class) ;      
  }
  
  public void teaDown() throws Exception {    
  }
  
  public void testUserPortalConfigService() throws Exception {
    UserPortalConfig userPortalConfig = service_.getUserPortalConfig("site" ,"exo");
    PortalConfig portalConfig = userPortalConfig.getPortalConfig();
    
    assertTrue(portalConfig != null);
    assertEquals(portalConfig.getAccessGroup().length, 1);
    assertEquals(portalConfig.getTitle(), "Portal Site");
    
    assertEquals(userPortalConfig.getNavigations().size(), 1);
    
    userPortalConfig = service_.getUserPortalConfig("site" ,"exoadmin");
    assertEquals(userPortalConfig.getNavigations().size(), 1);
  }
 
}
