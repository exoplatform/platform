/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.social.opensocial.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts pojos to json objects
 */
public class BeanJsonConverter {

  private static final Object[] EMPTY_OBJECT = {};
  private static final String EXCLUDED_GETTER = "class";
  private static final Pattern GETTER = Pattern.compile("^get([a-zA-Z]+)$");

  /**
   * Convert the object to {@link JSONObject} reading Pojo properties
   *
   * @param pojo The object to convert
   * @return A JSONObject representing this pojo
   */
  public JSONObject convertToJson(Object pojo) {
    JSONObject toReturn = new JSONObject();
    Method[] methods = pojo.getClass().getMethods();
    for (Method method : methods) {
      String errorMessage = "Could not encode the " + method + " method.";
      try {
        putAttribute(pojo, toReturn, method);
      } catch (JSONException e) {
        throw new RuntimeException(errorMessage, e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(errorMessage, e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(errorMessage, e);
      }
    }
    return toReturn;
  }

  /**
   * Convert java declared method and its value to an entry in the given
   * {@link JSONObject}
   *
   * @param pojo The pojo being translated
   * @param object the json object to put the field value in
   * @param method the method to encode
   * @throws JSONException thrown exception
   * @throws IllegalAccessException thrown exception
   * @throws InvocationTargetException thrown exception
   */
  private void putAttribute(Object pojo, JSONObject object,
      Method method) throws JSONException, IllegalAccessException,
      InvocationTargetException {
    Matcher matcher = GETTER.matcher(method.getName());
    if (!matcher.matches()) {
      return;
    }

    String name = matcher.group();
    String fieldName = name.substring(3, 4).toLowerCase() + name.substring(4);
    if (fieldName.equalsIgnoreCase(EXCLUDED_GETTER)) {
      return;
    }

    Object val = method.invoke(pojo, EMPTY_OBJECT);
    if (val != null) {
      object.put(fieldName, translateObjectToJson(val));
    }
  }

  private Object translateObjectToJson(Object val) throws JSONException {
    if (val instanceof Object[]) {
      JSONArray array = new JSONArray();
      for (Object asd : (Object[]) val) {
        array.put(translateObjectToJson(asd));
      }
      return array;

    } else if (val instanceof List) {
      JSONArray list = new JSONArray();
      for (Object item : (List) val) {
        list.put(translateObjectToJson(item));
      }
      return list;

    } else if (val instanceof Map) {
      JSONObject map = new JSONObject();
      Map originalMap = (Map) val;

      for (Object item : originalMap.keySet()) {
        map.put(item.toString(), translateObjectToJson(originalMap.get(item)));
      }
      return map;

    } else if (val instanceof String
        || val instanceof Boolean
        || val instanceof Integer
        || val instanceof Date
        || val instanceof Long
        || val instanceof Enum
        || val instanceof Float) {
      return val;
    }

    return convertToJson(val);
  }
}
