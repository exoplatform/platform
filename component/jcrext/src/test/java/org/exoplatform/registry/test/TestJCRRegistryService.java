/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.registry.test;

import javax.jcr.Node;

import org.exoplatform.container.PortalContainer;
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
    
    ServiceRegistry registry = new ServiceRegistry("name") {
      public void preAction(JCRRegistryService service) throws Exception {
        this.description = "A Description";
      }

      public void postAction(JCRRegistryService service, Node registryNode) throws Exception {
        System.out.println("Hello, calling postCreate method");
      }
    };
    
    service.createServiceRegistry(registry, true) ;
  }
  
}
