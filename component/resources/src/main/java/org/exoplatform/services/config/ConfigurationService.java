/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.config;

/**
 * @author Tuan Nguyen (tuan08@users.sourceforge.net)
 * @since Dec 5, 2004
 * @version $Id: ConfigurationService.java 5799 2006-05-28 17:55:42Z geaz $
 */
public interface ConfigurationService {
  public Object getServiceConfiguration(Class serviceType) throws Exception ;
  public void   saveServiceConfiguration(Class serviceType, Object config) throws Exception ;
  public void   removeServiceConfiguration(Class serviceType) throws Exception ;
}
