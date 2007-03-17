 /**************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources;

import java.util.Collection;
/**
 * @author Benjamin Mestrallet
 * benjamin.mestrallet@exoplatform.com
 * 
 * This Service is used to manage the locales that the applications can handle 
 */
public interface LocaleConfigService {
  
  /**
   * @return   Return the default LocaleConfig
   */
  public LocaleConfig getDefaultLocaleConfig() ;
  /**
   * @param lang  a locale language
   * @return The LocalConfig  
   */
  public LocaleConfig getLocaleConfig(String lang);
  /**
   * @return All the LocalConfig that manage by the service 
   */
  public Collection getLocalConfigs();
  
}
