/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.application.gadget;

import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetRegistryService;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 11, 2008  
 */
public class TestGadgetRegistryService extends BasicTestCase {
  
  private GadgetRegistryService service_;
  
  public void setUp() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    service_ = (GadgetRegistryService) container.getComponentInstanceOfType(GadgetRegistryService.class) ;
  }
  
  public void testAddGadget() { 
    
  }
  
  public void testGetGadget() {
    
  }
  
  public void testGetAllGadgets() {
    
  }

  public void testRemoveGadget() {
    
  }
  
  public void tearDown() throws Exception {
    List<GadgetApplication> gadgets = service_.getAllGadgets() ;
    for(GadgetApplication ele : gadgets) {
      service_.removeGadget(ele.getApplicationId()) ;
    }
  }

}
