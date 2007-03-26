/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;


/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 20, 2007  
 */
abstract public class BeanToJSONPlugin<T> {
  
  protected JSONService service_;
  
  abstract public void toJSONScript(T object, StringBuilder builder, int indentLevel) throws Exception;
  
  protected void appendIndentation(StringBuilder builder,  int indentLevel) {
    for(int i = 0; i < indentLevel * JSONService.NUMBER_SPACE; i++) builder.append(' ');
  }

  public void setService(JSONService service) { service_ = service; }
  
  
  protected boolean isCharacterType(Class type) {
    return ( type == char.class          ||
            type == String.class         ||
            type == Character.class      ||
            type == StringBuilder.class  ||
            type == StringBuffer.class
          );
  }

  protected boolean isPrimitiveType(Class type) {
    return ( type == int.class    ||
            type == long.class    ||
            type == byte.class    ||
            type == double.class  ||
            type == float.class   ||
            type == boolean.class ||
            type == short.class   ||
    
            type == Integer.class || 
            type == Long.class    || 
            type == Boolean.class ||
            type == Double.class  || 
            type == Float.class   || 
            type == Short.class 
         );
  }

  protected String encode(String value) {
    String charValue = value.toString();
    charValue = charValue.replace("\'", "\\\\\'");
    return charValue.replace("\"", "\\\"");
  }
  
}
