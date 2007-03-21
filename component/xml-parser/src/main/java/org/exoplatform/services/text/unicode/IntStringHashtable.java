package org.exoplatform.services.text.unicode;

import java.util.Hashtable;

/**
 * Integer-String hash table. Uses Java Hashtable for now.
 * @author Mark Davis
 */

public class IntStringHashtable {
  
  private String defaultValue;
  private Hashtable<Integer, String> table = new Hashtable<Integer, String>();
  
  public IntStringHashtable (String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void put(int key, String value) {
    if (value == defaultValue) {
      table.remove(key);
      return;
    } 
    table.put(key, value);
  }

  public String get(int key) {
    String value = table.get(key);
    return value == null ? defaultValue : value;
  } 
}
