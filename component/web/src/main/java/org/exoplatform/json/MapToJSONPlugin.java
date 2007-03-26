/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class MapToJSONPlugin extends BeanToJSONPlugin<Object> {
  
  @SuppressWarnings("unchecked")
  public void toJSONScript(Object object, StringBuilder builder, int indentLevel) throws Exception {
    if(object instanceof Map){
      toJSONScript((Map<String, Object>)object, builder, indentLevel);
    }
    if(object instanceof JSONMap) {
      JSONMap jsonMap = (JSONMap) object;
      toJSONScript(jsonMap.getJSONMap(), builder, indentLevel); 
    }
  }
  
  @SuppressWarnings("unchecked")
  public void toJSONScript(Map<String, Object> map, StringBuilder builder, int indentLevel) throws Exception {
    appendIndentation(builder, indentLevel);
    builder.append('{').append('\n');
    Iterator<String> iterator = map.keySet().iterator();
    while(iterator.hasNext()){
      String key = iterator.next();
      appendIndentation(builder, indentLevel+1);
      builder.append('\'').append(key).append('\'').append(':').append(' ');
      Object value = map.get(key);
      Class type = value.getClass();
      if (isPrimitiveType(type)) {
        builder.append(value).append(',').append('\n');
      } else if (isCharacterType(type)){
        builder.append('\'').append(encode(value.toString())).append('\'').append(',').append('\n');
      } else {
        BeanToJSONPlugin plugin = service_.getConverterPlugin(value);
        plugin.toJSONScript(value, builder, indentLevel+1);
      }
    }
    
    builder.deleteCharAt(builder.length()-2);
    builder.append('\n');
    appendIndentation(builder, indentLevel);
    builder.append('}');   
  }

}
