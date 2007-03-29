/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.application;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Oct 26, 2006
 */
public class Parameter {
  
  private String name;
  private String value;
  
  public Parameter(String name, String value) {
    this.name = name;
    this.value = value;
  }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }

}
