/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.portal.layout;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Container;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SAS
 * Author : Tan Pham Dinh
 *          tan.pham@exoplatform.com
 * Feb 20, 2009  
 */
public class TestPortalLayoutService extends BasicTestCase {
  
  protected String userName = "root" ;
  protected Container container1, container2;
  protected PortalLayoutService service ;

  public TestPortalLayoutService(String name) {
    super(name);
  }
  
  public void setUp() throws Exception {
 //   PortalContainer portalCont = PortalContainer.getInstance() ;
 //   service  = (PortalLayoutService) portalCont.getComponentInstanceOfType(PortalLayoutService.class) ;
    
//    container1 = new Container() ;
//    container1.setId("testId1") ;
//    container1.setFactoryId("testFactoryId1") ;
//    container1.setName("testName1") ;
//    container1.setHeight("300") ;
//    container1.setWidth("400") ;
//    container1.setTitle("testTitle1") ;
//    container1.setDescription("testDescription1") ;
//    
//    container2 = new Container() ;
//    container2.setId("testId2") ;
//    container2.setFactoryId("testFactoryId2") ;
//    container2.setName("testName2") ;
//    container2.setHeight("500") ;
//    container2.setWidth("600") ;
//    container2.setTitle("testTitle2") ;
//    container2.setDescription("testDescription2") ;
//    
//    assertNotNull(service) ;
//    assertNotNull(container1) ;
//    assertNotNull(container2) ;
  }
  
  public void testCreate() throws Exception {
//    service.create(container1) ;
//    Container rCont = service.getContainer(container1.getId()) ;
//    assertNotNull(rCont) ;
//    assertEquals(container1.getName(), rCont.getName()) ;
//    rCont = service.getContainer(container1.getId(), userName) ;
//    assertNull(rCont) ;
//    service.remove(container1) ;
  }
//
//  public void testCreateWithUserId() throws Exception {
//    service.create(container1, userName) ;
//    Container rCont = service.getContainer(container1.getId(), userName) ;
//    assertNotNull(rCont) ;
//    assertEquals(container1.getName(), rCont.getName()) ;
//    rCont = service.getContainer(container1.getId()) ;
//    assertNull(rCont) ;
//    service.remove(container1, userName) ;
//  }
//  
//  public void testCreateWithTemplate() throws Exception {
//    String id = "testTemplate" ;
//    String template = "template" ;
//    service.create(id, template, userName) ;
//    Container rCont = service.getContainer(id, userName) ;
//    assertNotNull(rCont) ;
//    assertEquals("rootName", rCont.getName()) ;
//    assertNotNull(rCont.getChildren()) ;
//    assertEquals(3, rCont.getChildren().size()) ;
//    service.remove(rCont, userName) ;
//    rCont = service.getContainer(id, userName) ;
//    assertNull(rCont) ;
//  }
//  
//  public void testSave() throws Exception {
//    service.create(container1) ;
//    Container rCont1 = service.getContainer(container1.getId()) ;
//    assertEquals(container1.getName(), rCont1.getName()) ;
//    container1.setName("title") ;
//    Container rCont2 = service.getContainer(container1.getId()) ;
//    assertEquals(rCont1.getName(), rCont2.getName()) ;
//    service.save(container1) ;
//    rCont2 = service.getContainer(container1.getId()) ;
//    assertNotSame(rCont1.getName(), rCont2.getName()) ;
//    service.remove(container1) ;
//  }
//  
//  public void testSaveWithUserId() throws Exception {
//    service.create(container1, userName) ;
//    Container rCont1 = service.getContainer(container1.getId(), userName) ;
//    assertEquals(container1.getName(), rCont1.getName()) ;
//    container1.setName("title") ;
//    Container rCont2 = service.getContainer(container1.getId(), userName) ;
//    assertEquals(rCont1.getName(), rCont2.getName()) ;
//    service.save(container1, userName) ;
//    rCont2 = service.getContainer(container1.getId(), userName) ;
//    assertNotSame(rCont1.getName(), rCont2.getName()) ;
//    service.remove(container1, userName) ;
//  }
//  
//  public void testDelete() throws Exception {
//    service.create(container1) ;
//    service.save(container1) ;
//    service.create(container2) ;
//    service.save(container2) ;
//    Container rCont = service.getContainer(container1.getId()) ;
//    assertNotNull(rCont) ;
//    service.remove(container1) ;
//    assertNotNull(rCont) ;
//    rCont = service.getContainer(container1.getId()) ;
//    assertNull(rCont) ;
//    rCont = service.getContainer(container2.getId()) ;
//    assertNotNull(rCont) ;
//    service.remove(rCont) ;
//  }
//  
//  public void testDeleteWithUserId() throws Exception {
//    service.create(container1, userName) ;
//    service.save(container1, userName) ;
//    service.create(container2, userName) ;
//    service.save(container2, userName) ;
//    Container rCont = service.getContainer(container1.getId(), userName) ;
//    assertNotNull(rCont) ;
//    service.remove(container1, userName) ;
//    assertNotNull(rCont) ;
//    rCont = service.getContainer(container1.getId(), userName) ;
//    assertNull(rCont) ;
//    rCont = service.getContainer(container2.getId(), userName) ;
//    assertNotNull(rCont) ;
//    service.remove(container2, userName) ;
//  }
  
}
