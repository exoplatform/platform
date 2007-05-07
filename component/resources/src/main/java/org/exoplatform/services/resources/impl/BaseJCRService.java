/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources.impl;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.registry.JCRRegistryService;
import org.exoplatform.registry.ServiceRegistry;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.resources.ResourceBundleData;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
abstract class BaseJCRService extends BaseResourceBundleService {
  
  final static String SYSTEM_WS = "production".intern();
  final static String LOCALE =  "locale";
  
  final static String RESOURCE_BUNDLE_TYPE = "exo:resourceBundleData";
  
  final static String ID = "id";
  final static String NAME = "name";
  final static String LANGUAGE = "language";
  final static String COUNTRY = "country";
  final static String VARIANT = "variant";
  final static String RESOUCE_TYPE = "resourceType";
  final static String DATA = "data";
  final static String TYPE = "type";
  
  public BaseJCRService() throws Exception {
    getResourceBundleNode(true);
  }
  
  void resourceBundleToNode(Node node, ResourceBundleData data) throws Exception {
    node.setProperty(ID, data.getId());
    node.setProperty(NAME, data.getName());
    node.setProperty(LANGUAGE, data.getLanguage());
    node.setProperty(COUNTRY, data.getCountry());
    node.setProperty(VARIANT, data.getVariant());
    node.setProperty(RESOUCE_TYPE, data.getResourceType());
    node.setProperty(DATA, data.getData());
    node.setProperty(TYPE, LOCALE);
  }
  
  ResourceBundleData nodeToResourceBundleData(Node node) throws Exception{
    ResourceBundleData data = new ResourceBundleData();
    if(!node.hasProperty(ID)) return null; 
    data.setId(node.getProperty(ID).getString());
    if(!node.hasProperty(NAME)) return null;
    data.setName(node.getProperty(NAME).getString());
    
    if(node.hasProperty(LANGUAGE)) data.setLanguage(node.getProperty(LANGUAGE).getString());
    if(node.hasProperty(COUNTRY)) data.setCountry(node.getProperty(COUNTRY).getString());
    if(node.hasProperty(VARIANT)) data.setVariant(node.getProperty(VARIANT).getString());
    if(node.hasProperty(RESOUCE_TYPE)) data.setResourceType(node.getProperty(RESOUCE_TYPE).getString());
    if(node.hasProperty(DATA)) data.setData(node.getProperty(DATA).getString());
    
    return data;
  }
  
   Node getResourceBundleNode(boolean autoCreate) throws Exception {
    Session session = getSession();
    if(session.getRootNode().hasNode("exo:registry/exo:services/resourceBundle") == true)
    
    return session.getRootNode().getNode("exo:registry/exo:services/resourceBundleService");
    
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    JCRRegistryService service = new JCRRegistryService(repoService);
    ServiceRegistry registry = new ServiceRegistry("resourceBundleService") {
      public void preAction(JCRRegistryService service) throws Exception {
        this.description = "Resource Bundle Service";
      }

      public void postAction(JCRRegistryService service, Node registryNode) throws Exception {}
    };
    service.createServiceRegistry(registry, autoCreate) ;
    Node resultNode = session.getRootNode().getNode("exo:registry/exo:services/resourceBundleService");
    session.logout();
    return resultNode;
   }
  
  Node getNode(Node parentNode, String name, boolean autoCreate) throws Exception {
    if(parentNode.hasNode(name)) return parentNode.getNode(name);
    if(!autoCreate) return null;
    Node node  = parentNode.addNode(name);
    parentNode.save();
    return node;
  }
  
  javax.jcr.Session getSession() throws Exception{
    RepositoryService repoService = (RepositoryService)PortalContainer.getComponent(RepositoryService.class) ;    
    javax.jcr.Session session =  repoService.getRepository().getSystemSession(SYSTEM_WS) ;  
    return session;
  }
  
  Node getNode(Node node, String property, String value) throws Exception {
    if(node.hasProperty(property) && value.equals(node.getProperty(property).getString())) return node;
    NodeIterator iterator = node.getNodes();
    while(iterator.hasNext()){
      Node returnNode = getNode(iterator.nextNode(), property, value);
      if(returnNode != null) return returnNode;
    }
    return null;
  }
  
}
