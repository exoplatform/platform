/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.jsun.test;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 22, 2007  
 */
public class Student extends Person {
  public Student(String n, int a, String c) {
    super(n, a);
    clazz = c;
    // TODO Auto-generated constructor stub
  }
  private String clazz;
  
  public String getClazz() {return clazz; }
  public void setClazz(String c) {clazz = c;}

}
