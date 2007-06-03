/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config.model;

import java.util.HashMap;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 2, 2007  
 */
@SuppressWarnings("serial")
public class Properties extends HashMap<String, String> {
  
  public Properties() {
    super(10);
  }
  
  public Properties(int size) {
    super(size);
  }
  
  public int getIntValue(String key) { 
    String value = super.get(key);
    if(value == null || value.trim().length() < 1) return -1;
    return Integer.valueOf(value.trim());
  }
  
  public double getDoubleValue(String key) { 
    String value = super.get(key);
    if(value == null || value.trim().length() < 1) return -1.0;
    return Double.valueOf(value.trim());
  }
  
  public void put(String key, int value) {
    super.put(key, String.valueOf(value));
  }
  
  public void put(String key, double value) {
    super.put(key, String.valueOf(value));
  }
  
  public String setProperty(String key, String value) {
    return put(key, value);
  }

}
