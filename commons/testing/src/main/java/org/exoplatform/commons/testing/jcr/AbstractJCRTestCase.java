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
package org.exoplatform.commons.testing.jcr;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.commons.testing.AbstractExoContainerTestCase;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;

/**
 * @author <a href="mailto:patrice.lamarque@exoplatform.com">Patrice Lamarque</a>
 * @version $Revision$
 */
@ConfiguredBy({@ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/jcr/jcr-configuration.xml")})
public abstract class AbstractJCRTestCase extends AbstractExoContainerTestCase
{

  private String tempDir;
  
  
  public void beforeContainerStart() {
    initTempDir();
  }

  public void initTempDir() {
    tempDir = "target/temp" + System.nanoTime() + getClass().getCanonicalName();
    System.setProperty("test.tmpdir", tempDir);
  }

  /**
    * 
    * @return The ManageableRepository for this test
    */
   ManageableRepository getRepo() {
     try {
       RepositoryService repos = getComponent(RepositoryService.class);
       ManageableRepository repo = repos.getDefaultRepository();
       return repo;
     } catch (Exception e) {
       throw new RuntimeException(e);
     }
   }

   /**
    * Get the workspace available for test data
    * @return workspace name
    */
   protected String getWorkspace() {
     return getRepo().getConfiguration().getDefaultWorkspaceName();
   }

   /**
    * Get the repository for the current test
    * @return repository name
    */
   protected String getRepository() {
     return getRepo().getConfiguration().getName();
   }

   /**
    * Asserts a node exists at the given path
    * @param path path relative to root of test workspace
    */
   protected void assertNodeExists(String path) {
     Session session = null;
     try {
       session = getSession();
       boolean exists = session.getRootNode().hasNode(path);
       if (!exists) {
         fail("no node exists at " + path);
       }
     } catch (Exception e) {
       throw new RuntimeException("failed to assert node exists", e);
     }
     finally {
       if (session != null) {
         session.logout();
       }
     }
   }
   
   /**
    * Asserts a node does not exists at the given path
    * @param path relative path to root of test workspace
    */
   protected void assertNodeNotExists(String path) {
     Session session = null;
     try {
       session = getSession();
       boolean exists = session.getRootNode().hasNode(path);
       if (exists) {
         fail("node exists at " + path);
       }
     } catch (Exception e) {
       throw new RuntimeException("failed to assert node exists", e);
     }
     finally {
       if (session != null) {
         session.logout();
       }
     }
   }
   
   /**
    * Load a node from path
    * @param path
    * @return
    */
   protected Node getNode(String path) {
     Session session = null;
     try {
       session = getSession();
       return session.getRootNode().getNode(path);
     } catch (Exception e) {
       throw new RuntimeException("failed to load node exists", e);
     }
     finally {
       if (session != null) {
         session.logout();
       }
     }
   }


   /**
    * Get a session on the test workspace
    * @return a new system session
    * @throws Exception
    */
   protected Session getSession() throws Exception {
     try {
       Session session = getRepo().getSystemSession(getWorkspace());
       return session;
     } catch (Exception e) {
       throw new RuntimeException("failed to initiate JCR session on " + getWorkspace(), e);
     }     

   }
   
   /**
    * Add a new node to a given path. intermediary are created if needed.
    * @param path relative path to root
    * @return the newly added node
    */
   protected Node addNode(String path) {
     return addNode(path, null);
   }

   /**
    * Add a new node to a given path. intermediary are created if needed.
    * @param path relative path to root
    * @param nodetype nodetype for the last node to create 
    * @return the newly added node
    */
   protected Node addNode(String path, String nodetype) {
     Session session = null;
     try {
       session = getSession();
       Node parent = session.getRootNode();
       String[] sections = path.split("/");
       for (String section : sections) {
        if (section.length() > 0 && !parent.hasNode(section)) {
          if (nodetype != null && path.endsWith(section)) {
            parent.addNode(section, nodetype); // add child 
          } else {
            parent.addNode(section);
          }
        }
        parent = parent.getNode(section); // jump into
       }
     session.save();
     return parent;
     }
     catch (Exception e) {
       throw new RuntimeException("failed to add node" + path, e);
     }
     finally {
       if (session != null) {
         session.logout();
       }
     }
   }
   
   /**
    * Add a file at a given path. An nt:file is added at 'path' with a sample text/plain jcr:content for the string "stuff"
    * @param path relative path to root
    * @return the node for the newly added file
    */
   protected Node addFile(String path) {
     Session session = null;
     try {
       session = getSession();
       Node parent = session.getRootNode();
       String[] sections = path.split("/");
       for (String section : sections) {
        if (section.length() > 0 && !parent.hasNode(section)) {
          if (path.endsWith(section)) {
            Node ntfile = parent.addNode(section, "nt:file");
            Node nodeContent = ntfile.addNode("jcr:content", "nt:resource");
            nodeContent.setProperty("jcr:mimeType", "text/plain");
            nodeContent.setProperty("jcr:data", new ByteArrayInputStream("stuff".getBytes()));
            nodeContent.setProperty("jcr:lastModified", Calendar.getInstance().getTimeInMillis());           
          } else {
            parent.addNode(section);
          }
        }
        parent = parent.getNode(section); // jump into
       }
     session.save();
     return parent;
     }
     catch (Exception e) {
       throw new RuntimeException("failed to add node" + path, e);
     }   
   }

   /**
    * removes a node at a given path
    * @param path relative path from root
    */
  protected void deleteNode(String path) {
    Session session = null;
    try {
      session = getSession();
      Node parent = session.getRootNode();
      Node target = parent.getNode(path);
      target.remove();
      session.save();
    } catch (Exception e) {
      throw new RuntimeException("failed to remove node" + path, e);
    } finally {
      if (session != null) {
        session.logout();
      }
    }
  }

  /**
   * Asserts a node has a property
   * @param node note to assert
   * @param property propertyName
   */
  protected void assertPropertyExists(Node node, String property) {
    try {
      assertTrue("Node misses property " + property, node.hasProperty(property)) ;
    } catch (RepositoryException e) {
      throw new RuntimeException(e);
    }
  }   
  
  
  /**
   * Asserts a property value is not empty
   * @param node
   * @param property
   */
  protected void assertBinaryPropertyNotEmpty(Node node, String property) {
    assertPropertyExists(node, property);

    try {
      InputStream is = node.getProperty(property).getStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuffer buff = new StringBuffer();
      String str;
      while ((str = reader.readLine()) != null) {
        buff.append(str);
      }
      reader.close();
      assertTrue("property " + property + " was empty", buff.length() > 0);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  protected void assertPropertyEquals(String expected, Node node, String property) {
    try {
      assertEquals(expected, node.getProperty(property).getString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected void assertPropertyEquals(boolean expected, Node node, String property) {
    try {
      assertEquals(expected, node.getProperty(property).getBoolean());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
   
   
}
