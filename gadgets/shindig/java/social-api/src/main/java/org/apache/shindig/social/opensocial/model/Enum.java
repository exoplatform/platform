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
package org.apache.shindig.social.opensocial.model;

import org.apache.shindig.social.AbstractGadgetData;
import org.apache.shindig.social.Mandatory;


/**
 * see
 * http://code.google.com/apis/opensocial/docs/0.7/reference/opensocial.Enum.html
 *
 * Base class for all Enum objects. This class allows containers to use constants
 * for fields that have a common set of values.
 *
 */
public final class Enum<E extends Enum.EnumKey> extends AbstractGadgetData {
  private String displayValue;
  private E key = null;

  public interface EnumKey {
    @Mandatory
    String getDisplayValue();
  }

  /**
   * public java.lang.Enum for opensocial.Enum.Drinker
   */
  public enum Drinker implements EnumKey {
    HEAVILY("HEAVILY", "Heavily"),
    NO("NO", "No"),
    OCCASIONALLY("OCCASIONALLY", "Occasionally"),
    QUIT("QUIT", "Quit"),
    QUITTING("QUITTING", "Quitting"),
    REGULARLY("REGULARLY", "Regularly"),
    SOCIALLY("SOCIALLY", "Socially"),
    YES("YES", "Yes");

    private final String jsonString;
    private final String displayValue;

    private Drinker(String jsonString, String displayValue) {
      this.jsonString = jsonString;
      this.displayValue = displayValue;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }

    public String getDisplayValue() {
      return displayValue;
    }
  }

  /**
   * public java.lang.Enum for opensocial.Enum.Gender
   */
  public enum Gender implements EnumKey {
    FEMALE("FEMALE", "Female"),
    MALE("MALE", "Male");

    private final String jsonString;
    private final String displayValue;

    private Gender(String jsonString, String displayValue) {
      this.jsonString = jsonString;
      this.displayValue = displayValue;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }

    public String getDisplayValue() {
      return displayValue;
    }
  }

  /**
   * public java.lang.Enum for opensocial.Enum.Smoker
   */
  public enum Smoker implements EnumKey {
    HEAVILY("HEAVILY", "Heavily"),
    NO("NO", "No"),
    OCCASIONALLY("OCCASIONALLY", "Ocasionally"),
    QUIT("QUIT", "Quit"),
    QUITTING("QUITTING", "Quitting"),
    REGULARLY("REGULARLY", "Regularly"),
    SOCIALLY("SOCIALLY", "Socially"),
    YES("YES", "Yes");

    private final String jsonString;
    private final String displayValue;

    private Smoker(String jsonString, String displayValue) {
      this.jsonString = jsonString;
      this.displayValue = displayValue;
    }

    @Override
    public String toString() {
      return this.jsonString;
    }

    public String getDisplayValue() {
      return displayValue;
    }
  }

  /**
   * Constructs a Enum object.
   * @param key EnumKey The key to use
   * @param displayValue String The display value
   */
  public Enum(E key, String displayValue) {
    this.key = key;
    this.displayValue = displayValue;
  }

  /**
   * Constructs a Enum object.
   * @param key The key to use. Will use the value from getDisplayValue() as
   *     the display value.
   */
  public Enum(E key) {
    this(key, key.getDisplayValue());
  }

  /**
   * Gets the value of this Enum. This is the string displayed to the user.
   * If the container supports localization, the string should be localized.
   * @return the Enum's user visible value
   */
  public String getDisplayValue() {
    return this.displayValue;
  }

  /**
   * Sets the value of this Enum. This is the string displayed to the user.
   * If the container supports localization, the string should be localized.
   * @param displayValue The value to set.
   */
  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  /**
   * Gets the key for this Enum.
   * Use this for logic within your gadget.
   * @return java.lang.Enum key object for this Enum.
   */
  public E getKey() {
    return this.key;
  }

  /**
   * Sets the key for this Enum.
   * Use this for logic within your gadget.
   * @param key The value to set.
   */
  public void setKey(E key) {
    this.key = key;
  }

}
