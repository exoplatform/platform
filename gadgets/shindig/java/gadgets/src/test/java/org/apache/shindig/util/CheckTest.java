/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.util;

import junit.framework.JUnit4TestAdapter;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckTest {
  static final String STATIC_MESSAGE = "message";

  static final String MESSAGE_FORMAT = "the %s message";
  static final int MESSAGE_VALUE = 42;
  static final String FORMATTED_MESSAGE = "the 42 message";

  static final String ARG_NAME = "arg";

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(CheckTest.class);
  }

  @Test(expected = IllegalStateException.class)
  public void is_Message_ThrowsWhenFalse() {
    Check.is(false, STATIC_MESSAGE);
  }

  @Test(expected = IllegalStateException.class)
  public void is_MessageArgs_ThrowsWhenFalse() {
    Check.is(false, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void is_Message_ReturnsWhenTrue() {
    try {
      Check.is(true, STATIC_MESSAGE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test public void is_MessageArgs_ReturnsWhenTrue() {
    try {
      Check.is(true, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test(expected = IllegalStateException.class)
  public void eq_Message_ThrowsWhenNotEqual() {
    Check.eq(2, 3, STATIC_MESSAGE);
  }

  @Test(expected = IllegalStateException.class)
  public void eq_MessageArgs_ThrowsWhenNotEqual() {
    Check.eq("bob", "chuck", MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void eq_Message_ReturnsWhenEqual() {
    try {
      Check.eq("frank", "franko".substring(0, 5), STATIC_MESSAGE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test public void eq_MessageArgs_ReturnsWhenEqual() {
    try {
      Check.eq('c', 'c', MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test(expected = IllegalStateException.class)
  public void that_ThrowsWhenFalse() {
    Check.that("bob", equalTo("frank"));
  }

  @Test(expected = IllegalStateException.class)
  public void that_Message_ThrowsWhenFalse() {
    Check.that("bob", equalTo("frank"), STATIC_MESSAGE);
  }

  @Test(expected = IllegalStateException.class)
  public void that_MessageArgs_ThrowsWhenFalse() {
    Check.that("bob", equalTo("frank"), MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void that_ReturnsWhenTrue() {
    try {
      Check.that(23, equalTo(23), STATIC_MESSAGE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test public void that_Message_ReturnsWhenTrue() {
    try {
      Check.that(23, equalTo(23), STATIC_MESSAGE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test public void that_MessageArgs_ReturnsWhenTrue() {
    try {
      Check.that(23, equalTo(23), MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalStateException e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notNull_ThrowsWhenNotNull() {
    Check.notNull(null);
  }

  @Test public void notNull_ReturnsWhenNull() {
    try {
      Check.notNull(24);
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notNull_Message_ThrowsWhenNotNull() {
    Check.notNull(null, STATIC_MESSAGE);
  }

  @Test public void notNull_Message_ReturnsWhenNull() {
    try {
      Check.notNull("bob", STATIC_MESSAGE);
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notNull_MessageArgs_ThrowsWhenNotNull() {
    Check.notNull(null, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void notNull_MessageArgs_ReturnsWhenNull() {
    try {
      Check.notNull("bob", MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_ThrowsWhenNull() {
    String string = null;
    Check.notEmpty(string);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_ThrowsWhenEmpty() {
    String string = "";
    Check.notEmpty(string, ARG_NAME);
  }

  @Test public void notEmptyString_ReturnsWhenNotEmpty() {
    try {
      String string = "full";
      Check.notEmpty(string, ARG_NAME);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_Message_ThrowsWhenNull() {
    String string = null;
    Check.notEmpty(string, STATIC_MESSAGE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_Message_ThrowsWhenEmpty() {
    String string = "";
    Check.notEmpty(string, STATIC_MESSAGE);
  }

  @Test public void notEmptyString_Message_ReturnsWhenNotEmpty() {
    try {
      String string = "full";
      Check.notEmpty(string, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_MessageArgs_ThrowsWhenNull() {
    String string = null;
    Check.notEmpty(string, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyString_MessageArgs_ThrowsWhenEmpty() {
    String string = "";
    Check.notEmpty(string, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void notEmptyString_MessageArgs_ReturnsWhenNotEmpty() {
    try {
      String string = "full";
      Check.notEmpty(string, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_ThrowsWhenNull() {
    Object[] array = null;
    Check.notEmpty(array);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_ThrowsWhenEmpty() {
    Object[] array = new Object[0];
    Check.notEmpty(array);
  }

  @Test public void notEmptyObjectArray_ReturnsWhenNotEmpty() {
    try {
      Object[] array = new Object[1];
      Check.notEmpty(array);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_Message_ThrowsWhenNull() {
    Object[] array = null;
    Check.notEmpty(array, STATIC_MESSAGE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_Message_ThrowsWhenEmpty() {
    Object[] array = new Object[0];
    Check.notEmpty(array, STATIC_MESSAGE);
  }

  @Test public void notEmptyObjectArray_Message_ReturnsWhenNotEmpty() {
    try {
      Object[] array = new Object[1];
      Check.notEmpty(array, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_MessageArgs_ThrowsWhenNull() {
    Object[] array = null;
    Check.notEmpty(array, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyObjectArray_MessageArgs_ThrowsWhenEmpty() {
    Object[] array = new Object[0];
    Check.notEmpty(array, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void notEmptyObjectArray_MessageArgs_ReturnsWhenNotEmpty() {
    try {
      Object[] array = new Object[1];
      Check.notEmpty(array, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_ThrowsWhenNull() {
    Collection<String> collection = null;
    Check.notEmpty(collection);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_ThrowsWhenEmpty() {
    Collection<String> collection = new ArrayList<String>();
    Check.notEmpty(collection);
  }

  @Test public void notEmptyCollection_ReturnsWhenNotEmpty() {
    try {
      Collection<String> collection = new ArrayList<String>();
      collection.add(null);
      Check.notEmpty(collection);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_Message_ThrowsWhenNull() {
    Collection<String> collection = null;
    Check.notEmpty(collection, STATIC_MESSAGE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_Message_ThrowsWhenEmpty() {
    Collection<String> collection = new ArrayList<String>();
    Check.notEmpty(collection, STATIC_MESSAGE);
  }

  @Test public void notEmptyCollection_Message_ReturnsWhenNotEmpty() {
    try {
      Collection<String> collection = new ArrayList<String>();
      collection.add(null);
      Check.notEmpty(collection, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_MessageArgs_ThrowsWhenNull() {
    Collection<String> collection = null;
    Check.notEmpty(collection, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyCollection_MessageArgs_ThrowsWhenEmpty() {
    Collection<String> collection = new ArrayList<String>();
    Check.notEmpty(collection, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void notEmptyCollection_MessageArgs_ReturnsWhenNotEmpty() {
    try {
      Collection<String> collection = new ArrayList<String>();
      collection.add(null);
      Check.notEmpty(collection, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_ThrowsWhenNull() {
    Map<String, String> map = null;
    Check.notEmpty(map);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_ThrowsWhenEmpty() {
    Map<String, String> map = new HashMap<String, String>();
    Check.notEmpty(map);
  }

  @Test public void notEmptyMap_ReturnsWhenNotEmpty() {
    try {
      Map<String, String> map = new HashMap<String, String>();
      map.put(null, null);
      Check.notEmpty(map);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_Message_ThrowsWhenNull() {
    Map<String, String> map = null;
    Check.notEmpty(map, STATIC_MESSAGE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_Message_ThrowsWhenEmpty() {
    Map<String, String> map = new HashMap<String, String>();
    Check.notEmpty(map, STATIC_MESSAGE);
  }

  @Test public void notEmptyMap_Message_ReturnsWhenNotEmpty() {
    try {
      Map<String, String> map = new HashMap<String, String>();
      map.put(null, null);
      Check.notEmpty(map, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_MessageArgs_ThrowsWhenNull() {
    Map<String, String> map = null;
    Check.notEmpty(map, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void notEmptyMap_MessageArgs_ThrowsWhenEmpty() {
    Map<String, String> map = new HashMap<String, String>();
    Check.notEmpty(map, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void notEmptyMap_MessageArgs_ReturnsWhenNotEmpty() {
    try {
      Map<String, String> map = new HashMap<String, String>();
      map.put(null, null);
      Check.notEmpty(map, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_ReturnsWhenNull() {
    try {
      Object[] array = null;
      Check.noNulls(array);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_ReturnsWhenEmpty() {
    try {
      Object[] array = new Object[0];
      Check.noNulls(array);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      Object[] array = new Object[] { "bob", "frank" };
      Check.noNulls(array);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsObjectArray_ThrowsWhenHasANull() {
    Object[] array = new Object[] { "bob", null };
    Check.noNulls(array);
  }

  @Test public void noNullsObjectArray_Message_ReturnsWhenNull() {
    try {
      Object[] array = null;
      Check.noNulls(array, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_Message_ReturnsWhenEmpty() {
    try {
      Object[] array = new Object[0];
      Check.noNulls(array, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_Message_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      Object[] array = new Object[] { "bob", "frank" };
      Check.noNulls(array, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsObjectArray_Message_ThrowsWhenHasANull() {
    Object[] array = new Object[] { "bob", null };
    Check.noNulls(array, STATIC_MESSAGE);
  }

  @Test public void noNullsObjectArray_MessageArgs_ReturnsWhenNull() {
    try {
      Object[] array = null;
      Check.noNulls(array, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_MessageArgs_ReturnsWhenEmpty() {
    try {
      Object[] array = new Object[0];
      Check.noNulls(array, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsObjectArray_MessageArgs_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      Object[] array = new Object[] { "bob", "frank" };
      Check.noNulls(array, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsObjectArray_MessageArgs_ThrowsWhenHasANull() {
    Object[] array = new Object[] { "bob", null };
    Check.noNulls(array, MESSAGE_FORMAT, MESSAGE_VALUE);
  }

  @Test public void noNullsIterable_ReturnsWhenNull() {
    try {
      List<String> iterable = null;
      Check.noNulls(iterable);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_ReturnsWhenEmpty() {
    try {
      List<String> iterable = new ArrayList<String>();
      Check.noNulls(iterable);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      List<String> iterable = new ArrayList<String>();
      iterable.add("bob");
      Check.noNulls(iterable);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsList_ThrowsWhenHasANull() {
    List<String> iterable = new ArrayList<String>();
    iterable.add("bob");
    iterable.add(null);
    Check.noNulls(iterable);
  }

  @Test public void noNullsIterable_Message_ReturnsWhenNull() {
    try {
      List<String> iterable = null;
      Check.noNulls(iterable, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_Message_ReturnsWhenEmpty() {
    try {
      List<String> iterable = new ArrayList<String>();
      Check.noNulls(iterable, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_Message_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      List<String> iterable = new ArrayList<String>();
      iterable.add("bob");
      Check.noNulls(iterable, STATIC_MESSAGE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsList_Message_ThrowsWhenHasANull() {
    List<String> iterable = new ArrayList<String>();
    iterable.add("bob");
    iterable.add(null);
    Check.noNulls(iterable, STATIC_MESSAGE);
  }

  @Test public void noNullsIterable_MessageArgs_ReturnsWhenNull() {
    try {
      List<String> iterable = null;
      Check.noNulls(iterable, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_MessageArgs_ReturnsWhenEmpty() {
    try {
      List<String> iterable = new ArrayList<String>();
      Check.noNulls(iterable, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test public void noNullsList_MessageArgs_ReturnsWhenNotEmptyWithNoNulls() {
    try {
      List<String> iterable = new ArrayList<String>();
      iterable.add("bob");
      Check.noNulls(iterable, MESSAGE_FORMAT, MESSAGE_VALUE);
    } catch (IllegalArgumentException  e) {
      fail();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void noNullsList_MessageArgs_ThrowsWhenHasANull() {
    List<String> iterable = new ArrayList<String>();
    iterable.add("bob");
    iterable.add(null);
    Check.noNulls(iterable, MESSAGE_FORMAT, MESSAGE_VALUE);
  }
}
