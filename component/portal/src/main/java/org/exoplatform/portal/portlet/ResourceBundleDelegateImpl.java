 /***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.exoplatform.portal.portlet ;

import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.services.portletcontainer.bundle.ResourceBundleDelegate;
import org.exoplatform.services.resources.ResourceBundleService;

/**
 * @author Benjamin Mestrallet
 * benjamin.mestrallet@exoplatform.com
 */
public class ResourceBundleDelegateImpl implements ResourceBundleDelegate {
  
  private ResourceBundleService resourceBundleService;
  
  public ResourceBundleDelegateImpl(ResourceBundleService resourceBundleService) {    
    this.resourceBundleService = resourceBundleService;
  }
  
  public ResourceBundle lookupBundle(String portletBundleName, Locale locale){
    String[]  portalBundles =  resourceBundleService.getSharedResourceBundleNames();
    String[] bundles = new String[portalBundles.length  + 1] ;
    for(int i = 0; i < portalBundles.length; i++) {
      bundles[i] = portalBundles[i] ;
    }
    bundles[portalBundles.length] =  portletBundleName ;
    return resourceBundleService.getResourceBundle(bundles, locale);
  }
  
}