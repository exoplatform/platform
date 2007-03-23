package org.exoplatform.jsun.test;

/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Sep 25, 2006  
 */
public class ReflectUtil extends Clazz {
  
//  public int[] int_x2 = {3, 2, 11};
//  int inttt = 4;
////  public  long long_x3_ = 3;
//  private int kk = 32;
//  String ddd  = "eeeeeeeeeeeeeeeeeee";
//  String[] ss = {"asdf", "123431", ";lkj"};
//  
  private  void  getField(Class c, List<Field> allFields) {
    Field[] publicFields = c.getDeclaredFields();
    for(Field f: publicFields ) allFields.add(f);
    if(c.getSuperclass() == null) return;
    if(!c.getSuperclass().isInstance(new Object()));
      getField(c.getSuperclass(), allFields);
  }
  
   void getFieldNames(Object o) throws Exception {
    Class c = o.getClass();
    List<Field> allFields = new ArrayList<Field>();
    getField(c, allFields);
    for( Field f: allFields) {
      f.setAccessible(true);
      Class typeClass = f.getType();
      if(typeClass.isArray()) {
//        System.out.println(">>>>>>>>>Array " + f.getName());
        arrayToJSON( f, o);
      } else {
//        System.out.println(">>>>>>>>>Single " + f.getName());
        getJSONString (f, o);
      }
    }
  }
   private void getJSONString(  Field f, Object o) throws Exception {
//     if(f.get)
     Class cla = f.getType();
     int[] array = (int[]) o ;
//     f.get
     if(cla == int.class){
       System.out.println("int: " + f.getName() + " -- "+ (Integer)f.get(o)); 
     }else if (cla == long.class) {
       System.out.println("long: "+ f.getName()  + " -- " + (Long)f.get(o));
     }else if (cla == String.class){
     System.out.println("String: " + f.getName() + " -- " + (String)f.get(o));
     }else {
       getFieldNames(f.get(o));
     }
   }

   private void arrayToJSON(Field f, Object o) throws Exception {
     Object object = f.get(o);
     for (int i = 0; i < Array.getLength(object); i++) {
       
       Object value =  Array.get(object, i);
//       System.out.println(">>>>>>>>>Array " + f.getName()+ " > " + value.getClass().getName());
       if(value.getClass() == Integer.class){
         System.out.println("int: " + (Integer)value); 
       }else if (value.getClass() == Long.class) {
         System.out.println("long: " + (Long)value);
       }else if (value.getClass() == String.class){
         System.out.println("String: " + (String)value);
       }else {
         getFieldNames(value);
       }
    }

   }  
  public static Object createObject(Class clazz, Class types[], Object[] args) throws Exception {
    Constructor constructor = clazz.getConstructor(types);    
    Object object = constructor.newInstance(args);    
    return object;   
  }

  public static void main(String arg[]) throws Exception {
    ReflectUtil reflectUtil = new ReflectUtil();
    Teacher teacher = new Teacher("John", 33, "java");
    Student student = new Student("Mary", 14, "abc");
    Student student2 = new Student("Jack", 13, "abc");
    Student student3 = new Student("Peter", 15, "abc");
    Student[] students = {student, student2, student3};
    reflectUtil.setStudents(students);
    reflectUtil.setTeacher(teacher);
    reflectUtil.setName("asdsf");
    
    reflectUtil.getFieldNames(reflectUtil);
  }
}