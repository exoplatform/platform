/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
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
      System.out.println("\n\n\n == > "+type+"\n\n");
      if (isPrimitiveType(type)) {
        builder.append(value).append(',').append('\n');
      } else if (isCharacterType(type)){
        builder.append('\'').append(encode(value.toString())).append('\'').append(',').append('\n');
      } else if (isDateType(type)) { 
        toDateValue(builder, value);  
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
