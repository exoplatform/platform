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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.Property;

import org.exoplatform.portal.application.UserGadgetStorage;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class UserGadgetStorageImpl implements UserGadgetStorage {

  public static final String GADGETS_REGISTRY_NODE = "gadgets";
  public static final String DATA = "data";
  public static final String GADGET_NODE_TYPE = "exo:gadget";
  private NodeHierarchyCreator nodeCreator ;

  public UserGadgetStorageImpl(NodeHierarchyCreator creator) throws Exception{
    nodeCreator = creator ;
  }

  private Node createGadgetInstanceNode(SessionProvider sessionProvider, String userName, String gadgetType, String instanceId) throws Exception{
    Node userApplicationsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    Node gadgetsApp = getNode(userApplicationsNode, GADGETS_REGISTRY_NODE, null) ;
    Node gadgetsTypeNode = getNode(gadgetsApp, gadgetType, null);
    Node node = getNode(gadgetsTypeNode, instanceId, GADGET_NODE_TYPE);
    userApplicationsNode.save();
    return node;
  }

  public void save(String userName, String gadgetType, String instanceId, String key, String value) throws Exception{
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = createGadgetInstanceNode(sessionProvider, userName, gadgetType, instanceId);

    gadgetNode.setProperty(key, value);
    gadgetNode.save();
    sessionProvider.close() ;
  }

  public void save(String userName, String gadgetType, String instanceId, Map<String, String> values) throws Exception{
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();

    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instanceId);
    if(gadgetNode == null)
      gadgetNode = createGadgetInstanceNode(sessionProvider, userName, gadgetType, instanceId);

    for (String key : values.keySet()) {
      gadgetNode.setProperty(key, values.get(key));
    }
    gadgetNode.save();
    sessionProvider.close();
  }
  
  public String get(String userName, String gadgetType, String instanceId, String key) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instanceId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return null;
    }
     
    String value = null;
    if(gadgetNode.hasProperty(key)) value = gadgetNode.getProperty(key).getString();
    sessionProvider.close() ;
    return value;
  }

  public Map<String, String> get(String userName, String gadgetType, String instanceId) throws Exception {
    return get(userName, gadgetType, instanceId, (Set<String>)null);
  }

  public Map<String, String> get(String userName, String gadgetType, String instanceId, Set<String> keys) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instanceId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return null;
    }
    Map<String, String> res = new HashMap<String, String>();

    if (keys == null) {
      PropertyIterator it = gadgetNode.getProperties();
      while (it.hasNext()) {
        Property prop = it.nextProperty();
        res.put(prop.getName(), prop.getString());
      }
    }
    else {
      for (String key : keys) {
        if(gadgetNode.hasProperty(key))
          res.put(key, gadgetNode.getProperty(key).getString());
      }
    }
    sessionProvider.close() ;
    return res;
  }

  public void delete(String userName, String gadgetType, String instanceId) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instanceId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return;
    }
    Node parentNode = gadgetNode.getParent();
    gadgetNode.remove() ;
    parentNode.save();
    sessionProvider.close() ;
  }

  public void delete(String userName, String gadgetType, String instanceId, Set<String> keys) throws Exception {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider() ;
    Node gadgetNode = getGadgetNode(sessionProvider, userName, gadgetType, instanceId);
    if(gadgetNode == null ) {
      sessionProvider.close() ;
      return;
    }

    for (String key : keys) {
      if (gadgetNode.hasProperty(key))
        gadgetNode.getProperty(key).remove();
    }

    gadgetNode.save();
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

  private Node getGadgetNode(SessionProvider sessionProvider, String userName, String gadgetType, String instanceId) throws Exception {
    Node userAppsNode = nodeCreator.getUserApplicationNode(sessionProvider, userName) ;
    String instancePath = GADGETS_REGISTRY_NODE + "/" + gadgetType + "/" + instanceId ;
    if(userAppsNode.hasNode(instancePath)) return userAppsNode.getNode(instancePath) ;
    return null;
  }
}