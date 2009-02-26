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
     * 4 preconfig locales: English, France, Arabic, Vietnamese
    **/
    Collection <LocaleConfig> locales = service_.getLocalConfigs() ;
    assertTrue(locales.size() == 4) ;    
    
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