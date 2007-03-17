/***************************************************************************
 * Copyright 2004-2006 The  eXo Platform SARL All rights reserved.  *
 **************************************************************************/
package org.exoplatform.services.parser.html.refs;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
class CharRef {
  
  private int value;
  
  private String name;
  
  CharRef(String n, int v) {
    name = n;
    value = v;
    if (name == null) name = "";
  }
  
  String getName() {
    return name;
  }
  
  void setName(String name) {
    this.name = name;
  }
  int getValue() {
    return value;
  }
  
  void setValue(int value) {
    this.value = value;
  }
  
  int compare(CharRef r) {
    return getName().compareTo(r.getName());
  }        
}
