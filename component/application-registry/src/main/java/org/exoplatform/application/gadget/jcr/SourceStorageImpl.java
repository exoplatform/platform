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

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Session;

import org.exoplatform.application.gadget.SourceStorage;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Aug 6, 2008  
 */
public class SourceStorageImpl implements SourceStorage {

  public String getSource(String name) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node homeNode = getHomeNode(sessionProvider) ;
    String source = homeNode.getNode(name + ".xml" + "/jcr:content").getProperty("jcr:data").getString() ;
    sessionProvider.close() ;
    return source ;
  }

  public void saveSource(String name, String source) throws Exception {
//    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
//    RepositoryService repoService = (RepositoryService) PortalContainer.getComponent(RepositoryService.class) ; 
//    Session session = sessionProvider.getSession("collaboration", repoService.getRepository("repository")) ;
//    NodeHierarchyCreator nodeCreator = (NodeHierarchyCreator) PortalContainer.getComponent(NodeHierarchyCreator.class) ;
//    Node homeNode = (Node)session.getItem(nodeCreator.getJcrPath("GadgetSources"));
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RepositoryService repoService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    Session session = sessionProvider.getSession("gadgets", repoService.getRepository("repository")) ;
    Node homeNode = session.getRootNode() ;
    Node contentNode ;
    String fileName = name + ".xml" ;
    if(!homeNode.hasNode(fileName)) {
      Node fileNode = homeNode.addNode(fileName, "nt:file") ;
      contentNode = fileNode.addNode("jcr:content", "nt:resource") ;
    } else contentNode = homeNode.getNode(fileName + "/jcr:content") ;
    contentNode.setProperty("jcr:data", source) ;
    contentNode.setProperty("jcr:mimeType", "text/xml") ;
    contentNode.setProperty("jcr:lastModified", Calendar.getInstance()) ;
    session.save() ;
    sessionProvider.close() ;
  }

  public void removeSource(String name) throws Exception {
//    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
//    RepositoryService repoService = (RepositoryService) PortalContainer.getComponent(RepositoryService.class) ; 
//    Session session = sessionProvider.getSession("collaboration", repoService.getRepository("repository")) ;
//    NodeHierarchyCreator nodeHi = (NodeHierarchyCreator) PortalContainer.getComponent(NodeHierarchyCreator.class) ;
//    Node homeNode = (Node)session.getItem(nodeHi.getJcrPath("GadgetSources"));
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    RepositoryService repoService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    Session session = sessionProvider.getSession("gadgets", repoService.getRepository("repository")) ;
    Node homeNode = session.getRootNode() ;
    Node sourceNode = homeNode.getNode(name) ;
    sourceNode.remove() ;
    session.save() ;
    sessionProvider.close() ;
  }

  public String getSourceLink(String name) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node homeNode = getHomeNode(sessionProvider) ;
    String link = "rest/public/jcr/repository/gadgets" + homeNode.getNode(name + ".xml").getPath() ;
    sessionProvider.close() ;
    return link ;
//    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
//    Node homeNode = getHomeNode(sessionProvider) ;
//    String link = "rest/public/jcr/repository/collaboration" + homeNode.getNode(name + ".xml").getPath() ;
//    sessionProvider.close() ;
//    return link ;
  }

  private Node getHomeNode(SessionProvider sessionProvider) throws Exception {
    RepositoryService repoService = (RepositoryService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RepositoryService.class);
    Session session = sessionProvider.getSession("gadgets", repoService.getRepository("repository")) ;
    return session.getRootNode() ; 

//    RepositoryService repoService = (RepositoryService) PortalContainer.getComponent(RepositoryService.class) ; 
//    Session session = sessionProvider.getSession("collaboration", repoService.getRepository("repository")) ;
//    NodeHierarchyCreator nodeHi = (NodeHierarchyCreator) PortalContainer.getComponent(NodeHierarchyCreator.class) ;
//    return (Node)session.getItem(nodeHi.getJcrPath("GadgetSources")) ; 
  }

}
