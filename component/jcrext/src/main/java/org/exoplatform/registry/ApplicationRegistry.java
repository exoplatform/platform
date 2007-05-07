/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.registry;

import javax.jcr.Node;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 3, 2007  
 */
public class ApplicationRegistry {
  protected String name ;
  protected String description ;
  
  public ApplicationRegistry(String appName) {
    name = appName ;
  }
  
  public String getName()  { return name ; }
  public String getDescription() { return description ; }
  
  @SuppressWarnings("unused")
  public void preAction(JCRRegistryService service) throws Exception { }
  
  @SuppressWarnings("unused")
  public void postAction(JCRRegistryService service, Node appNode) throws Exception { }
}
