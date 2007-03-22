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
public class Person {
  private String name;
  private int age;
  
  public Person(String n, int a) {
    name = n;
    age = a;
  }
  public void setName(String n ){name = n; }
  public String getName() { return name; }
  
  public void setAge(int a) {age = a; }
  public int getAge() {return age; }
  
}
