/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.jsun.test;

import org.exoplatform.json.JSONService;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 22, 2007  
 */
public class TestJSONService {
  JSONService service_  ;
  public void setUp() throws Exception {
    if(service_ !=null)  return;
    service_ = new JSONService();
  }
  
  
  public void testJSONService () {
    TearchToJSON tearchToJSON = new TearchToJSON();
    StudentToJSON studentToJSON = new StudentToJSON();
    ClassToJSON classToJSON = new ClassToJSON();
    service_.register(Teacher.class, tearchToJSON);
    service_.register(Student.class, studentToJSON);
    service_.register(Clazz.class, classToJSON);
    StringBuilder b = new StringBuilder();
    Teacher teacher = new Teacher("John", 33, "java");
    Student student = new Student("Mary", 14, "abc");
    Student student2 = new Student("Jack", 13, "abc");
    Student student3 = new Student("Peter", 15, "abc");
    Student[] students = {student, student2, student3};
    Clazz clazz = new Clazz(students, teacher, "abc");
    try {
      service_.toJSONScript(clazz, b, 2);
    }catch (Exception e) {
      
    }
    
    System.out.println("\n\n\n***************************************************************");
    System.out.println(b + "\n\n");
//    b = new StringBuilder();
//    service_.toJSONScript(student, b, 2);
//    System.out.println(b + "\n\n");
//    b = new StringBuilder();
//    service_.toJSONScript(person, b, 2);
//    System.out.println(b + "\n\n");
    System.out.println("***************************************************************\n\n\n");
  }
  
  public static void main(String args[]) throws Exception {
    TestJSONService test = new TestJSONService();
    test.setUp();
    test.testJSONService();
  }
}
