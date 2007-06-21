/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public class ReflectUtil {
  
  public Method getSetterMethod(Class<?> clazz, Field field) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    Method method = null;
    if(map != null) method = getSetterMethodByMap(clazz, map);
    if(method != null) return method;
    
    String name = getSetterOrGetter('s', field.getName());
    method = getMethodByName(clazz, name);
    if(method != null) return method;
    
    if(map != null) name = map.value();
    method = getMethodByName(clazz, name);
    return method;
  }
  
  public Method getGetterMethod(Class<?> clazz, Field field) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    Method method = null;
    if(map != null) method = getGetterMethodByMap(clazz, map);
    if(method != null) return method;
    
    String name = getSetterOrGetter('g', field.getName());
    method = getMethodByName(clazz, name);
    if(method != null) return method;
    
    if(map != null) name = map.value();
    method = getMethodByName(clazz, name);
    return method;
  }
  
  private Method getSetterMethodByMap(Class<?> clazz, NodeMap map) {
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){      
      SetterMap getterMap = ele.getAnnotation(SetterMap.class);      
      if(getterMap == null) continue;
      if(getterMap.value().equals(map.value())) return ele;
    }
    Method method = getGetterMethodByMap(clazz.getSuperclass(), map);
    if(method != null) return method;
    return null;
  }
  
  private Method getGetterMethodByMap(Class<?> clazz, NodeMap map) {
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){      
      GetterMap getterMap = ele.getAnnotation(GetterMap.class);      
      if(getterMap == null) continue;
      if(getterMap.value().equals(map.value())) return ele;
    }
    Method method = getGetterMethodByMap(clazz.getSuperclass(), map);
    if(method != null) return method;
    return null;
  }
  
  private Method getMethodByName(Class<?> clazz, String name) {
    Method method = getPublicMethodByName(clazz, name);
    if(method != null) return method;
    method = getProtectedMethodByName(clazz, name);
    if(method != null) return method;
    return null;
  }
  
  private Method getPublicMethodByName(Class<?> clazz, String name) {
    Method [] methods = clazz.getMethods();
    for(Method ele : methods){
      if(name.equals(ele.getName())) return ele;
    }
    return null;
  }
  
  private Method getProtectedMethodByName(Class<?> clazz, String name) {
    Method [] methods = clazz.getDeclaredMethods();
    for(Method ele : methods){
      if(ele.isAccessible()) continue;
      if(name.equals(ele.getName())) return ele;
    }
    Method method = getProtectedMethodByName(clazz.getSuperclass(), name);
    if(method != null) return method;
    return null;
  }
  
  private String getSetterOrGetter(char c, String name){
    char [] chars = new char[name.length()+3];
    chars[0] = c;
    chars[1] = 'e';
    chars[2] = 't';
    chars[3] = Character.toUpperCase(name.charAt(0));
    for(int i = 1 ; i<name.length(); i++){
      chars[i+3] = name.charAt(i);
    }
    return new String(chars);
  }
  
  public boolean isPrimitiveType(Class<?> type) {
    return type == Integer.class 
              || type == Long.class
              || type == Boolean.class
              || type == Double.class
              || type == Float.class
              || type == Short.class
              || type == Byte.class
              || type == Short.class
              || type == Character.class
              || type == String.class
              || type == StringBuffer.class
              || type == StringBuilder.class            
              ;
  }
  
}
