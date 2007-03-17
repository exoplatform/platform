/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.xml.object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.services.parser.attribute.Attribute;
import org.exoplatform.services.parser.attribute.AttributeParser;
import org.exoplatform.services.parser.attribute.Attributes;
import org.exoplatform.services.parser.xml.XMLNode;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 18, 2006
 */
public class XML2Object {

  private ReflectUtil reflection;

  public XML2Object(){
    reflection = new ReflectUtil();
  }
  
  public <T> T createObject(XMLNode node, Class<T> clazz) throws Exception {    
    T object = clazz.newInstance();
    return createObject(node, clazz, object);
  }

  @SuppressWarnings("unchecked")
  public <T> T createObject(XMLNode node, Class<T> clazz, T object) throws Exception {    
    Attributes attrs = AttributeParser.getAttributes(node);
    Field [] fields = clazz.getDeclaredFields();
    List<XMLNode> children = node.getChildren();

    NodeMap map; 
    String name;
    boolean conti = false;
    for(Field field : fields){    
      map = field.getAnnotation(NodeMap.class);
      if(map != null) 
        name = map.value();
      else 
        name = field.getName();
      Class type = field.getType();
      conti = false;
      for(Attribute attr : attrs){
        if(!attr.getName().equals(name)) continue;
        try{
          setValue(object, field, attr.getValue());
        }catch (Exception e) {
          Object obj = reflection.createValue(type, attr.getValue());
          setValue(object, field, type.cast(obj));
        }
        conti = true;
        break;         
      }
      if(conti) continue;

      for(XMLNode child : children){        
        if(!child.getName().equalsIgnoreCase(name)) continue;
        if(reflection.isPrimaty(type)) {
          if(child.getChildren().size() > 0
              && child.getChildren().get(0).getValue() != null) {
            try{
              setValue(object, field, new String(child.getChildren().get(0).getValue()));                
            }catch (Exception e) {
              System.out.println(e.getMessage());
            }
          }else if(type == String.class){
            setValue(object, field, "");
          }
        } else if(field.getType().isArray()){         
          try{            
            setValue(object, field, createArray(field.getType().getComponentType(), child));            
          }catch (Exception e) { 
          }
        } else {  
          try{
            setValue(object, field, createObject(child, type));
          }catch (Exception e) {
          }
        }
      }
    }  
    return object;
  }

  private void setValue(Object obj, Field field, Object value) throws Exception{
    if(!field.getType().isArray()) {      
      value = reflection.createValue(field.getType(), value); 
    }

    Exception rExp = null;  
    try{
      Method method = reflection.getSetterMethod(obj.getClass(), field);
      if(method != null) method.invoke(obj, value);
      return;
    }catch (Exception e) {
      rExp = e;
    }

    try{ 
      if(!field.isAccessible()) field.setAccessible(true);
      field.set(obj, value); 
      return;
    }catch(Exception exp){
    }    
    if(rExp == null) rExp = new Exception("Cann't setter method for "+field.getName());
    throw rExp;
  }

  @SuppressWarnings("unchecked")
  private Object createArray(Class type, XMLNode child) throws Exception {
    int size = child.getChildren().size();
    Object values = Array.newInstance(type, size);
    for(int i = 0; i < size; i++){     
      if(reflection.isPrimaty(type)){        
        String value = new String(child.getChild(i).getChild(0).getValue());
        Array.set(values, i, reflection.createValue(type, value));        
      }else{
        Array.set(values, i, createObject(child.getChild(i), type));
      }
    }
    return values;
  }

}
