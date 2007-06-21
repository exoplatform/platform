/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.common.ThreadSoftRef;
import org.exoplatform.services.token.TypeToken;
import org.exoplatform.services.token.attribute.Attribute;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.Attributes;
import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public class Bean2XML implements XMLMapper {
  
  static ThreadSoftRef<Bean2XML> MAPPER = new ThreadSoftRef<Bean2XML>(Bean2XML.class);
  
  public final static Bean2XML getInstance() { return MAPPER.getRef(); } 
  
  public XMLDocument toXMLDocument(Object bean) throws Exception {
    NodeMap map = bean.getClass().getAnnotation(NodeMap.class);
    if(map == null) return null;
    XMLNode node = new XMLNode(map.value().toCharArray(), map.value(), TypeToken.TAG);
    toXML(bean, node);
    return new XMLDocument(node);
  }

  public void toXML(Object bean, XMLNode node)  throws Exception  {
    NodeMap map = bean.getClass().getAnnotation(NodeMap.class);
    if (map == null) return;
    toXMLValue(bean.getClass(), map.depth(), bean, node);
  }
  
  private void toXMLValue(Class<?> clazz, int depth, Object bean, XMLNode node)  throws Exception  {
    Field [] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      if(field.getType().isArray()) {
        toArray(field, bean, node);
      } else if (Collection.class.isAssignableFrom(field.getType())) {
        toCollection(field, bean, node);
      } else {
        toField(field, bean, node);
      }
    }
    depth--;
    if(depth <= 0 || clazz.getSuperclass() == null) return;    
    toXMLValue(clazz.getSuperclass(), depth, bean, node);
  }
  
  protected void toField(Field field, Object bean, XMLNode node) throws Exception {
    if(bean == null) return;
    NodeMap map = field.getAnnotation(NodeMap.class);
    if(map == null) return;
    
    String name = map.value();    
    boolean isAttribute = map.attribute();      
    
    Class<?> type = field.getType();
    Object value = null;
    field.setAccessible(true);
  
    if(type.isPrimitive()) {
      try{
        value = getValue(type, field, bean);
      }catch (Exception e) {
      }     
      if(isAttribute){
        setAttribute(node, name, (String)value); 
      } else {
        setChild(node, name, (String)value);
      }
      return;
    }
    
    value = getValue(field, bean);
    
    if(XMLSerialize.REFLECT_UTIL.getRef().isPrimitiveType(type)) {
      if(value == null) value = "";
      if(isAttribute) {
        setAttribute(node, name, value.toString());
      } else {
        setChild(node, name, value.toString());
      }
      return;
    }
    
    XMLMapper mapper = XMLSerialize.getInstance().getXMLMapper(type);
    if(mapper == null) {    
      if(value == null) value = "";
      if(isAttribute) {
        setAttribute(node, name, value.toString());
      } else {
        setChild(node, name, value.toString());
      }
      return;
    }
    
    XMLNode child = new XMLNode(name.toCharArray(), name, TypeToken.TAG);   
    node.getChildren().add(child);
    NodeMap valueMap = value.getClass().getAnnotation(NodeMap.class);
    if(valueMap == null) {
      toXMLValue(value.getClass(), 1, value, child);
      return;
    }
    XMLNode valueNode  = new XMLNode(valueMap.value().toCharArray(), valueMap.value(), TypeToken.TAG);
    child.addChild(valueNode);
    toXMLValue(value.getClass(), map.depth(), value, valueNode);
  }
  
  private void toCollection(Field field, Object bean, XMLNode parent) {
    NodesMap map = field.getAnnotation(NodesMap.class);
    if(map == null) return;
    
    field.setAccessible(true);
    String name = map.value(); 
    XMLNode node = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
    parent.getChildren().add(node);
    
    if(bean == null) return;
   
    Object values = getValue(field, bean); 
    
    Collection<?> collection = (Collection<?>) values;
    Object [] array = new Object[collection.size()];
    collection.toArray(array);
    toArray(node, array, map);
  }
  
  private void toArray(Field field, Object bean, XMLNode parent) {
    NodesMap map = field.getAnnotation(NodesMap.class);
    if(map == null) return;
    
    field.setAccessible(true);
    String name = map.value(); 
    XMLNode node = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
    parent.getChildren().add(node);
    
    if(bean == null) return;
    
    Object values = getValue(field, bean);    
    toArray(node, values, map);
  }
  
  private void toArray(XMLNode node, Object values, NodesMap map) {
    if(values == null) return;
    int length = Array.getLength(values);
    Class<?> type;
    for(int i = 0; i < length; i++) {
      String itemName = map.item();
      Object value = Array.get(values, i);
      if(value == null) continue;
      type = value.getClass();

      XMLNode child = new XMLNode(itemName.toCharArray(), itemName, TypeToken.TAG);
      node.getChildren().add(child);
      if(type.isPrimitive()  || XMLSerialize.REFLECT_UTIL.getRef().isPrimitiveType(type)) {
        child.addChild(new XMLNode(value.toString().toCharArray(), null, TypeToken.CONTENT));
        continue;
      }
      XMLMapper mapper = XMLSerialize.getInstance().getXMLMapper(value.getClass());
      if(mapper == null) {
        child.addChild(new XMLNode(value.toString().toCharArray(), null, TypeToken.CONTENT));
        continue;
      }
      
      try {  
        NodeMap valueMap = value.getClass().getAnnotation(NodeMap.class);
        if(valueMap == null) {
          mapper.toXML(value, child);
          return;
        }
        XMLNode valueNode  = new XMLNode(valueMap.value().toCharArray(), valueMap.value(), TypeToken.TAG);
        child.addChild(valueNode);        
        mapper.toXML(value, valueNode);
      }catch (Exception e) {
      }
    }
  }

  private Object getValue(Field field, Object bean) {
    Object value = null;
    try {
      Method method = XMLSerialize.REFLECT_UTIL.getRef().getGetterMethod(bean.getClass(), field);
      if(method != null) value = method.invoke(bean);
    } catch (Exception e) {
    }
    if(value != null)  return value;
    try {
      value = field.get(bean);
    }catch (Exception e) {
      value = null;
    }
    return value;
  }
  
  private String getValue(Class<?> type, Field field, Object bean) throws Exception {
    try{
      Method method = XMLSerialize.REFLECT_UTIL.getRef().getGetterMethod(bean.getClass(), field);
      if(method != null) return String.valueOf(method.invoke(bean));
    }catch (Exception e) {
    }
    if(type == boolean.class) return String.valueOf(field.getBoolean(bean));
    if(type == byte.class) return String.valueOf(field.getByte(bean));
    if(type == char.class) return String.valueOf(field.getChar(bean));
    if(type == short.class) return String.valueOf(field.getShort(bean));
    if(type == int.class) return String.valueOf(field.getInt(bean));
    if(type == long.class) return String.valueOf(field.getLong(bean));
    if(type == float.class) return String.valueOf(field.getFloat(bean));
    if(type == double.class) return String.valueOf(field.getDouble(bean));
    return "";
  }
  
  private void setChild(XMLNode parent, String name, String value) {
    List<XMLNode> children = parent.getChildren();
    for(XMLNode ele : children) {
      if(ele.getName().equals(name)){
        ele.addChild(new XMLNode(value.toCharArray(), null, TypeToken.CONTENT));
        return;
      }
    }   
    XMLNode node = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
    node.addChild(new XMLNode(value.toCharArray(), null, TypeToken.CONTENT));
    children.add(node);
  }
  
  private void setAttribute(XMLNode parent, String name, String value) {
    Attributes attrs = AttributeParser.getAttributes(parent);
    for(Attribute ele : attrs) {
      if(ele.getName().equals(name)){
        ele.setValue(value);
        return ;
      }
    }
    Attribute attr = new Attribute(name, value);
    attrs.set(attr);    
  }
  
}
