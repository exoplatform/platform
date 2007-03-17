/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.xml.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Oct 18, 2006  
 */
class ReflectUtil {

  Object createValue(Class type, Object value) throws Exception {    
    String name = type.getSimpleName();
    if(name.equals("char")){
      return String.valueOf(value).toCharArray()[0];
    }else if(name.equals("int")){
      name  = "Integer";
    }else {
      name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    name = "java.lang."+name;
    Class clazz = getClass().getClassLoader().loadClass(name);
    return clazz.getConstructor(String.class).newInstance(value);
  }

  Method getSetterMethod(Class clazz, Field field) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    String name = null;
    if(map != null){
      name = map.value();
      Method [] methods = clazz.getMethods();    
      for(Method ele : methods){
        SetterMap getterMap = ele.getAnnotation(SetterMap.class);      
        if(getterMap == null) continue;
        if(getterMap.value().equals(name)) return ele;
      }     
    }    
    name = getSetterOrGetter('s', field.getName());
    Method method = clazz.getMethod(name);
    return method;
  }

  Method getGetterMethod(Class clazz, Field field) throws Exception {
    NodeMap map = field.getAnnotation(NodeMap.class);
    String name = null;
    if(map != null){
      name = map.value();
      Method [] methods = clazz.getMethods();    
      for(Method ele : methods){
        GetterMap getterMap = ele.getAnnotation(GetterMap.class);      
        if(getterMap == null) continue;
        if(getterMap.value().equals(name)) return ele;
      }     
    }    
    name = getSetterOrGetter('g', field.getName());
    Method method = clazz.getMethod(name);
    return method;
  }

  String getSetterOrGetter(char c, String name){
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

  boolean isPrimaty(Class type){
    if(type.isPrimitive()) return true;
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
