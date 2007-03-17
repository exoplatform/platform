/**
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */
package org.exoplatform.services.portletregistery.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletPermission;
import org.exoplatform.services.portletregistery.PortletRegisteryException;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
import org.exoplatform.test.BasicTestCase;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 16 juin 2004
 */
public class TestPortletRegistery extends BasicTestCase {

  static protected PortletRegisteryService service_ ;

  public TestPortletRegistery(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    if (service_ != null) return ;
    PortalContainer manager  = PortalContainer.getInstance();
    service_ = (PortletRegisteryService) manager.getComponentInstanceOfType(PortletRegisteryService.class) ;
  }

  @SuppressWarnings("unchecked")
  public void tearDown() throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();
    List<HibernateService> list = manager.getComponentInstancesOfType(HibernateService.class) ;
    for(HibernateService hservice : list) hservice.closeSession() ;
  }

  public void testPortletService() throws Exception {
    List portletCategoriesList = service_.getPortletCategories() ;
    assertTrue(portletCategoriesList.isEmpty());
    PortletCategory category = createPortletCategory("portletCategory", "this is a test") ;
    assertEquals("check portletCategory name: ", "portletCategory", category.getPortletCategoryName()) ;
    assertEquals("check portletCategory desc: ", "this is a test", category.getDescription()) ;
    portletCategoriesList = service_.getPortletCategories() ;
    assertEquals("check number of portlet categories: ", 1 , portletCategoriesList.size()) ;

    Portlet portlet = createPortlet(category, "portlet", "this is a test") ;
    assertEquals("check portlet name: ", "portlet", portlet.getPortletName()) ;
    assertEquals("check portlet desc: ", "this is a test", portlet.getDescription()) ;

    PortletPermission permission = createPortletPermission(portlet, "member:/user", "this is a test") ;
    assertEquals("check portletRole : ", "member:/user", permission.getPermissionExpression()) ;

    portlet = service_.getPortlet(portlet.getId()) ;
    permission = service_.getPortletPermission(permission.getId()) ;

    permission = service_.getPortletPermission(permission.getId()) ;
    portlet = service_.getPortlet(portlet.getId()) ;

    try {
      service_.removePortletPermission(permission.getId()) ;
      permission = service_.getPortletPermission(permission.getId()) ;
    } catch (PortletRegisteryException ex) {
      assertEquals("check portletRole not found exception ",
          PortletRegisteryException.PORTLET_ROLE_NOT_FOUND, ex.getErrorCode()) ;
    }

    try {
      service_.removePortlet(portlet.getId()) ;
      portlet = service_.getPortlet(portlet.getId()) ;
    } catch (PortletRegisteryException ex) {
      assertEquals("check portlet not found exception ",
          PortletRegisteryException.PORTLET_NOT_FOUND, ex.getErrorCode()) ;
    }

    portlet = createPortlet(category, "portlet", "this is a test") ;
    String portletId = portlet.getId();
    String portletRoleId = "";
    for (int i = 0; i < 25; i++) {
      portletRoleId = createPortletPermission(portlet, "membership" + i + ":/user", "this is a test").getId() ;
    }
    List portletRoleList = service_.getPortletPermissions(portlet.getId()) ;
    assertEquals("check number of portletRoles in portlet: ", 25 , portletRoleList.size()) ;

    try {
      service_.removePortletCategory(category.getId()) ;
      assertNull(service_.getPortletCategory(category.getId())) ;
    } catch (PortletRegisteryException ex) {
      assertEquals("check portletCategory not found exception ",
          PortletRegisteryException.PORTLET_CATEGORY_NOT_FOUND, ex.getErrorCode()) ;
    }

    //test cascade delete
    try{
      portlet = service_.getPortlet(portletId);
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }

    try {
      permission = service_.getPortletPermission(portletRoleId);
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }
  }

  public void testClearPortletRoles() throws Exception {
    PortletCategory cat = createPortletCategory("clearPortletCategory", "this is a test") ;
    Portlet portlet = createPortlet(cat, "portlet", "this is a test") ;
    PortletPermission portletRole1 = createPortletPermission(portlet, "member:/user", "this is a test") ;
    PortletPermission portletRole2 = createPortletPermission(portlet, "member:/admin", "this is a test") ;

    assertNotNull(service_.getPortletPermission(portletRole1.getId()));

    service_.clearPortletPermissions(portlet.getId());

    try {
      service_.getPortletPermission(portletRole1.getId());
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }

    try{
      service_.getPortletPermission(portletRole2.getId());
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }
  }

  /*
   public void testUpdateRoles() throws Exception {
   PortletCategory cat = createPortletCategory("portletCategory", "this is a test") ;
   Portlet portlet = createPortlet(cat, "portlet", "this is a test") ;
   createPortletPermission(portlet, "member:/user", "this is a test") ;
   createPortletPermission(portlet, "member:/admin", "this is a test") ;

   Collection newRoles = new ArrayList();
   newRoles.add("newRole1");
   newRoles.add("newRole2");
   newRoles.add("newRole3");

   service_.updatePortletRoles(portlet.getId(), newRoles);

   List roles = service_.getPortletRoles(portlet.getId());
   assertTrue(roles.size() == 3);
   for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
   PortletPermission ppermission = (PortletPermission) iterator.next();
   assertTrue(ppermission.getPermissionExpression().startsWith("newRole"));
   }
   }
   */

  @SuppressWarnings("unchecked")
  public void testImportPortlets() throws Exception {
    Collection mocks = new ArrayList();
    MockPortletRuntimeData mock = new MockPortletRuntimeData("app1", "name1");
    mocks.add(mock);
    mock = new MockPortletRuntimeData("app1", "name2");
    mocks.add(mock);
    mock = new MockPortletRuntimeData("app2", "name21");
    mocks.add(mock);

    service_.importPortlets(mocks);

    assertNotNull(service_.findPortletCategoryByName("app1"));

    PortletCategory portletCategory = service_.findPortletCategoryByName("app2");
    assertNotNull(portletCategory);
    List portlets = service_.getPortlets(portletCategory.getId());
    for (Iterator iterator = portlets.iterator(); iterator.hasNext();) {
      Portlet portlet = (Portlet) iterator.next();
      assertTrue(portlet.getDisplayName().startsWith("name2"));
      List roles = service_.getPortletPermissions(portlet.getId());
      for (Iterator iterator1 = roles.iterator(); iterator1.hasNext();) {
        PortletPermission ppermission = (PortletPermission) iterator1.next();
        assertEquals("member:/user", ppermission.getPermissionExpression());
      }
    }
  }

  public void testClearRepository() throws Exception {
    PortletCategory category = createPortletCategory("repositoryCategory", "this is a test") ;
    Portlet portlet = createPortlet(category, "portlet", "this is a test") ;
    PortletPermission portletRole = createPortletPermission(portlet, "member:/user", "this is a test") ;


    service_.clearRepository();

    try{
      category = service_.getPortletCategory(category.getId());
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }
    
    try{
      portlet =   service_.getPortlet(portlet.getId());
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }

    try{
      portletRole = service_.getPortletPermission(portletRole.getId());
      fail("exception should have been thrown");
    } catch (PortletRegisteryException e) {
    }
  }

  private PortletCategory createPortletCategory(String name, String desc) throws Exception {
    PortletCategory portletCategory = service_.createPortletCategoryInstance() ;
    portletCategory.setPortletCategoryName(name) ;
    portletCategory.setDescription(desc) ;
    portletCategory = service_.addPortletCategory(portletCategory) ;
    return portletCategory ;
  }

  private Portlet createPortlet(PortletCategory category, String name, String desc) throws Exception {
    Portlet portlet = service_.createPortletInstance() ;
    portlet.setPortletName(name) ;
    portlet.setDescription(desc) ;
    portlet = service_.addPortlet(category, portlet) ;
    return portlet ;
  }

  @SuppressWarnings("unused")
  private PortletPermission createPortletPermission(Portlet portlet, String pexpression, String desc) throws Exception {
    PortletPermission ppermission = service_.createPortletPermissionInstance() ;
    ppermission.setPermissionExpression(pexpression) ;
    ppermission = service_.addPortletPermission(portlet, ppermission) ;
    return ppermission ;
  }

  protected String getDescription() {
    return "Test Portlet Registery Service" ;
  }

}