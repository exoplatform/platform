/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
  
  public void testResourceBundleService() throws Exception {        
    //-------getResourceBundle have loaded from property file to database--------
  	ResourceBundle res =  service_.getResourceBundle(fileRes, Locale.ENGLISH) ;
  	assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect language is: English ", res.getString("language").equals("English")) ;
    
  	res = service_.getResourceBundle(fileRes, Locale.FRANCE) ;
  	assertTrue("Expect to find the ResourceBundle", res != null);
  	assertEquals("Expect the french resource bundle", "French", res.getString("language"));    
  	
    Locale vnLocale = lservice_.getLocaleConfig("vi").getLocale() ;
    res = service_.getResourceBundle(fileRes, vnLocale) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect language is: TiengViet ", "TiengViet".equals(res.getString("language").trim())) ;
    
    //------------create ressource bundle in database------------------
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
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystem")) ;    
    assertEquals("Expect languge property is:","french", res.getString("language")) ;
    
    //------get resource bundle with both resource bundle: fileRes and databaseRes
    res = service_.getResourceBundle(mergeRes,Locale.FRANCE) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertEquals("Expect French locale bundle", "fr-property", res.getString("property"));
        
    assertTrue("expect 'fr'(in databseRes) is found",res.getString("language").equals("fr")) ;        
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystem")) ;
    
    //------Get resource bundle with concrete class loader (isn't current class loader)
    ClassLoader cl = TestResourceBundleService.class.getClassLoader().getParent() ;
    
    res = service_.getResourceBundle(fileRes,vnLocale,cl) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect language is: TiengViet ", "TiengViet".equals(res.getString("language").trim())) ;
    
    res =  service_.getResourceBundle(fileRes, Locale.ENGLISH, cl) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect language is: English ",res.getString("language").equals("English")) ;

    res = service_.getResourceBundle(fileRes,Locale.FRANCE,cl) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystem")) ;
    
    //------get resource bundle with both resource bundle: fileRes and databaseRes with class loader 
    res = service_.getResourceBundle(mergeRes,Locale.FRANCE,cl) ;
    assertTrue("Expect to find the ResourceBundle", res != null);
    assertEquals("Expect French locale bundle", "fr-property", res.getString("property"));
        
    assertTrue("expect 'fr'(in databseRes) is found",res.getString("language").equals("fr")) ;        
    assertTrue("Expect 'fileRes' is updated",res.getString("key1").equals("fileSystem")) ;
    
    //-----------get all resource bundle-----------    
    Query q = new Query(null, null) ;
    List l = service_.findResourceDescriptions(q).getAll() ;    
  	assertTrue("Expect at least 5 locale properties resources",  l.size() >= 5);
  	
    //----------remove a resource bundle data with Id: fileRes_en------
    int sizeBeforeRemove = l.size() ;
    ResourceBundleData data = service_.getResourceBundleData(fileRes+"_en") ;
    service_.removeResourceBundleData(data.getId());
    l = service_.findResourceDescriptions(q).getAll() ;
  	assertEquals("Expect resources bundle in in database decrease", sizeBeforeRemove - 1 , l.size());
    assertTrue("expect resource bundle is removed",service_.getResourceBundleData(fileRes+"_en")==null) ;    
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
}
