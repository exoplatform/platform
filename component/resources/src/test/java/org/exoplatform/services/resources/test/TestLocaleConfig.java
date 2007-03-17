/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources.test;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.test.BasicTestCase;

/**
 * Thu, May 15, 2004 @   
 * @author: Tuan Nguyen
 * @version: $Id: TestLocaleConfig.java 5799 2006-05-28 17:55:42Z geaz $
 * @email: tuan08@yahoo.com
 */
public class TestLocaleConfig extends BasicTestCase {
  
  private LocaleConfigService service_ ;
  
  public TestLocaleConfig(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    service_ = (LocaleConfigService) PortalContainer.getInstance().
                                     getComponentInstanceOfType(LocaleConfigService.class) ;
  }
  
  public void tearDown() throws Exception {

  }
  
  public void testLocaleConfigManager() throws Exception {        
    //-------------------default locale is English-------------
    LocaleConfig locale = service_.getDefaultLocaleConfig() ;
    assertTrue("expect defautl locale config is found", locale!=null) ;
    assertTrue("expect default locale is English",locale.getLocale().equals(Locale.ENGLISH)) ;
    // --------------get a locale------------------
    locale = service_.getLocaleConfig("fr") ;
    assertTrue("expect locale config is found", locale!=null) ;
    assertTrue("expect France locale is found", locale.getLocale().equals(Locale.FRANCE)) ;
    
    locale = service_.getLocaleConfig("vi") ;
    assertTrue("expect locale config is found", locale!=null) ;
    assertEquals("expect Viet Nam locale is found", "vi", locale.getLocale().toString()) ;
    /*-------------------get all locale config-------------------
     * expect 3 locale config is found: English, France/Simplified_Chinese
    **/
    Collection <LocaleConfig> locales = service_.getLocalConfigs() ;
    assertTrue("expect 3 locale config are found", locales.size() == 5) ;    
    
    Locale vnlocale = service_.getLocaleConfig("vi").getLocale() ;    
    hasObjectInCollection(vnlocale,locales, new LocaleComparator()) ;
    hasObjectInCollection(Locale.ENGLISH,locales,new LocaleComparator()) ;
    hasObjectInCollection(Locale.FRANCE,locales,new LocaleComparator()) ;            
  } 
  
  public static class LocaleComparator implements Comparator {

    public int compare(Object o1, Object o2) {
      Locale locale1 = (Locale) o1 ;
      LocaleConfig localse2 = (LocaleConfig) o2 ;
      if(locale1.equals(localse2.getLocale())) return 0 ;
      return -1;
    } 
  }
}