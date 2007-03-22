package org.exoplatform.jsun.test;

/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.JButton;
import javax.swing.JTextField;

/**
 * Created by The eXo Platform SARL
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Sep 25, 2006  
 */


public class ReflectUtil {
  
  public int int_x2_ = 1;
  public static long long_x3_ = 3;
  public static final double double_x4_ = 4.4; 
  public String string_x5_ = "Cong hoa xa hoi chu nghia viet nam";
  transient boolean boolean_x6_ = true;
  protected JButton button_x7_ = new  JButton("Doc lap tu do hanh phuc");
  
  public ReflectUtil() {    
  }
  
  public ReflectUtil(long x1, int x2) {
    long_x3_ = x1;
    int_x2_ = x2;
  }
  
  public ReflectUtil(JButton button1, String string1, boolean boolean1) {    
    button_x7_ = button1;
    string_x5_ = string1;
    boolean_x6_ = boolean1;
  }
  
  public static void printObjectInfo(Object object){
    int modified = object.getClass().getModifiers();
    
    if(object.getClass().isInterface())
      System.out.println(getStringModified(modified) + " interface " + getClassName(object)+ " { ");
    System.out.println(getStringModified(modified) + " class " + getClassName(object)+ " { ");
    
    System.out.println("\\\\All of Fields.");    
    getFieldNames(object);
    
    System.out.println("\\\\All of Contructors.\n");
    System.out.println(getConstructor(object));
    
    System.out.println("\\\\All of Methods.");
    System.out.println(getStringMethods(object) + "}");   
  }
  
  static String getClassName(Object object) {    
    String result = object.getClass().getName();
    result = result.substring(result.lastIndexOf('.') + 1) ;
    return result;
  }
  
  static String getConstructor(Object object) {
    
    String strContructor = "";
    int modified = object.getClass().getModifiers();
    Class c = object.getClass();
    Constructor[] theConstructors = c.getConstructors();
    for (int i = 0; i < theConstructors.length; i++) {
      String result ="  " + getStringModified(modified);
      result += getClassName(object) + "(";

      Class[] parameterTypes = theConstructors[i].getParameterTypes();
      for (int k = 0; k < parameterTypes.length; k ++) {
        String parameterString = parameterTypes[k].getName();
        result += parameterString.substring(parameterString.lastIndexOf('.')+1);
        result += " argument" + k ;
        if(k < parameterTypes.length - 1)
          result += ", ";
      }
      result += ") { \n    \\\\Todo\n  }\n\n";
      strContructor += result;
      //System.out.println(result);
    }
    return strContructor;
    // TODO Auto-generated method stub
    
  }

  static void getFieldNames(Object o) {
    Class c = o.getClass();
    Field[] publicFields = c.getDeclaredFields();
    for (int i = 0; i < publicFields.length; i++) {
      int modified = publicFields[i].getModifiers();
      //System.out.println(printModified(modified));
      String fieldName = publicFields[i].getName();
      Class typeClass = publicFields[i].getType();
      String fieldType = typeClass.getName();
      System.out.println("   " + getStringModified(modified) + fieldType + " " + fieldName +";");
    }
  }
  
  static String getStringModified(int modified) {

    String resutl = "" ;
    if(Modifier.isPublic(modified))  resutl  += "public ";
    if(Modifier.isPrivate(modified)) resutl += "private ";
    if(Modifier.isProtected(modified)) resutl += "protected ";    
    if(Modifier.isAbstract(modified)) resutl += "Abstract ";
    if(Modifier.isFinal(modified)) resutl += "final ";
    if(Modifier.isNative(modified)) resutl += "native ";
    if(Modifier.isStatic(modified)) resutl += "static ";
    if(Modifier.isTransient(modified)) resutl += "transient ";
    if(Modifier.isSynchronized(modified)) resutl += "synchronized ";
    if(Modifier.isVolatile(modified)) resutl += "volatile ";
    
    return resutl;
  }
  
  static String getStringMethods(Object object) {
    
    String strMethod = "";
    int modified = object.getClass().getModifiers();
    Class c = object.getClass();
    
    Method[] theMethods = c.getMethods();
    for (int i = 0; i < theMethods.length; i++) {
      String result ="  " + getStringModified(modified);
      result += theMethods[i].getReturnType() + " ";
      result += theMethods[i].getName() + "(";
//      theMethods[i].s/
      Class[] parameterTypes = theMethods[i].getParameterTypes();
      
      for (int k = 0; k < parameterTypes.length; k ++) {
        String parameterString =parameterTypes[k].getName();
        result += parameterString.substring(parameterString.lastIndexOf('.')+1);
        result += " argument" + k ;
        if(k < parameterTypes.length - 1)
          result += ", ";
      }
      result += ") { \n    \\\\Todo\n  }\n\n";
      strMethod += result;
    }
    return strMethod;
 }
  
  public static Object createObject(Class clazz, Class types[], Object[] args) throws Exception {
    Constructor constructor = clazz.getConstructor(types);    
    Object object = constructor.newInstance(args);    
    return object;   
  }
  
  public static Object runMethod(Object object, String methodName, Object[] args, Class[] typesAgrs) throws Exception {
    Class clazz = object.getClass();
    Method method = clazz.getMethod(methodName, typesAgrs);
    Object result = method.invoke(object, args);
    return result;
  }

  public static void main(String arg[]) throws Exception {
    ReflectUtil reflectUtil = new ReflectUtil();
    printObjectInfo(reflectUtil);
    Object[] args = {"Doc lap - Tu do - Hanh phuc", 33};
    Class types[] = { String.class, int.class };
    JTextField j2 = (JTextField) createObject(JTextField.class, types, args );
    System.out.println(runMethod(j2, "getText", null, null ));
  }
}