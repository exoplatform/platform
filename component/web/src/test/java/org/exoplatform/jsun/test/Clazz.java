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
public class Clazz {
  private Student[] students;
  private Teacher teacher;
  private String name;
  
  public Clazz(){
    
  }
  
  public Clazz(Student[] s, Teacher t, String n) {
    students = s;
    teacher = t;
    name = n;
  }
  
  public Student[] getStudents(){return students; }
  public void setStudents(Student[] s){ students = s; }
  
  public Teacher getTeacher(){return teacher; }
  public void setTeacher(Teacher t) { teacher = t; }
  
  public String getName() { return name; }
  public void setName(String n) { name = n; }
}
