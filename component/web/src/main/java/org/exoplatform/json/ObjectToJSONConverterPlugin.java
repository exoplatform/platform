/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;

import java.util.HashMap;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 20, 2007  
 */
abstract public class ObjectToJSONConverterPlugin {
  abstract public <T> void  toJSONScript(HashMap<Class, ObjectToJSONConverterPlugin> converterPlugins, 
                                                 T object, StringBuilder b, int indentLevel) ;
  
  
  protected void appendIndentation(StringBuilder b,  int indentLevel) {
    
  }
}
