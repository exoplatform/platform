/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.services.resources.test;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.LogService;
import org.exoplatform.services.log.LogUtil;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.services.resources.Query;
import org.exoplatform.services.resources.ResourceBundleData;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.test.BasicTestCase;
/*
 * Thu, May 15, 2004 @   
 * @author: Tuan Nguyen
 * @version: $Id: TestResourceBundleService.java 5799 2006-05-28 17:55:42Z geaz $
 * @since: 0.0
 * @email: tuan08@yahoo.com
 */
public class TestResourceBundleService extends BasicTestCase {
  
  final static private String PROPERTIES =  "language=en\nproperty=property" ;
  final static private String PROPERTIES_FR =  "language=fr" ;
  final static private String PROPERTIES_FR_UPDATE=  "language=fr\nproperty=fr-property" ;  
  private static String databaseRes = "exo.locale" ;         
  private static String fileRes = "locale.test.resources.test" ;  
  
  private static String[] mergeRes = {fileRes, databaseRes} ;  
  
  private ResourceBundleService service_ ;
  private LocaleConfigService lservice_ ;
  
  public TestResourceBundleService(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    PortalContainer manager  = PortalContainer.getInstance();
    service_ = (ResourceBundleService) manager.getComponentInstanceOfType(ResourceBundleService.class) ;
    lservice_ = (LocaleConfigService)manager.getComponentInstanceOfType(LocaleConfigService.class) ;
    LogUtil.setLevel("org.exoplatform.services.resources", LogService.DEBUG, true) ;
    LogUtil.setLevel("org.exoplatform.services.database", LogService.DEBUG, true) ;
  }
  
  public void testResourceBundleServiceUpdate() throws Exception {    
    //-------getResourceBundle have loaded from property file to database--------
    ResourceBundle res =  service_.getResourceBundle(fileRes, Locale.ENGLISH) ;    
    
//    //------------create ressource bundle in database------------------
    createResourceBundle(databaseRes,PROPERTIES,Locale.ENGLISH.getLanguage()) ;
    createResourceBundle(databaseRes,PROPERTIES_FR,Locale.FRANCE.getLanguage()) ;        
    
    res = service_.getResourceBundle(databaseRes, Locale.ENGLISH) ;    
    assertTrue("Expect to find the ResourceBundle", res != null);
    
    res = service_.getResourceBundle(databaseRes, Locale.FRANCE) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertEquals("Expect French locale bundle", "fr", res.getString("language"));
    assertEquals("Expect French locale bundle", "property", res.getString("property"));
    //--------- Update a databseRes resource bundle in database ----------------
    createResourceBundle(databaseRes,PROPERTIES_FR_UPDATE, Locale.FRANCE.getLanguage()) ;                
    res = service_.getResourceBundle(databaseRes, Locale.FRANCE) ;
    assertEquals("Expect French locale bundle", "fr-property", res.getString("property"));    
      
    //--------Update fileRes resource bundle in databse--------------
    String datas = "key1=fileSystem\nlanguage=french" ;
    createResourceBundle(fileRes, datas, Locale.FRANCE.getLanguage()) ;
    res = service_.getResourceBundle(fileRes, Locale.FRANCE) ; 
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystem")) ;    
    assertEquals("Expect languge property is:","french", res.getString("language")) ;

    //--------Update fileRes resource bundle in databse--------------
    datas = "key1=fileSystemUpdate\nlanguage=french" ;
    createResourceBundle(fileRes, datas, Locale.FRANCE.getLanguage()) ;
    res = service_.getResourceBundle(fileRes, Locale.FRANCE) ; 
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystemUpdate")) ;    
    assertEquals("Expect languge property is:","french", res.getString("language")) ;
    
    tearDown();
  }
  
  public void testResourceBundleServiceRemove() throws Exception {   
    //-------getResourceBundle have loaded from property file to database--------
    ResourceBundle res =  service_.getResourceBundle(fileRes, Locale.ENGLISH) ;     
    
    //------------create ressource bundle in database------------------
    createResourceBundle(databaseRes,PROPERTIES,Locale.ENGLISH.getLanguage()) ;
    createResourceBundle(databaseRes,PROPERTIES_FR,Locale.FRANCE.getLanguage()) ;        
    
    res = service_.getResourceBundle(databaseRes, Locale.ENGLISH) ;    
    assertTrue("Expect to find the ResourceBundle", res != null);
    
    res = service_.getResourceBundle(databaseRes, Locale.FRANCE) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertEquals("Expect French locale bundle", "fr", res.getString("language"));
    assertEquals("Expect French locale bundle", "property", res.getString("property"));  
    
//    //-----------get all resource bundle-----------    
    Query q = new Query(null, null) ;
    List l = service_.findResourceDescriptions(q).getAll() ;  
    
    //----------remove a resource bundle data with Id: databaseRes_en------
    int sizeBeforeRemove = l.size() ;
    ResourceBundleData data = service_.getResourceBundleData(databaseRes+"_en") ;
    service_.removeResourceBundleData(data.getId());
    l = service_.findResourceDescriptions(q).getAll() ;
    
    assertEquals("Expect resources bundle in in database decrease", sizeBeforeRemove - 1 , l.size());
    assertTrue("expect resource bundle is removed",service_.getResourceBundleData(databaseRes+"_en")==null) ; 
    
    tearDown();
  }
  
  public void testResourceBundleServiceList() throws Exception {   
    
    Query q = new Query(null, null) ;
    List l = service_.findResourceDescriptions(q).getAll() ;  
    
    assertTrue("Expect none locale properties resources",  l.size() == 0);
    
    //-------getResourceBundle have loaded from property file to database--------
    ResourceBundle res =  service_.getResourceBundle(fileRes, Locale.ENGLISH) ;     
    
    //------------create ressource bundle in database------------------
    createResourceBundle(databaseRes,PROPERTIES,Locale.ENGLISH.getLanguage()) ;
    createResourceBundle(databaseRes,PROPERTIES_FR,Locale.FRANCE.getLanguage()) ;        
    
    res = service_.getResourceBundle(databaseRes, Locale.ENGLISH) ;    
    assertTrue("Expect to find the ResourceBundle", res != null);
    
    res = service_.getResourceBundle(databaseRes, Locale.FRANCE) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertEquals("Expect French locale bundle", "fr", res.getString("language"));
    assertEquals("Expect French locale bundle", "property", res.getString("property"));  
    
//    //-----------get all resource bundle-----------    
    q = new Query(null, null) ;
    l = service_.findResourceDescriptions(q).getAll() ;  
    
    assertTrue("Expect at least 2 locale properties resources",  l.size() == 2); 
    
    tearDown();
  }
  private void createResourceBundle(String name, String datas,String language) throws Exception {
    ResourceBundleData data = service_.createResourceBundleDataInstance();
    data.setName(name);  
    data.setData(datas); 
    data.setLanguage(language) ;
    service_.saveResourceBundle(data) ;
  }
  
  protected String getDescription() {
    return "Test Resource Bundle Service" ;
  }
  
  public void tearDown() throws Exception {
    // remove all data test
    service_.removeResourceBundleData("");
  }
}
