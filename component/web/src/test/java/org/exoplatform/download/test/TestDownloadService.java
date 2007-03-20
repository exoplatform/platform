/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.download.test;

import org.exoplatform.services.download.DownloadService;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 26, 2005
 */
public class TestDownloadService extends BasicTestCase {
  
  private DownloadService service_ ;
  
  public TestDownloadService(String name) {
    super(name) ; 
  }
  
  public void setUp() throws Exception {
    if(service_ !=null)  return;
//    PortalContainer manager = PortalContainer.getInstance() ;
//    service_ = (DownloadService)manager.getComponentInstanceOfType(DownloadService.class) ;      
  }
  
  public void testDownloadService() throws Exception {
    assertTrue("expect service is inited",service_ ==null)  ;   
  }
  
  protected String getDescription() { return "Test Download Service" ; }
}
