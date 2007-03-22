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
public class PersonToJSON extends ObjectToJSONConverterPlugin {

  public <T> void toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, T object, StringBuilder b, int indentLevel) {
    Person person = (Person) object;
    b = b.append("{\n");
    b.append(getIndenLevelString(indentLevel));
    b.append("\"Name\": \"" + person.getName() + "\",\n"  );
    b.append(getIndenLevelString(indentLevel));
    b.append("\"Age\": \"" + person.getAge() + "\"\n"  );
    b.append("}");
  }
  private String getIndenLevelString(int n){
    char c = ' ';
    String s = "";
    for(int i = 0; i< n; i ++) s = s + c;
    return s;
    }

}
