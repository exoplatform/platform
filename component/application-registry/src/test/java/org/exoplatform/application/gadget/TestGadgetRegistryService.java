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
    Gadget g1 = new Gadget() ;
    g1.setName("weather") ;
    g1.setUrl("http://www.labpixies.com/campaigns/weather/weather.xml") ;
    Gadget g2 = new Gadget() ;
    g2.setName("map") ;
    g2.setUrl("http://www.labpixies.com/campaigns/maps/maps.xml") ;
    service_.saveGadget(g1) ;
    service_.saveGadget(g2) ;
    assertEquals(2, service_.getAllGadgets().size()) ;
    Gadget g3 = service_.getGadget(g1.getName()) ; 
    assertNotNull(g3) ;
    assertEquals("weather", g3.getName()) ;
    assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", g3.getUrl()) ;
    
    Gadget g4 = service_.getGadget(g2.getName()) ; 
    assertNotNull(g4) ;
    assertEquals("map", g4.getName()) ;
    assertEquals("http://www.labpixies.com/campaigns/maps/maps.xml", g4.getUrl()) ;

  }
  
  public void testGetGadget() throws Exception {
    Gadget gadget = new Gadget() ;
    gadget.setName("weather") ;
    gadget.setUrl("http://www.labpixies.com/campaigns/weather/weather.xml") ;
    service_.saveGadget(gadget) ;
    Gadget g = service_.getGadget(gadget.getName()) ;
    assertNotNull(g) ;
    assertEquals(1, service_.getAllGadgets().size()) ;
    assertEquals("weather", g.getName()) ;
    assertEquals("http://www.labpixies.com/campaigns/weather/weather.xml", g.getUrl()) ;    
  }
  
  public void testGetAllGadgets() {
    
  }

  public void testRemoveGadget() {
    
  }
  
  public void tearDown() throws Exception {
    List<Gadget> gadgets = service_.getAllGadgets() ;
    for(Gadget ele : gadgets) {
      service_.removeGadget(ele.getName()) ;
    }
  }

}
