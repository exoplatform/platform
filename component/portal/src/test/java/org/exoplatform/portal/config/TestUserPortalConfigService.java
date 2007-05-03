/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.organization.OrganizationService;

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
    service_ = (UserPortalConfigService)
          manager.getComponentInstanceOfType(UserPortalConfigService.class) ;      
    orgService_ = 
      (OrganizationService) manager.getComponentInstanceOfType(OrganizationService.class);    
    hservice_ = 
      (HibernateService)manager.getComponentInstanceOfType(HibernateService.class) ;
  }
  
  public void teaDown() throws Exception {    
  }
  
  public void testUserPortalConfigService() throws Exception {
    System.out.println("\n\n\n=================================> Test\n");    
    prepareOrganizationData() ;
    orgService_.getMembershipHandler().linkMembership(user1, group1, mType1,true) ;    
    orgService_.getMembershipHandler().linkMembership(user1, group2, mType1,true) ;
    orgService_.getMembershipHandler().linkMembership(user2, group2, mType1,true) ;
    orgService_.getMembershipHandler().linkMembership(user2, group2, mType2,true) ;
   
  }
 
}
