/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 23, 2007  
 */
public class ReflectionConverterPlugin extends ObjectToJSONConverterPlugin {

  @Override
  public <T> void toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                               T object, StringBuilder b, int indentLevel) {
    ObjectToJSONConverterPlugin test = null ;
//    test.toJSONScript(null, null, null, (indentLevel + 1) * 2) ;
   
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
  
  private void getFieldNames(Object o, List<Field> allFields) {
    Class c = o.getClass();
    allFields = new ArrayList<Field>();
    getField(c, allFields);
    Field[] publicFields = c.getDeclaredFields();
  for( Field f: allFields) {
      int modified = f.getModifiers();
      String fieldName = f.getName();
      Class typeClass = f.getType();
      String fieldType = typeClass.getName();
      System.out.println("   " +  fieldType + " : " + fieldName +"; -->" + typeClass.isArray());
    }
  }
}
