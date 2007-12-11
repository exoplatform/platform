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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy
 *          lebienthuy@gmail.com
 * Mar 23, 2007  
 */
public class ReflectToJSONPlugin extends BeanToJSONPlugin<Object> {
  
  @SuppressWarnings("unchecked")
  public void toJSONScript(Object object, StringBuilder builder, int indentLevel) throws Exception {
    if(object instanceof Collection){
      Collection collection = (Collection) object;
      Object [] array = new Object[collection.size()];
      collection.toArray(array);
      object = array;
    } 
    
    appendIndentation(builder, indentLevel);
    builder.append('{').append('\n');
    
    if(object.getClass().isArray()) {
      ArrayToJSONPlugin arrayToJSONPlugin = service_.getArrayToJSONPlugin();
      arrayToJSONPlugin.toJSONScript(object, builder, indentLevel);
    } else {
      Field [] fields = object.getClass().getDeclaredFields();
      for(Field field : fields) {
        int modified  = field.getModifiers();
        if(Modifier.isStatic(modified) || Modifier.isTransient(modified)) continue;
        String name = field.getName();
        if(name.startsWith("this")) continue;     
        toJSONString(object, field, builder, indentLevel+1);      
      }
    }
    builder.deleteCharAt(builder.length()-2);
    builder.append('\n');
    appendIndentation(builder, indentLevel);
    builder.append('}');   
  }

  @SuppressWarnings("unchecked")
  private void toJSONString(Object object, Field field, StringBuilder builder, int indentLevel) throws Exception {
    Class type  = field.getType();
    Object value = getValue(object, field);
    if(value  == null) value = new String();
    appendIndentation(builder, indentLevel);
    builder.append('\'').append(field.getName()).append('\'').append(':').append(' ');

    if(type.isArray()){
      ArrayToJSONPlugin arrayToJSONPlugin = service_.getArrayToJSONPlugin();
      arrayToJSONPlugin.toJSONScript(value, builder, indentLevel);
      return;
    }

    if(value instanceof Collection){
      Collection collection = (Collection) value;
      Object [] array = new Object[collection.size()];
      collection.toArray(array);
      ArrayToJSONPlugin arrayToJSONPlugin = service_.getArrayToJSONPlugin();
      arrayToJSONPlugin.toJSONScript(array, builder, indentLevel);
      return;
    } 

    if (isPrimitiveType(type)) {
      builder.append(value).append(',').append('\n');
      return ;
    }

    if (isCharacterType(type)){
      String charValue = encode(value.toString());
      builder.append('\'').append(charValue).append('\'').append(',').append('\n');
      return ;
    }
    
    if (isDateType(type)) { 
      toDateValue(builder, value);
      return ;
    } 
    
    BeanToJSONPlugin plugin = service_.getConverterPlugin(value);
    plugin.toJSONScript(value, builder, indentLevel+1);
  }
 
  private Object getValue(Object bean, Field field) throws Exception {
    Class clazz = bean.getClass();
    Method method = getMethod("get", field, clazz);
    if(method != null) return method.invoke(bean, new Object[]{});
    method = getMethod("is", field, clazz);
    if(method != null) return method.invoke(bean, new Object[]{});
    field.setAccessible(true);
    return field.get(bean);
  }

  private Method getMethod(String prefix, Field field, Class clazz) throws Exception {
    StringBuilder name = new StringBuilder(field.getName());
    name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
    name.insert(0, prefix);
    try{
      Method method = clazz.getDeclaredMethod(name.toString(), new Class[]{});
      return method; 
    }catch (Exception e) {
    }
    return null;
  }

}