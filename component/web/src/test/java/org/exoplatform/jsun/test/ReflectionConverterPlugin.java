/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.jsun.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.json.ObjectToJSONConverterPlugin;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 23, 2007  
 */
public class ReflectionConverterPlugin extends ObjectToJSONConverterPlugin {

  

  @Override
  public <T> void toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                               T object, StringBuilder b, int indentLevel) throws Exception {
//    ObjectToJSONConverterPlugin test = converterPlugins.get(object.getClass()) ;
    List<Field> fields = new ArrayList<Field>();
    getField(object.getClass(), fields );
    appendIndentation(b, indentLevel);
    b.append("{\n");

    //#############################################################################    
    for(Field f: fields) {
      String name = f.getName();
      f.setAccessible(true);
      appendIndentation(b, indentLevel);
      b.append("\"" + name + "\":");
      Class typeClass = f.getType();
      if(typeClass.isArray()) {
        arrayToJSON(converterPlugins, b, indentLevel +1, f, object);
      } else {
        getJSONString (converterPlugins, b, indentLevel +1, f, object);
      }
    }

    //#############################################################################
    
    appendIndentation(b, indentLevel);
    b.append("}\n");
  }

  private void getJSONString(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                              StringBuilder b, int i, Field f, Object o) throws Exception {
    Class cla = f.getType();
//  f.get
    if(cla == Integer.class  || cla == Long.class || cla == Boolean.class 
        || cla == Double.class || cla == Float.class  ){
      appendIndentation(b, i);
//      b.append()
    }else if (cla == String.class){
      System.out.println("String: " + f.getName() + " -- " + (String)f.get(o));
    }else {
    toJSONScript(converterPlugins, f.get(o), b, i+1);
  }
  }

  private void arrayToJSON(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins,
                            StringBuilder b, int i, Field f, Object o) {
    
  }

  private void  getField(Class c, List<Field> allFields) {
    Field[] publicFields = c.getDeclaredFields();
    for(Field f: publicFields ) allFields.add(f);
    if(c.getSuperclass() == null) return;
    if(!c.getSuperclass().isInstance(new Object()));
      getField(c.getSuperclass(), allFields);
  }
  
//  private <T>  getValue(Field f, Class T) {
//    return null;
//  }
  
  
}
