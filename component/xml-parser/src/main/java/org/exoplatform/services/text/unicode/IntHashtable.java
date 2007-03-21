package org.exoplatform.services.text.unicode;
import java.util.Hashtable;

/**
 * Integer hash table. Uses Java Hashtable for now.
 * @author Mark Davis
 */

public class IntHashtable {

  private int defaultValue;

  private Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();

  public IntHashtable (int defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void put(int key, int value) {
    if (value == defaultValue) {
      table.remove(new Integer(key));
      return;
    } 
    table.put(new Integer(key), new Integer(value));
  }

  public int get(int key) {
    Integer value = table.get(key);
    if (value == null) return defaultValue;
    return value;
  }

}
