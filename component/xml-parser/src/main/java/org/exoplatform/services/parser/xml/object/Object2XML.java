/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.xml.object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.exoplatform.services.parser.attribute.Attribute;
import org.exoplatform.services.parser.attribute.AttributeParser;
import org.exoplatform.services.parser.attribute.Attributes;
import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.xml.XMLDocument;
import org.exoplatform.services.parser.xml.XMLNode;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 18, 2006
 */
public class Object2XML {  

  private ReflectUtil reflect;

  public Object2XML(){
    reflect = new ReflectUtil();
  }

  public XMLDocument createDocument(Object t) throws Exception {
    XMLNode element = createElement(t, false, null, null);
    return new XMLDocument(element);
  }

  private XMLNode createElement(Object t, boolean auto, Object parent, String fieldName) throws Exception {
    String name = null;    
    NodeMap map = t.getClass().getAnnotation(NodeMap.class);
    if(map != null){
      name = map.value();
      if(map.auto()) auto = true;
    }else if(fieldName != null){
      name = fieldName;
    } else {
      name = t.getClass().getName();
    }
    
    XMLNode element = new XMLNode(name.toCharArray(), name, TypeToken.TAG);

    if(t.getClass().isArray()){
      int length = Array.getLength(t);
      for(int i = 0; i < length; i++){
        XMLNode ele = createElement(Array.get(t, i), auto, t, null);
        String string = String.valueOf(Array.get(t, i));
        ele.addChild(new XMLNode(string.toCharArray(), null, TypeToken.CONTENT));
        element.addChild(ele);
      }
    }

    Attributes attrs = AttributeParser.getAttributes(element);
    Field [] fields = t.getClass().getDeclaredFields();    

    for(Field field : fields){    
      map = field.getAnnotation(NodeMap.class);
      if(map != null)  name = map.value(); else name = field.getName();
      if(map == null && !auto) continue;      
      Class type = field.getType();
      Object value  = getValue(t, field);
      if(value == null || value == parent) continue;
      if(reflect.isPrimaty(type)) {
        if (map == null || map.isAttribute()){
          attrs.set(new Attribute(name, String.valueOf(value)));
          continue;
        }
        XMLNode child = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
        child.addChild(new XMLNode(String.valueOf(value).toCharArray(), null, TypeToken.CONTENT));
        element.addChild(child); 
        continue;
      }
      if(map!= null && map.auto()) auto = true;      
      element.addChild(createElement(value, auto, t, name));     
    }   
    return element;   
  }

  private Object getValue(Object obj, Field field) throws Exception{    
    Exception rExp = null;  
    try{
      Method method = reflect.getGetterMethod(obj.getClass(), field);
      if(method != null) return method.invoke(obj);
    }catch (Exception e) {
      rExp = e;
    }
    try{ 
      if(!field.isAccessible()) field.setAccessible(true);
      return field.get(obj); 
    }catch(Exception exp){
    }       
    if(rExp == null) rExp = new Exception("Cann't getter method for "+field.getName());
    throw rExp;
  } 
}

