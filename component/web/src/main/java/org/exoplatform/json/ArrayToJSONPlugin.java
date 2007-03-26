/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.json;


/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class ArrayToJSONPlugin extends BeanToJSONPlugin<Object>  {

  public void toJSONScript(Object object, StringBuilder builder, int indentLevel) throws Exception {
    if(!object.getClass().isArray()) throw new Exception("Object isn't intanceOf array");
    if(object instanceof byte[]){
      toJSON((byte[])object, builder);
    } 
    else if(object instanceof short[]) {
      toJSON((short[])object, builder);
    } 
    else if(object instanceof int[]) {
      toJSON((int[])object, builder);
    } 
    else if(object instanceof long[]) {
      toJSON((long[])object, builder);
    } 
    else if(object instanceof float[]) {
      toJSON((float[])object, builder);
    } 
    else if(object instanceof double[]) {
      toJSON((double[])object, builder);
    }
    else if(object instanceof boolean[]) {
      toJSON((boolean[])object, builder);
    }
    else if(object instanceof char[]) {
      toJSON((char[])object, builder);
    } else {
      objectToJSON((Object[])object, builder, indentLevel);
    }
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
