/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resource.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.exoplatform.services.resources.ResourceBundleData;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 9, 2007  
 */
 public class DataMapper   {
  
  final static String SYSTEM_WS = "production".intern();
  final static String LOCALE =  "locale";
  
  final static String ID = "id";
  final static String NAME = "name";
  final static String LANGUAGE = "language";
  final static String COUNTRY = "country";
  final static String VARIANT = "variant";
  final static String RESOUCE_TYPE = "resourceType";
  final static String DATA = "data";
  final static String TYPE = "type";
  
  public DataMapper() throws Exception {
  }
  
  void map(Node node, ResourceBundleData data) throws Exception {
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
