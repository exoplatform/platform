/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.io.FileInputStream;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.test.BasicTestCase;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * May 2, 2007  
 */
public class TestDataStorage extends BasicTestCase { 
  DataStorage storage_ ;
  public TestDataStorage(String name) {
    super(name) ;
  }
  
  public void testAll()  throws Exception {
    PortalContainer portalContainer = PortalContainer.getInstance() ;
    storage_ = (DataStorage)portalContainer.getComponentInstanceOfType(DataStorage.class) ;
    
    assertPortalConfigOperator() ;
    assertNavigationOperator() ;
  }
  
  void assertPortalConfigOperator()  throws Exception {
    String portalName = "portalone" ;
    
    assertPortalConfigCreate(portalName) ;
    assertPortalConfigSave(portalName) ;
    assertPortalConfigRemove(portalName) ;
  }
  
  void assertPortalConfigCreate(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertEquals(config.getName(), returnConfig.getName()) ;
    storage_.remove(config) ;
  }
  
  void assertPortalConfigSave(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertEquals(portalName, returnConfig.getName()) ;
    
    String newLocate = "new locate" ;
    config.setLocale(newLocate) ;
    storage_.save(config) ;
    PortalConfig returnConfig2 = storage_.getPortalConfig(portalName) ;
    assertEquals(newLocate, returnConfig2.getLocale()) ;
    
    storage_.remove(config) ;
  }
  
  void assertPortalConfigRemove(String portalName) throws Exception {
    PortalConfig config = createPortalConfig(portalName) ;
    assertEquals(portalName, config.getName()) ;
    storage_.create(config) ;
    PortalConfig returnConfig = storage_.getPortalConfig(portalName) ;
    assertNotNull(returnConfig) ;
    
    storage_.remove(config) ;
    PortalConfig returnConfig2 = storage_.getPortalConfig(portalName) ;
    assertNull(returnConfig2) ;
  }
  
  void assertNavigationOperator() throws Exception {
    String ownerId = "portalone" ;
    
    //assertNavigationCreate(ownerId) ;
    //assertNavigationSave(ownerId) ;
    assertNavigationRemove() ;
  }
  
  void assertNavigationCreate(String ownerId) throws Exception {
    PageNavigation navigation = createNavigation(ownerId) ;
    assertEquals(ownerId, navigation.getOwnerId()) ;
    
    storage_.create(navigation) ;
    PageNavigation returnNavigation = storage_.getPageNavigation(navigation.getId()) ;
    assertEquals(ownerId, returnNavigation.getOwnerId()) ;
    
    storage_.remove(navigation) ;
  }
  
  void assertNavigationSave(String ownerId) throws Exception {
    PageNavigation navigation = createNavigation(ownerId) ;
    assertEquals(ownerId, navigation.getOwnerId()) ;
    String navigationId = navigation.getId() ;
    
    String oldDescription = "Old Description." ;
    navigation.setDescription(oldDescription) ;
    storage_.create(navigation) ;
    PageNavigation returnNavigation = storage_.getPageNavigation(navigationId) ;
    assertEquals(oldDescription, returnNavigation.getDescription()) ;
    
    String newDescription = "New Description." ;
    navigation.setDescription(newDescription) ;
    storage_.save(navigation) ;
    PageNavigation returnNavigation2 = storage_.getPageNavigation(navigationId) ;
    assertEquals(newDescription, returnNavigation2.getDescription()) ;
    
    storage_.remove(navigation) ;
  }
  
  void assertNavigationRemove() throws Exception {
    String ownerId1 = "portalone" ;
    PageNavigation navigation1 = createNavigation(ownerId1) ;
    assertEquals(ownerId1, navigation1.getOwnerId()) ;
    String navigationId1 = navigation1.getId() ;
    
    storage_.create(navigation1) ;
    PageNavigation returnNavigation1 = storage_.getPageNavigation(navigationId1) ;
    assertNotNull(returnNavigation1) ;
    
    String ownerId2 = "portaltwo" ;
    PageNavigation navigation2 = createNavigation(ownerId2) ;
    assertEquals(ownerId2, navigation2.getOwnerId()) ;
    String navigationId2 = navigation2.getId() ;
    
    storage_.create(navigation2) ;
    PageNavigation returnNavigation2 = storage_.getPageNavigation(navigationId2) ;
    assertNotNull(returnNavigation2) ;
    
    storage_.remove(navigation1) ;
    
    PageNavigation _returnNavigation1 = storage_.getPageNavigation(navigationId1) ;
    assertNull(_returnNavigation1) ;
    
    PageNavigation _returnNavigation2 = storage_.getPageNavigation(navigationId2) ;
    assertNotNull(_returnNavigation2) ;

  }

  private PortalConfig createPortalConfig(String portalName) throws Exception {
    String configFile = portalName + "/config.xml" ;
    
    PortalConfig config = loadObject(PortalConfig.class, configFile) ;
    
    return config ;
  }
  
  private PageNavigation createNavigation(String ownerId) throws Exception {
    String navigationFile = ownerId + "/navigation.xml" ;

    PageNavigation navigation = loadObject(PageNavigation.class, navigationFile) ;
    
    return navigation ;
  }
  private <T> T loadObject(Class<T> clazz, String file) throws Exception{
    IBindingFactory bfact = BindingDirectory.getFactory(clazz) ;
    IUnmarshallingContext uctx = bfact.createUnmarshallingContext() ;
    FileInputStream is = new FileInputStream("src/test/resources/PortalApp/" + file) ;
    
    return  (T) uctx.unmarshalDocument(is, null) ;
  }

}
