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
  
  public void testAddGadget() throws Exception {
    GadgetApplication g1 = new GadgetApplication("weather", "http://www.labpixies.com/campaigns/weather/weather.xml") ;
    GadgetApplication g2 = new GadgetApplication("map", "http://www.labpixies.com/campaigns/maps/maps.xml") ;
    service_.addGadget(g1) ;
    service_.addGadget(g2) ;
    assertEquals(2, service_.getAllGadgets().size()) ;
    GadgetApplication g3 = service_.getGadget(g1.getApplicationId()) ; 
    assertNotNull(g3) ;
    assertEquals("weather", g3.getApplicationId()) ;
    assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", g3.getUrl()) ;
    
    GadgetApplication g4 = service_.getGadget(g2.getApplicationId()) ; 
    assertNotNull(g4) ;
    assertEquals("map", g4.getApplicationId()) ;
    assertEquals("http://www.labpixies.com/campaigns/maps/maps.xml", g4.getUrl()) ;

  }
  
  public void testGetGadget() throws Exception {
    GadgetApplication gadget = new GadgetApplication("weather", "http://www.labpixies.com/campaigns/weather/weather.xml") ;
    service_.addGadget(gadget) ;
    GadgetApplication g = service_.getGadget(gadget.getApplicationId()) ;
    assertNotNull(g) ;
    assertEquals(1, service_.getAllGadgets().size()) ;
    assertEquals("weather", g.getApplicationId()) ;
    assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", g.getUrl()) ;    
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
