/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.test;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.SharedNavigation;
import org.exoplatform.portal.config.SharedPortal;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
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
  private PortalDAO pservice_;
  
  public TestUserPortalConfigService(String name){
    super(name);
  }
  
  public void setUp() throws Exception {
    if(service_ != null) return;
    PortalContainer manager  = PortalContainer.getInstance();      
    service_ = (UserPortalConfigService)
          manager.getComponentInstanceOfType(UserPortalConfigService.class) ;      
    pservice_ = (PortalDAO) manager.getComponentInstanceOfType(PortalDAO.class) ;    
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
    
    addSharedPortal(Group1, "exoportal", memtype1, 1);
    addSharedNavigation(Group1, "exoportal", memtype1, 1);
    
    SharedPortal sharedPortal = service_.getSharedConfigDAO().getSharedPortal("/"+Group1);
    assertTrue("expect shared portal for /Group1",  sharedPortal != null) ;
    assertEquals("Shared portal is exoportal : ", sharedPortal.getPortal(), "exoportal");
   
    UserPortalConfig userConfig = service_.computeUserPortalConfig(username1, username1);
    assertEquals(userConfig.getPortalConfig().getOwner(), username1);
//    assertEquals("Expect total navigation is 2", userConfig.getNavigations().size() , 2);    
    assertEquals("Expect the first navigation's owner is exo",
                 userConfig.getNavigations().get(0).getOwner(), "exo");
    assertEquals("Expect the second navigation's owner is exoportal",
                userConfig.getNavigations().get(1).getOwner(), "exoportal");
    
    sharedPortal = service_.getSharedConfigDAO().getSharedPortal("/"+Group2);
    assertTrue("expect no shared portal for /Group2",  sharedPortal == null) ;
    
    addSharedPortal(Group2, "exoportal", memtype1, 1);
    addSharedNavigation(Group2, "exoportal", memtype1, 1);
    
    sharedPortal = service_.getSharedConfigDAO().getSharedPortal("/"+Group2);
    assertTrue("expect shared portal for /Group2 ",  sharedPortal != null) ;    
    
    userConfig = service_.computeUserPortalConfig(username1, username1);
    assertEquals("Expect total navigation for user 1 is 3", userConfig.getNavigations().size(), 3); 
    
    SharedNavigation sharedNav = service_.getSharedConfigDAO().getSharedNavigation("/"+Group1);
    assertTrue("Exist share navigation for group1", sharedNav != null);
    service_.getSharedConfigDAO().removeSharedNavigation(sharedNav);
    sharedNav = service_.getSharedConfigDAO().getSharedNavigation("/"+Group1);
    assertTrue("share navigation for group1 was removed ", sharedNav == null);
    
    userConfig = service_.computeUserPortalConfig(username1, username1);
    assertEquals(userConfig.getNavigations().size(), 2); 
    
    sharedPortal = service_.getSharedConfigDAO().getSharedPortal("/"+Group2);
    service_.getSharedConfigDAO().removeSharedPortal(sharedPortal);
    sharedPortal = service_.getSharedConfigDAO().getSharedPortal("/"+Group2);
    assertTrue("share portal for group2 was removed ", sharedPortal == null);
    
    userConfig = service_.computeUserPortalConfig(username2, username2);
    assertEquals(userConfig.getPortalConfig().getOwner(), "exo");
    
    System.out.println(userConfig.getPortalConfig().getOwner());
    System.out.println(userConfig.getNavigations().size());
    System.out.println("\n\n\n=================================> End test\n");
  }
  
  
  private void addSharedNavigation(
      String group, String portal,String membership, int priority) throws Exception {
    SharedNavigation cn = new SharedNavigation();
    cn.setGroupId("/"+group) ;
    cn.setNavigation(portal) ;
    cn.setMembership(membership) ;
    cn.setPriority(priority) ;
    service_.getSharedConfigDAO().addSharedNavigation(cn) ;
  } 
  
  private void addSharedPortal(
      String group, String portal,String membership, int priority) throws Exception {
    SharedPortal cp = new SharedPortal();
    cp.setGroupId("/"+group) ;
    cp.setPortal(portal) ;
    cp.setMembership(membership) ;
    cp.setPriority(priority) ;    
    service_.getSharedConfigDAO().addSharedPortal(cp) ;
  }    
}
