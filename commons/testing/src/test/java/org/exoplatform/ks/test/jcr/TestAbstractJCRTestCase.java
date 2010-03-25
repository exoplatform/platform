/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ks.test.jcr;


import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.ks.test.AssertUtils;
import org.exoplatform.ks.test.Closure;
import org.testng.annotations.Test;

import static  org.testng.AssertJUnit.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
 @ConfiguredBy({@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/jcr/jcr-configuration.xml")})
public class TestAbstractJCRTestCase extends AbstractJCRTestCase
{

   @Test
   public void testGetSession() throws Exception
   {
     Session session = getSession();   
     assertNotNull("Session was null", session);
     assertEquals(getWorkspace(), session.getWorkspace().getName());
     session.logout();
   }
   
   @Test
   public void testRepository() {
     assertNotNull("repository was null", getRepository());
   }
   
   @Test
   public void testWorkspace() {
     assertNotNull("workspace was null", getWorkspace());
   }
   
   @Test
   public void testAssertNodeExists() throws Exception {
     Session session = getSession();
     Node root = session.getRootNode();
     Node node = root.addNode("this");
     node = node.addNode("path");
     node = node.addNode("exists");
     session.save();    
     assertNodeExists("this/path/exists");
     assertNodeNotExists("this/path/does/not/exist");
     session.logout();     
   }
 
   @Test
   public void testAddNode() throws Exception {
     
     addNode("addnode");
     Session session = getSession();
     Node root = session.getRootNode();
     assertNotNull(root.getNode("addnode"));
     
     addNode("addnode/hierarchy");
     root.refresh(false);
     assertNotNull(root.getNode("addnode/hierarchy"));     
     
     addNode("addnode/with/no/ancestors");
     root.refresh(false);
     assertNotNull(root.getNode("addnode/with/no/ancestors"));
     
     AssertUtils.assertException(RuntimeException.class, new Closure() {public void dothis() { addNode("/absolute/path/not/allowed");}}); 

     session.logout();     
   }
   
   @Test
   public void testAddFile() throws Exception {
     
     addFile("addfile");
     Session session = getSession();
     Node root = session.getRootNode();
     Node actual = root.getNode("addfile");
     assertNotNull(actual);
     assertTrue(actual.isNodeType("nt:file"));
     
     addFile("addfile2/hierarchy");
     root.refresh(false);
     assertNotNull(root.getNode("addfile2/hierarchy"));     
     
     addFile("addfile3/with/no/ancestors");
     root.refresh(false);
     assertNotNull(root.getNode("addfile3/with/no/ancestors"));
     
     AssertUtils.assertException(RuntimeException.class, new Closure() {public void dothis() { addFile("/absolute2/addfile/not/allowed");}}); 

     session.logout();     
   }

}
