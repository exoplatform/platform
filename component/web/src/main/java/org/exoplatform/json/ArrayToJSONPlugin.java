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

import java.lang.reflect.Array;


/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class ArrayToJSONPlugin extends BeanToJSONPlugin<Object>  {

  public void toJSONScript(Object objects, StringBuilder builder, int indentLevel) throws Exception {
    if(!objects.getClass().isArray()) throw new Exception("Object isn't intanceOf array");
    boolean newBuilder = builder.length() < 3;
    if(newBuilder) {
      indentLevel = indentLevel + 1;
      appendIndentation(builder, indentLevel);
      String name = "";
      if(Array.getLength(objects) > 0){
        name = Array.get(objects, 0).getClass().getSimpleName();
      }else{
        name = objects.getClass().getSimpleName();
      }
      builder.append('\'').append(name).append("s\': ");
    }
    if(objects instanceof byte[]){
      toJSON((byte[])objects, builder);
    } 
    else if(objects instanceof short[]) {
      toJSON((short[])objects, builder);
    } 
    else if(objects instanceof int[]) {
      toJSON((int[])objects, builder);
    } 
    else if(objects instanceof long[]) {
      toJSON((long[])objects, builder);
    } 
    else if(objects instanceof float[]) {
      toJSON((float[])objects, builder);
    } 
    else if(objects instanceof double[]) {
      toJSON((double[])objects, builder);
    }
    else if(objects instanceof boolean[]) {
      toJSON((boolean[])objects, builder);
    }
    else if(objects instanceof char[]) {
      toJSON((char[])objects, builder);
    } else {
      objectToJSON((Object[])objects, builder, indentLevel);
    }
    if(newBuilder) appendIndentation(builder, indentLevel);
  }

  @SuppressWarnings("unchecked")
  private <T> void primitiveToJSON(Object object, StringBuilder builder) throws Exception {
    T[] array = (T[])object;
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  @SuppressWarnings("unchecked")
  private <T> void charsToJSON(Object object, StringBuilder builder, int indentLevel) throws Exception {
    T[] array = (T[])object;
    builder.append('[').append('\n');
    for (int i = 0; i < array.length; i++) {
      appendIndentation(builder, indentLevel + 1);
      builder.append('\'').append(encode(String.valueOf(array[i]))).append('\'');
      if(i != array.length - 1) builder.append(',');
      builder.append('\n');
    }
    appendIndentation(builder, indentLevel);
    builder.append(']').append(',').append('\n');
  }

  @SuppressWarnings("unchecked")
  public <T> void objectToJSON(Object object, StringBuilder builder, int indentLevel) throws Exception {
    Class type  = object.getClass().getComponentType();
    if(isPrimitiveType(type)){
      primitiveToJSON(object, builder);
      return;
    }

    if (isCharacterType(type)) {
      charsToJSON(object, builder, indentLevel);
      return;
    }

    T[] array = (T[])object;
    builder.append('[').append('\n');
    for (int i = 0; i < array.length; i++) {
      BeanToJSONPlugin plugin = service_.getConverterPlugin(array[i]);
      plugin.toJSONScript(array[i], builder, indentLevel+1);
      if(i != array.length - 1) builder.append(',');
      appendIndentation(builder, indentLevel+1);
      builder.append('\n');
    }
    appendIndentation(builder, indentLevel);
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(int[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(short[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(byte[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(long[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(float[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(double[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(boolean[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append(array[i]);
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

  private void toJSON(char[] array, StringBuilder builder) throws Exception {
    builder.append('[');
    for (int i = 0; i < array.length; i++) {
      if(i != 0) appendIndentation(builder, 1);
      builder.append('\'').append(encode(String.valueOf(array[i]))).append('\'');
      if(i != array.length - 1) builder.append(',');
    }
    builder.append(']').append(',').append('\n');
  }

}
