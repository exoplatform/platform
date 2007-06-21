/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  
 *    
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.exoplatform.services.common.ThreadSoftRef;
import org.exoplatform.services.token.attribute.Attribute;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.Attributes;
import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 13, 2007
 */
public class XML2Bean implements BeanMapper {
  
  static ThreadSoftRef<XML2Bean> MAPPER = new ThreadSoftRef<XML2Bean>(XML2Bean.class);
  
  public final static XML2Bean getInstance() { return MAPPER.getRef(); } 
  
  public <T> T toBean(Class<T> clazz, XMLDocument document) throws Exception {
    NodeMap map = clazz.getAnnotation(NodeMap.class);
    if (map == null) return null;
    String name = map.value();
    T object = clazz.newInstance();
    XMLNode node = searchNode(document.getRoot(), name);
    if(node == null) return null;
    toBean(clazz, object, node);
    return object;
  }
  
  private XMLNode searchNode(XMLNode node, String name) {
    if(node.isNode(name)) return node;
    List<XMLNode> children = node.getChildren();
    if(children == null) return null;
    for(XMLNode ele :  children) {
      XMLNode value = searchNode(ele, name);
      if(value != null) return value;
    }
    return null;
  }
  
  public <T> T toBean(Class<T> clazz, XMLNode node) throws Exception {
    T object = clazz.newInstance();
    toBean(clazz, object, node);
    return object;
  }
  
  public <T> void toBean(Class<T> clazz, T object, XMLNode node) throws Exception {    
    if(clazz != object.getClass()) throw new Exception("Incompatipable type for object and class");
    NodeMap map = clazz.getAnnotation(NodeMap.class);
    if (map == null) return ;
    toXMLValue(clazz, object, node);
  }
  
  private void toXMLValue(Class<?> clazz, Object bean, XMLNode node)  throws Exception  {
    Attributes attrs = AttributeParser.getAttributes(node);
    for(Attribute attr : attrs) {
      Field field = getField(attr.getName(), clazz);
      if(field == null) continue;
      Class<?> type = field.getType();
      if(type.isPrimitive() || XMLSerialize.REFLECT_UTIL.getRef().isPrimitiveType(type)) {
        Object data = toValue(type, attr.getValue());
        putField(bean, field, data);
      }
    }
    
    List<XMLNode> children = node.getChildren();
    if(children == null) return ;
    for(XMLNode ele :  children) {
      if(node.getChildren() == null || node.getChildren().size() < 1) return;
      Field field = getField(ele.getName(), clazz);
      if(field == null) continue;
      Object data = toValue(bean, field, ele);
      if(data == null) continue;
      putField(bean, field, data);
    }
  }
  
  private void putField(Object bean, Field field, Object data) throws Exception {
    try {
      Method method = XMLSerialize.REFLECT_UTIL.getRef().getSetterMethod(bean.getClass(), field);
      method.setAccessible(true);
      method.invoke(bean, new Object[]{data});
    }catch (Exception e) {      
    }
    field.setAccessible(true);
    field.set(bean, data);
  }
  
  @SuppressWarnings("unchecked")
  private Object toValue(Object bean, Field field, XMLNode node) throws Exception {
    Class<?> type = field.getType();
    
    field.setAccessible(true);
    Object current= field.get(bean);
    if(current != null) type = current.getClass();
    
    if(Collection.class.isAssignableFrom(type)) {
      Type eleParamType = Object.class;
      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
      if(paramType.getActualTypeArguments().length > 0) {
        eleParamType = paramType.getActualTypeArguments()[0];
      }      
      Object [] array =  (Object [])toArrayValues((Class<?>)eleParamType, node);
      Collection collection = null;
      
      if(current != null && current instanceof Collection) {
        collection = (Collection) current;
      } else if(type.isInterface())  {
        collection = new ArrayList<Object>();
      } else {
        collection = (Collection) type.newInstance();
      }
      Collections.addAll(collection, array);
      return collection;
    } 
    
    if(type.isArray()) return toArrayValues(type, node);
    
    return toValue(type, node);
  }
  
  private Object toArrayValues(Class<?> type, XMLNode node) throws Exception {
    List<XMLNode> children = node.getChildren();
    if(children == null) return new Object[]{};
    Class<?> elementType = type;
    if(type.isArray()) elementType = type.getComponentType();
    Object  array = Array.newInstance(elementType, children.size());
    
    for(int i = 0; i < children.size(); i++) {
      if(children.get(i).getChildren() == null || children.get(i).getChildren().size() < 1) {
        Array.set(array, i, null);
        continue;
      }
      XMLNode elementNode  = children.get(i);
      if(elementType.isPrimitive() || XMLSerialize.REFLECT_UTIL.getRef().isPrimitiveType(elementType)) {
        Array.set(array, i, toValue(elementType, new String(elementNode.getChild(0).getValue())));
      } else {
        Array.set(array, i, toValue(elementType, elementNode)); 
      }
    }
    return array;
  }
  
  private Object toValue(Class<?> type, XMLNode node) throws Exception {
    if(type.isPrimitive() || XMLSerialize.REFLECT_UTIL.getRef().isPrimitiveType(type)) {
      String value = new String(node.getChild(0).getValue());
      return toValue(type, value);
    }
    
    NodeMap valueMap = type.getAnnotation(NodeMap.class);
    Object newBean = type.newInstance();
    toXMLValue(type, newBean, valueMap == null ? node : node.getChild(0));
    return newBean;  
  }
  
  private Object toValue(Class<?> type, String value) {
    if(type == String.class || value == null) return value;
    if(type == StringBuffer.class) return new StringBuffer(value);
    if(type == StringBuilder.class) return new StringBuilder(value);
    value = value.trim();
    if(type == char.class || type  == Character.class) {
      if(value.length() < 1) return new Character(' ');
      return new Character(value.trim().charAt(0));
    }
    
    if(type == byte.class || type == Byte.class) return new Byte(value);
    if(type == boolean.class || type == Boolean.class) return new Boolean(value);
    
    if(type == short.class || type == Short.class) return new Short(value);
    if(type == int.class || type == Integer.class) return new Integer(value);
    if(type == long.class || type == Long.class) return new Long(value);
    if(type == float.class || type == Float.class) return new Float(value);
    if(type == double.class || type == Double.class) return new Double(value);
    
    return value;
  }
  
  private Field getField(String name, Class<?> clazz) throws Exception {
    Field [] fields = clazz.getDeclaredFields();
    for(Field field : fields) {
      NodeMap map = field.getAnnotation(NodeMap.class);
      if(map != null && name.equals(map.value())) return field;
      NodesMap maps = field.getAnnotation(NodesMap.class);
      if(maps != null && name.equals(maps.value())) return field;
    }
    if(clazz.getSuperclass() == null) return null;    
    return getField(name, clazz.getSuperclass());
  }
  
}
