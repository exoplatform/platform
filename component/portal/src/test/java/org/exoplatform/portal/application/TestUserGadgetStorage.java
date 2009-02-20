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
package org.exoplatform.portal.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SAS
 * Author : Tan Pham Dinh
 *          tan.pham@exoplatform.com
 * Feb 18, 2009  
 */
public class TestUserGadgetStorage extends BasicTestCase {

  protected String userName1 = "root", userName2 = "userName2" ;
  protected String gadgetType1 = "sport", gadgetType2 = "it" ;
  protected String gadgetId1 = "football", gadgetId2 = "java" ;
  protected String key1 = "mu", key2 = "bean" ;
  
  public TestUserGadgetStorage(String name) {
    super(name);
  } 

  public void testAddSingleValue() throws Exception {
    PortalContainer portalCont = PortalContainer.getInstance() ;
    UserGadgetStorage service = (UserGadgetStorage) portalCont.getComponentInstanceOfType(UserGadgetStorage.class) ;
    assertNotNull(service) ;
    String initValue = "ronaldo" ;
    service.save(userName1, gadgetType1, gadgetId1, key1, initValue) ;
    String receiveValue = service.get(userName1, gadgetType1, gadgetId1, key1) ;
    assertEquals(initValue, receiveValue) ;
  }
  
  public void testAddMapValue() throws Exception {
    PortalContainer portalCont = PortalContainer.getInstance() ;
    UserGadgetStorage service = (UserGadgetStorage) portalCont.getComponentInstanceOfType(UserGadgetStorage.class) ;
    assertNotNull(service) ;
    Map<String, String> values = new HashMap<String, String>() ;
    String initValue1 = "ronaldo", initValue2 = "ejb" ;
    values.put(key1, initValue1) ;
    values.put(key2, initValue2) ;
    service.save(userName1, gadgetType1, gadgetId1, values) ;
    String receiveValue1 = service.get(userName1, gadgetType1, gadgetId1, key1) ;
    String receiveValue2 = service.get(userName1, gadgetType1, gadgetId1, key2) ;
    assertEquals(initValue1, receiveValue1) ;
    assertEquals(initValue2, receiveValue2) ;
  }
  
  public void testGetValue() throws Exception {
    PortalContainer portalCont = PortalContainer.getInstance() ;
    UserGadgetStorage service = (UserGadgetStorage) portalCont.getComponentInstanceOfType(UserGadgetStorage.class) ;
    assertNotNull(service) ;
    Map<String, String> values = new HashMap<String, String>() ;
    String initValue1 = "ronaldo", initValue2 = "ejb" ;
    values.put(key1, initValue1) ;
    values.put(key2, initValue2) ;
    service.save(userName1, gadgetType1, gadgetId1, values) ;
    
    Map<String, String> receiveValues = service.get(userName1, gadgetType1, gadgetId1) ;
    assertEquals(values.get(key1), receiveValues.get(key1)) ;
    assertEquals(values.get(key1), receiveValues.get(key1)) ;
    
    String receiveValue = service.get(userName1, gadgetType1, gadgetId2, key1) ;
    assertNull(receiveValue) ;
    receiveValue = service.get(userName1, gadgetType2, gadgetId1, key1) ;
    assertNull(receiveValue) ;
    receiveValue = service.get(userName2, gadgetType1, gadgetId1, key1) ;
    assertNull(receiveValue) ;
  }
  
  public void testDeleteSingle() throws Exception {
    PortalContainer portalCont = PortalContainer.getInstance() ;
    UserGadgetStorage service = (UserGadgetStorage) portalCont.getComponentInstanceOfType(UserGadgetStorage.class) ;
    assertNotNull(service) ;
    String initValue = "test" ;
    service.save(userName1, gadgetType1, gadgetId1, key1, initValue) ;
    service.delete(userName1, gadgetType1, gadgetId1) ;
    String receiveValue = service.get(userName1, gadgetType1, gadgetId1, key1) ;
    assertNull(receiveValue) ;
  }
  
  public void testDeleteMulti() throws Exception {
    PortalContainer portalCont = PortalContainer.getInstance() ;
    UserGadgetStorage service = (UserGadgetStorage) portalCont.getComponentInstanceOfType(UserGadgetStorage.class) ;
    assertNotNull(service) ;
    Map<String, String> values = new HashMap<String, String>() ;
    String initValue1 = "ronaldo", initValue2 = "ejb" ;
    values.put(key1, initValue1) ;
    values.put(key2, initValue2) ;
    service.save(userName1, gadgetType1, gadgetId1, values) ;
    Set<String> keys = new HashSet<String>() ;
    keys.add(key1) ;
    keys.add(key2) ;
    service.delete(userName1, gadgetType1, gadgetId1, keys) ;
    String receiveValue1 = service.get(userName1, gadgetType1, gadgetId1, key1) ;
    String receiveValue2 = service.get(userName1, gadgetType1, gadgetId1, key2) ;
    assertNull(receiveValue1) ;
    assertNull(receiveValue2) ;
  }
}
 