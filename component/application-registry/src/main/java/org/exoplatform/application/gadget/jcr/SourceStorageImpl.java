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
package org.exoplatform.application.gadget.jcr;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.exoplatform.application.gadget.Source;
import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 6, 2008  
 */
public class SourceStorageImpl implements SourceStorage {
  
  static final private String NT_UNSTRUCTURED = "nt:unstructured";
  static final private String NT_FOLDER = "nt:folder";
  static final private String JCR_DATA = "jcr:data";
  static final private String JCR_MIME = "jcr:mimeType";
  static final private String JCR_ENCODING = "jcr:encoding";
  static final private String JCR_MODIFIED = "jcr:lastModified";
  
  private RepositoryService repoService;
  private String repo;
  private String wsName;
  private String homePath;
  
  public SourceStorageImpl(InitParams params, RepositoryService service) throws Exception {
    PropertiesParam properties = params.getPropertiesParam("location");
    if(properties == null) throw new Exception("The 'location' properties parameter is expected.");
    repo = properties.getProperty("repository");
    wsName = properties.getProperty("workspace");
    homePath = reproduceDirPath(properties.getProperty("store.path"));
    repoService = service;
  }

  /**
   * Overridden method.
   * @param sourcePath
   * @return
   * @see org.exoplatform.application.gadget.SourceStorage#getSource(java.lang.String)
   */
  public Source getSource(String sourcePath) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Session session = sessionProvider.getSession(wsName, repoService.getRepository(repo)) ;
    String fullPath = homePath + sourcePath;
    Node sourceNode;
    try {
       sourceNode = (Node)session.getItem(fullPath + "/jcr:content") ;
    } catch (PathNotFoundException pnfe) {
      sessionProvider.close() ;
      return null;
    }
    String [] strs = sourcePath.split("/");
    String name = strs[strs.length-1];
    Source source = toSource(name, sourceNode);
    sessionProvider.close() ;
    return source ;
  }
  
  /**
   * Overridden method.
   * @param dirPath
   * @param source
   * @throws Exception
   * @see org.exoplatform.application.gadget.SourceStorage#saveSource(java.lang.String, Source)
   */
  public void saveSource(String dirPath, Source source) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    String storePath; 
    if(dirPath == null || dirPath.trim().length() < 1) {
      storePath = homePath;
    } else {
      storePath = homePath + dirPath;
      createStructure(sessionProvider, dirPath);
    }
    Session session = sessionProvider.getSession(wsName, repoService.getRepository(repo)) ;
    Node storeNode = (Node) session.getItem(storePath);
    Node contentNode ;
    String fileName = source.getName();
    if(!storeNode.hasNode(fileName)) {
      Node fileNode = storeNode.addNode(fileName, "nt:file") ;
      contentNode = fileNode.addNode("jcr:content", "nt:resource") ;
    } else contentNode = storeNode.getNode(fileName + "/jcr:content") ;
    map(contentNode,source);
    session.save() ;
    sessionProvider.close() ;
  }

  /**
   * Overridden method.
   * @param sourcePath
   * @throws Exception
   * @see org.exoplatform.application.gadget.SourceStorage#removeSource(java.lang.String)
   */
  public void removeSource(String sourcePath) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Session session = sessionProvider.getSession(wsName, repoService.getRepository(repo)) ;
    String fullPath = homePath + sourcePath;
    Node sourceNode = (Node) session.getItem(fullPath);
    sourceNode.remove() ;
    session.save() ;
    sessionProvider.close() ;
  }

  /**
   * Overridden method.
   * @param sourcePath
   * @return
   * @see org.exoplatform.application.gadget.SourceStorage#getSourceURI(java.lang.String)
   */
  public String getSourceURI(String sourcePath) {
    StringBuilder path = new StringBuilder(30);
    path.append("jcr/").append(repo).append("/")
        .append(wsName).append(homePath).append(sourcePath);
    return path.toString() ;
  }
  
  /**
   * This method will create structure for dirPath
   * @param sessionProvider
   * @param dirPath
   * @throws Exception
   */
  private void createStructure(SessionProvider sessionProvider, String dirPath) throws Exception {
    Session session = sessionProvider.getSession(wsName, repoService.getRepository(repo));
    String [] dirs = dirPath.split("/");
    String parentPath = homePath;
    for(String name : dirs) {
      String path = parentPath + name + "/";
      try {
        Node node = (Node) session.getItem(path);
        if(!node.isNodeType(NT_UNSTRUCTURED) && !node.isNodeType(NT_FOLDER)){
          throw new Exception("Node at " + path + " should be " + NT_UNSTRUCTURED + " or " + NT_FOLDER + " type.");
        }
      } catch (PathNotFoundException pnfe) {
        Node parentNode = (Node) session.getItem(parentPath);
        parentNode.addNode(name, NT_FOLDER);
        parentNode.save();
      }
      parentPath = path;
    }
  }

  /**
   * This method will map from Node object to Source object
   * @param name
   * @param node
   * @return
   * @throws Exception
   */
  private Source toSource(String name, Node node) throws Exception {
    Source source = new Source(name);
    source.setMimeType(node.getProperty(JCR_MIME).getString());
    source.setEncoding(node.getProperty(JCR_ENCODING).getString());
    source.setStreamContent(node.getProperty(JCR_DATA).getStream());
    source.setLastModified(node.getProperty(JCR_MODIFIED).getDate());
    return source;
  }
  
  /**
   * This method will map form Source object to Node object
   * @param node
   * @param source
   * @throws Exception
   */
  private void map(Node node, Source source) throws Exception {
    node.setProperty(JCR_MIME, source.getMimeType());
    node.setProperty(JCR_ENCODING, source.getEncoding());
    node.setProperty(JCR_DATA, source.getStreamContent());
    node.setProperty(JCR_MODIFIED, source.getLastModified());
  }
  
  /**
   * reproduce path, if path don't have '/' at end of string, append '/' at end of string 
   * @param path
   * @return
   */
  private String reproduceDirPath(String path) {
    if(path.endsWith("/")) return path;
    return path + "/";
  }

}