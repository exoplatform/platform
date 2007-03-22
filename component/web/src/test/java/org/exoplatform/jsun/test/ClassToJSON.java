/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.jsun.test;

import java.util.HashMap;

import org.exoplatform.json.ObjectToJSONConverterPlugin;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 22, 2007  
 */
public class ClassToJSON extends ObjectToJSONConverterPlugin {

  public <T> void toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, T object, StringBuilder b, int indentLevel) {
    Clazz clazz = (Clazz) object;
    b = b.append("{\n");
    b.append(getIndenLevelString(indentLevel));
    b.append("\"Tearch\": " ); 
//        + student.getName() + "\",\n"  );
    TearchToJSON tearchToJSON = (TearchToJSON) converterPlugins.get(Teacher.class);
    tearchToJSON.toJSONScript(converterPlugins, clazz.getTeacher(), b, indentLevel*2 );
    b.append(",\n");
    b.append(getIndenLevelString(indentLevel));
    b.append("\"Student\": [\n");
    StudentToJSON studentToJSON = (StudentToJSON) converterPlugins.get(Student.class);
    for(Student s: clazz.getStudents()) {
      studentToJSON.toJSONScript(converterPlugins, s, b, indentLevel*2 );
      b.append(",\n");
    }
    
    b.append(getIndenLevelString(indentLevel));
    b.append("\"Name\": \"" + clazz.getName() + "\"\n"  );
    b.append("}");
  }
  private String getIndenLevelString(int n){
    char c = ' ';
    String s = "";
    for(int i = 0; i< n; i ++) s = s + c;
    return s;
    }

}
