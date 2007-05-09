/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.registry.test;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.registry.ApplicationRegistry;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.registry.ServiceRegistry;
import org.exoplatform.test.BasicTestCase;
/**
 * Thu, May 3, 2007 @   
 * @author: Tuan Nguyen
 * @version: $Id$
 * @email: tuan08@yahoo.com
 */
public class TestJCRRegistryService extends BasicTestCase {
  public TestJCRRegistryService(String name) {
    super(name);
  }

  public void setUp() throws Exception {
  }
  
  public void testService() throws Exception {
    PortalContainer pcontainer = PortalContainer.getInstance() ;
    JCRRegistryService service = 
      (JCRRegistryService) pcontainer.getComponentInstanceOfType(JCRRegistryService.class);
    
    assertEXORegistry(service) ;
    assertUser(service) ;

    showTree(service) ;
  }
  
  private void assertEXORegistry(JCRRegistryService service) throws Exception {
    assertEXORegistryApp(service) ;
    assertEXORegistryService(service) ;
  }
  
  private void assertEXORegistryApp(JCRRegistryService service) throws Exception {
    Session session = service.getSession() ;
    String[] appNames = {"Test App 1", "Test App 2"} ;
    
    for (String name : appNames) {
      Node appNode = service.getApplicationRegistryNode(session, name) ;
      assertNull(appNode) ;
    }
    
    for (String name : appNames) {
      ApplicationRegistry appReg = createAppReg(name) ;
      service.createApplicationRegistry(appReg, true) ;
    }
    
    List<Node> appNodes = new ArrayList<Node>() ;
    for (String name : appNames) {
      Node appNode = service.getApplicationRegistryNode(session, name) ;
      assertNotNull(appNode) ;
      assertEquals(name, appNode.getName()) ;
      appNodes.add(appNode) ;
    }
    
    assertEquals(2, appNodes.size()) ;
  }
  
  private void assertEXORegistryService(JCRRegistryService service) throws Exception {
    Session session = service.getSession() ;
    String[] serviceNames = {"Test Service 1", "Test Service 2"} ;
    
    for (String name : serviceNames) {
      Node serviceNode = service.getServiceRegistryNode(session, name) ;
      assertNull(serviceNode) ;
    }
    
    for (String name : serviceNames) {
      ServiceRegistry srvReg = createSrvReg(name) ;
      service.createServiceRegistry(srvReg, true) ;
    }
    
    List<Node> serviceNodes = new ArrayList<Node>() ;
    for (String name : serviceNames) {
      Node serviceNode = service.getServiceRegistryNode(session, name) ;
      assertNotNull(serviceNode) ;
      assertEquals(name, serviceNode.getName()) ;
      serviceNodes.add(serviceNode) ;
    }
    
    assertEquals(2, serviceNodes.size()) ;    
  }
  
  private void assertUser(JCRRegistryService service) throws Exception {
    assertUserCreateHome(service) ;
    assertUserApp(service) ;
    assertUserService(service) ;
  }
  
  private void assertUserCreateHome(JCRRegistryService service) throws Exception {
    Session session = service.getSession() ;
    String[] userNames = {"User1", "User2"} ;
    
    for (String name : userNames) {
      Node userNode = service.getUserNode(session, name) ;
      assertNull(userNode) ;
      service.createUserHome(name, true) ;
    }
    
    List<Node> userNodes = new ArrayList<Node>() ;
    for (String name : userNames) {
      Node userNode = service.getUserNode(session, name) ;
      assertNotNull(userNode) ;
      assertEquals(name, userNode.getName()) ;
      assertEquals("exo:applications", userNode.getNode("exo:registry/exo:applications").getName()) ;
      assertEquals("exo:services", userNode.getNode("exo:registry/exo:services").getName()) ;
      userNodes.add(userNode) ;
    }
    
    assertEquals(2, userNodes.size()) ;
  }
  
  private void assertUserApp(JCRRegistryService service) throws Exception {
    Session session = service.getSession() ;
    String userName = "TestUser" ;
    String[] appNames = {"Test App 1", "Test App 2"} ;
    
    service.createUserHome(userName, true) ;
    
    for (String name : appNames) {
      Node appNode = service.getApplicationRegistryNode(session, userName, name) ;
      assertNull(appNode) ;
      ApplicationRegistry appReg = createAppReg(name) ;
      service.createApplicationRegistry(userName, appReg, true) ;
    }
    
    List<Node> appNodes = new ArrayList<Node>() ;
    for (String name : appNames) {
      Node appNode = service.getApplicationRegistryNode(session, userName, name) ;
      assertNotNull(appNode) ;
      assertEquals(name, appNode.getName()) ;
      appNodes.add(appNode) ;
    }
    
    assertEquals(2, appNodes.size()) ;
  }
  
  private void assertUserService(JCRRegistryService service) throws Exception {
    Session session = service.getSession() ;
    String userName = "TestUser" ;
    String[] serviceNames = {"Test Service 1", "Test Service 2"} ;
    
    service.createUserHome(userName, true) ;
    
    for (String name : serviceNames) {
      Node serviceNode = service.getServiceRegistryNode(session, userName, name) ;
      assertNull(serviceNode) ;
      ServiceRegistry srvReg = createSrvReg(name) ;
      service.createServiceRegistry(userName, srvReg, true) ;
    }
    
    List<Node> serviceNodes = new ArrayList<Node>() ;
    for (String name : serviceNames) {
      Node srvNode = service.getServiceRegistryNode(session, userName, name) ;
      assertNotNull(srvNode) ;
      assertEquals(name, srvNode.getName()) ;
      serviceNodes.add(srvNode) ;
    }
    
    assertEquals(2, serviceNodes.size()) ;
    
  }
  
  
  
  private ApplicationRegistry createAppReg(String name) {
    ApplicationRegistry app = new ApplicationRegistry(name) {
      public void preAction(JCRRegistryService service) throws Exception {
        this.description = "Description for ApplictionRegistry " + this.name ;
      }
      
      public void postAction(JCRRegistryService sevice, Node appNode) throws Exception {
        System.out.println("Method [postAction()] is called.") ;
      }
    } ;
    
    return app ;
  }
  
  private ServiceRegistry createSrvReg(String name) {
    ServiceRegistry registry = new ServiceRegistry(name) {
      public void preAction(JCRRegistryService service) throws Exception {
        this.description = "Description for ServiceRegistry " + this.name ;
      }

      public void postAction(JCRRegistryService service, Node registryNode) throws Exception {
        System.out.println("Hello, calling postCreate method");
      }
    };
    
    return registry ;
  }
  
  
  private void showTree(JCRRegistryService service) throws Exception {
    Node root = service.getSession().getRootNode() ;
    
    String pre = "/" ;
    showNode(root, pre) ;
  }
  private void showNode(Node node, String pre) throws Exception{
    System.out.println(pre + node.getName());
    NodeIterator ite = node.getNodes();
    while(ite.hasNext()){
      showNode(ite.nextNode(), pre + "---");
    }
  }
}
