/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.json.ObjectToJSONConverterPlugin;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Mar 23, 2007  
 */
public class ReflectionConverterPlugin extends ObjectToJSONConverterPlugin {

  @Override
  public <T> void toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                               T object, StringBuilder b, int indentLevel) throws Exception {
    List<Field> fields = new ArrayList<Field>();
    getField(object.getClass(), fields );
    appendIndentation(b, indentLevel-1);
    b.append("{\n");

    for(Field f: fields) {
      String name = f.getName();
      f.setAccessible(true);
      appendIndentation(b, indentLevel);
      b.append("\"" + name + "\": ");
      Class typeClass = f.getType();
      if(typeClass.isArray()) {
        arrayToJSON(converterPlugins, b, indentLevel +1, f, object);
      } else {
        getJSONString (converterPlugins, b, indentLevel +1, f, object);
      }
    }
    appendIndentation(b, indentLevel-1);
    b.deleteCharAt(b.length()-2);
    b.append("}\n");
  }

  private void getJSONString(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                              StringBuilder b, int i, Field f, Object object) throws Exception {
    Class cla  = object.getClass();
    
    if(f != null) {
      cla = f.getType();
      object = f.get(object);
    }
    boolean condition = cla == Integer.class || cla == Long.class  || cla == Boolean.class ||
                        cla == Double.class  || cla == Float.class || cla == boolean.class || 
                        cla == long.class    || cla == float.class || cla == double.class  ||
                        cla == byte.class    || cla == int.class;
    
    if(condition ){
      b.append(object + ",\n");
    }else if (cla == String.class || cla == char.class){
      b.append("\"" + object + "\",\n");
    }else {
    toJSONScript(converterPlugins, object, b, i+1);
  }
  }

  private void arrayToJSON(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins,
                            StringBuilder b, int indentLevel, Field f, Object object) throws Exception {
    b.append("[\n");
    object = f.get(object);
    for (int i = 0; i < Array.getLength(object); i++) {
      Object element =  Array.get(object, i);
      getJSONString(converterPlugins, b, indentLevel + 1, null, element);
    }
    appendIndentation(b, indentLevel -1 );
    b.append("],\n");
  }

  private void  getField(Class c, List<Field> allFields) {
    Field[] publicFields = c.getDeclaredFields();
    for(Field f: publicFields ) allFields.add(f);
    if(c.getSuperclass() == null) return;
    if(c != Object.class);
      getField(c.getSuperclass(), allFields);
  }
}