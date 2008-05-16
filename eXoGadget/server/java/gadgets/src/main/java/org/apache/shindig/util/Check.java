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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.equalTo;
import org.hamcrest.StringDescription;

import java.util.Collection;
import java.util.Map;

/**
 * Provides methods for checking conditions and arguments, throwing an
 * appropriate {@code RuntimeException} when the checks fail.
 * The main goal of this class is to ensure that appropriate exception types
 * with clear messages are thrown while minimizing code duplication.
 *
 * <p>Use this class instead of {@code assert} when you want to enforce that
 * the check is always performed. These are designed for testing arguments
 * and preconditions that make up a method's contract.
 *
 * <p>Example:
 *
 * <pre class="code">public void setContentData(String contentData) {
 *  Check.eq(contentType, ContentType.HTML,
 *           "getContentData() only valid for HTML gadgets");
 *  Check.notNull(contentData);
 *  this.contentData = contentData;
 *}</pre>
 *
 * <p>A generic check that can use any {@link org.hamcrest.Matcher} is provided
 * a la JUnit. Various matchers can be created using static methods from
 * {@link org.hamcrest.Matchers}.
 *
 * <pre class="code">Check.that(string, containsString("foo"));
 *Check.that(string, anyOf(startsWith("foo"), endsWith("bar")));</pre>
 */
public final class Check {
    /**
     * Private constructor for this class, do not allow instantiation
     */
    private Check() {
    }

    /**
   * Checks that {@code condition} is {@code true}.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static void is(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }

  /**
   * Checks that {@code condition} is {@code true}.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static void is(boolean condition, String message, Object... args) {
    if (!condition) {
      throw new IllegalStateException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code first} equals {@code second} using
   * {@link org.hamcrest.Matchers#equalTo}.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static void eq(Object first, Object second, String message) {
    that(first, equalTo(second), message);
  }

  /**
   * Checks that {@code first} equals {@code second} using
   * {@link org.hamcrest.Matchers#equalTo}.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static void eq(Object first, Object second, String message,
                        Object... args) {
    that(first, equalTo(second), String.format(message, args));
  }

  /**
   * Checks that {@code actual} matches {@code matcher} from Hamcrest.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static <T> void that(T actual, Matcher<T> matcher) {
    that(actual, matcher, "");
  }

  /**
   * Checks that {@code actual} matches {@code matcher} from Hamcrest.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static <T> void that(T actual, Matcher<T> matcher, String message) {
    if (!matcher.matches(actual)) {
      Description description = new StringDescription();
      if (message != null && message.length() > 0) {
        description.appendText(message).appendText(" - ");
      }
      description.appendValue(actual).appendText(" must be ");
      matcher.describeTo(description);
      throw new IllegalStateException(matcher.toString());
    }
  }

  /**
   * Checks that {@code actual} matches {@code matcher} from Hamcrest.
   *
   * @throws IllegalStateException if the check fails.
   */
  public static <T> void that(T actual, Matcher<T> matcher, String message,
                              Object... args) {
    that(actual, matcher, String.format(message, args));
  }

  /**
   * Checks that {@code ref} is not {@code null}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notNull(Object ref) {
    if (ref == null) {
      throw new IllegalArgumentException("Object must be non-null");
    }
  }

  /**
   * Checks that {@code ref} is not {@code null}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notNull(Object ref, String message) {
    if (ref == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code ref} is not {@code null}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notNull(Object ref, String message, Object... args) {
    if (ref == null) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code ref} is a (or a subclass of) {@code clazz}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void isA(Object ref, Class<?> clazz) {
    if (ref != null && !(clazz.isAssignableFrom(ref.getClass()))) {
      throw new IllegalArgumentException(
          String.format("Object must be an instance of %s", clazz));
    }
  }

  /**
   * Checks that {@code ref} is a (or a subclass of) {@code clazz}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void isA(Object ref, Class<?> clazz, String message) {
    if (ref != null && !(clazz.isAssignableFrom(ref.getClass()))) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code ref} is a (or a subclass of) {@code clazz}.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void isA(Object ref, Class<?> clazz, String message,
                         Object... args) {
    if (ref != null && !(clazz.isAssignableFrom(ref.getClass()))) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code string} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(String string) {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException("String must be non-empty");
    }
  }

  /**
   * Checks that {@code string} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(String string, String message) {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code string} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(String string, String message, Object... args) {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code array} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Object[] array) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException("Array must be non-empty");
    }
  }

  /**
   * Checks that {@code array} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Object[] array, String message) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code array} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Object[] array, String message, Object... args) {
    if (array == null || array.length == 0) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code collection} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   *         or empty.
   */
  public static void notEmpty(Collection<?> collection) {
    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException("Collection must be non-empty");
    }
  }

  /**
   * Checks that {@code collection} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   *         or empty.
   */
  public static void notEmpty(Collection<?> collection, String message) {
    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code collection} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   *         or empty.
   */
  public static void notEmpty(Collection<?> collection, String message,
                              Object... args) {
    if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code map} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Map<?, ?> map) {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException("Map must be non-empty");
    }
  }

  /**
   * Checks that {@code map} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Map<?, ?> map, String message) {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks that {@code map} is not {@code null} and not empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void notEmpty(Map<?, ?> map, String message, Object... args) {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException(String.format(message, args));
    }
  }

  /**
   * Checks that {@code array} contains no {@code null} elements.
   * It is not an error for the argument itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void noNulls(Object[] array) {
    if (array != null && array.length > 0) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(
          "Array must contain only non-null elements");
        }
      }
    }
  }

  /**
   * Checks that {@code array} contains no {@code null} elements.
   * It is not an error for the argument itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void noNulls(Object[] array, String message) {
    if (array != null && array.length > 0) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  /**
   * Checks that {@code array} contains no {@code null} elements.
   * It is not an error for the argument itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static void noNulls(Object[] array, String message, Object... args) {
    if (array != null && array.length > 0) {
      for (Object element : array) {
        if (element == null) {
          throw new IllegalArgumentException(String.format(message, args));
        }
      }
    }
  }

  /**
   * Checks that {@code iterable} contains no {@code null} elements.
   * It is not an error for {@code iterable} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <E> void noNulls(Iterable<E> iterable) {
    if (iterable != null) {
      for (E element : iterable) {
        if (element == null) {
          throw new IllegalArgumentException(
              "Iterable must contain only non-null elements");
        }
      }
    }
  }

  /**
   * Checks that {@code iterable} contains no {@code null} elements.
   * It is not an error for {@code iterable} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <E> void noNulls(Iterable<E> iterable, String message) {
    if (iterable != null) {
      for (E element : iterable) {
        if (element == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  /**
   * Checks that {@code iterable} contains no {@code null} elements.
   * It is not an error for {@code iterable} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <E> void noNulls(Iterable<E> iterable, String message,
                                 Object... args) {
    if (iterable != null) {
      for (E element : iterable) {
        if (element == null) {
          throw new IllegalArgumentException(String.format(message, args));
        }
      }
    }
  }

  /**
   * Checks that {@code map} contains no {@code null} keys or values.
   * It is not an error for {@code map} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <K, V> void noNulls(Map<K, V> map) {
    if (map != null) {
      for (Map.Entry<K, V> entry : map.entrySet()) {
        if (entry.getKey() == null || entry.getValue() == null) {
          throw new IllegalArgumentException(
              "Map must contain only non-null keys and values");
        }
      }
    }
  }

  /**
   * Checks that {@code map} contains no {@code null} elements.
   * It is not an error for {@code map} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <K, V> void noNulls(Map<K, V> map, String message) {
    if (map != null) {
      for (Map.Entry<K, V> entry : map.entrySet()) {
        if (entry.getKey() == null || entry.getValue() == null) {
          throw new IllegalArgumentException(message);
        }
      }
    }
  }

  /**
   * Checks that {@code map} contains no {@code null} elements.
   * It is not an error for {@code map} itself to be {@code null} or empty.
   *
   * @throws IllegalArgumentException if the check fails.
   */
  public static <K, V> void noNulls(Map<K, V> map, String message,
                                    Object... args) {
    if (map != null) {
      for (Map.Entry<K, V> entry : map.entrySet()) {
        if (entry.getKey() == null || entry.getValue() == null) {
          throw new IllegalArgumentException(String.format(message, args));
        }
      }
    }
  }
}
