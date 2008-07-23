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
package org.exoplatform.portal.application.jcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.jcr.Node;

import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.portal.application.UserGadgetStorage;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

public class UserGadgetStorageImpl implements UserGadgetStorage {

  public static final String GADGETS_REGETSTRY_NODE = "gadgets";
  public static final String DATA = "data";
  public static final String GADGET_NODE_TYPE = "exo:gadget";
  private NodeHierarchyCreator nodeCreator ;

  public UserGadgetStorageImpl(NodeHierarchyCreator creator) throws Exception{   
    nodeCreator = creator ;
  }

  private Node createGadgetInstanceNode(SessionProvider sessionProvider, String userName, String gadgetType, String instanceId) throws Exception{
    Node userApplicationsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    Node gadgetsApp = getNode(userApplicationsNode, GADGETS_REGETSTRY_NODE, null) ;
    Node gadgetsTypeNode = getNode(gadgetsApp, gadgetType, null);
    Node node = getNode(gadgetsTypeNode, instanceId, GADGET_NODE_TYPE);
    userApplicationsNode.save();
    return node;
  }

  public void save(String userName, String gadgetType, String instantId, Object data) throws Exception{
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = createGadgetInstanceNode(sessionProvider, userName, gadgetType, instantId);
    InputStream  inputStream;
    if(data instanceof byte[]) {
      inputStream  = new ByteArrayInputStream((byte[])data);
    } else if (data instanceof InputStream){
      inputStream = (InputStream) data;
    } else {
      inputStream  = new ByteArrayInputStream(data.toString().getBytes());
    }
    gadgetNode.setProperty(DATA, inputStream);
    gadgetNode.save();
    sessionProvider.close() ;
  }
  
  public Object get(String userName, String gadgetType, String instantId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instantId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return null;
    }
    byte[] bytes =  IOUtil.getStreamContentAsBytes(gadgetNode.getProperty(DATA).getStream());
    sessionProvider.close() ;
    return bytes;
  }

  public void delete(String userName, String gadgetType, String instantId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instantId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return;
    }
    Node parentNode = gadgetNode.getParent();
    gadgetNode.remove() ;
    parentNode.save();
    sessionProvider.close() ;
  }

  private Node getNode(Node parent, String name, String nodeType) throws Exception {
    if(parent.hasNode(name)){
      return parent.getNode(name);
    }
    Node node = null;
    if(nodeType == null) node= parent.addNode(name);
    else node = parent.addNode(name, nodeType);
    parent.save();
    return node;
  }

  private Node getGadgetNode(SessionProvider sessionProvider, String userName, String gadgetType, String instantId) throws Exception {
    Node userAppsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    String instancePath = GADGETS_REGETSTRY_NODE + "/" + gadgetType + "/" + instantId ;
    if(userAppsNode.hasNode(instancePath)) return userAppsNode.getNode(instancePath) ;
    return null;
  }
}