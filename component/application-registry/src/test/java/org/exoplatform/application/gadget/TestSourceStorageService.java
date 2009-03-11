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
package org.exoplatform.application.gadget;

import java.util.Calendar;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.test.BasicTestCase;
/*
 * Thu, May 15, 2004 @   
 * @author: Tuan Nguyen
 * @version: $Id: TestResourceBundleService.java 5799 2006-05-28 17:55:42Z geaz $
 * @since: 0.0
 * @email: tuan08@yahoo.com
 */
public class TestSourceStorageService extends BasicTestCase {
  
  
  private SourceStorage service_ ;
  
  public void setUp() throws Exception {
//    PortalContainer manager  = PortalContainer.getInstance();
//    service_ = (SourceStorage) manager.getComponentInstanceOfType(SourceStorage.class) ;
  }
  
  public void testSourceStorageAdd() throws Exception {    
//    //-------Insert Source Storeage 1--------
//    Source source1 = new Source("Calculator.xml", "application/xml", "UTF-8");
//    source1.setTextContent("Gadget's content");
//    source1.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source1) ; 
//    
//    //-------Check Source Storeage's content added--------
//    Source sourceGet = service_.getSource("source/Calculator.xml");
//    assertEquals("Gadget's content", sourceGet.getTextContent());
//    
//    //-------Insert Source Storeage 2 with path is null--------
//    Source source2 = new Source("Todo.xml", "application/xml", "UTF-8");
//    source2.setTextContent("Gadget's content");
//    source2.setLastModified(Calendar.getInstance());
//    service_.saveSource(null, source2) ; 
//    
//    //-------Check Source Storeage's content added--------
//    Source sourceGet2 = service_.getSource("Todo.xml");
//    assertEquals("Gadget's content", sourceGet2.getTextContent());
//    
//    //-------Insert Source Storeage 2 with text content is null--------
//    Source source3 = new Source("Todo.xml", "application/xml", "UTF-8");
//    source3.setTextContent(null);
//    source3.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source3) ; 
//    
//    Source sourceGet3 = service_.getSource("source/Todo.xml");
//    System.out.println("\n\n\n\n\n\n\n\n\naaa" + service_.getSourceURI("source/Todo.xml"));
//    assertTrue("Expect text content is empty", sourceGet3.getTextContent().equals(""));
//    
  }
//  
//  public void testSourceStorageUpdate() throws Exception {    
//    //-------Insert Source Storeage 1--------
//    Source source = new Source("Calculator.xml", "application/xml", "UTF-8");
//    source.setTextContent("Gadget's content");
//    source.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source) ; 
//    
//    //-------Check Source Storeage's content added--------
//    Source sourceGet = service_.getSource("source/Calculator.xml");
//    assertEquals("Gadget's content", sourceGet.getTextContent());
//    
//    //-------Update Source Storeage's just added to database--------
//    source.setTextContent("Gadget's content update");
//    service_.saveSource("source", source);
//    
//    //-------Check Source Storeage's content just updated--------
//    sourceGet = service_.getSource("source/Calculator.xml");
//    assertEquals("Gadget's content update", sourceGet.getTextContent());
//    
//  }
//  
//  public void testSourceStorageRemove() throws Exception {    
//    //-------Insert Source Storeage 1--------
//    Source source = new Source("Calculator.xml", "application/xml", "UTF-8");
//    source.setTextContent("Gadget's content");
//    source.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source) ; 
//    
//    //-------Check Source Storeage's content added--------
//    Source sourceGet = service_.getSource("source/Calculator.xml");
//    assertEquals("Gadget's content", sourceGet.getTextContent());
//    
//    //System.out.println("\n\n\n\n\n\naaaa" + sourceGet.getName());
//    //-------Remove Source Storeage's content added--------
//    service_.removeSource("source/Calculator.xml");
//    sourceGet = service_.getSource("source/Calculator.xml");
//    //-------Check Source Storeage is really removed--------
//    assertTrue("Expect source is null", sourceGet==null);
//    
//  }
//  
//  public void testSourceStorageList() throws Exception {
//    //-------Check list of Source Storeage is empty--------
//    Source sourceGet = service_.getSource("source");
//    
//    assertTrue("First, none of source exist", sourceGet == null);
//    
//    //-------Insert Source Storeage1--------
//    Source source1 = new Source("Calculator.xml", "application/xml", "UTF-8");
//    source1.setTextContent("Gadget's content");
//    source1.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source1) ; 
//    
//    //-------Check Source Storeage1's content added--------
//    sourceGet = service_.getSource("source/Calculator.xml");
//    assertEquals("Gadget's content", sourceGet.getTextContent());
//    
//    //-------Insert Source Storeage2--------
//    Source source2 = new Source("Todo.xml", "application/xml", "UTF-8");
//    source2.setTextContent("Gadget's content");
//    source2.setLastModified(Calendar.getInstance());
//    service_.saveSource("source", source2) ; 
//    
//    //-------Check Source Storeage2's content added--------
//    Source sourceGet2 = service_.getSource("source/Todo.xml");
//    assertEquals("Gadget's content", sourceGet2.getTextContent());
//    
//  }
//  
//  protected String getDescription() {
//    return "Test Source Storage Service" ;
//  }
//  
//  public void tearDown() throws Exception {
//    // remove all data test
//    service_.removeSource("source");
//  }
}
