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
public class Teacher extends Person{
  private String subject;
  public Teacher(String n,  int a, String s) {
    super(n, a);
    subject = s;
  }
  public String getSubject() {return subject; }
  public void setSubject(String c) {subject = c;}
}
