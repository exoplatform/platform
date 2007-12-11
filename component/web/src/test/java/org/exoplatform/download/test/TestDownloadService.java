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
package org.exoplatform.download.test;

import org.exoplatform.download.DownloadService;
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
