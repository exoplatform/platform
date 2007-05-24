/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Mar 21, 2007  
 */
public class CommandHandler extends WebRequestHandler {

  public String[] getPath() { return new String[] { "/command"} ; }

  public void execute(WebAppController app,  HttpServletRequest req, HttpServletResponse res) throws Exception {
    System.out.println("IN COMMAND " + req.getServletPath());
    System.out.println("IN COMMAND " + req.getPathInfo());
    Map props = req.getParameterMap() ;
  }

  /**
   * This method should use the java reflection to create the command object according to the command
   * type, then  populate the command  properties  
   * 
   * @param command  The command class type 
   * @param props    list of the properties that should be set in the command object
   * @return         The command object instance
   * @throws Exception
   */
  public Command createCommand(String command, Map props) throws Exception  {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
    Class<?> clazz =  classLoader.loadClass(command);
    Object object = clazz.newInstance();
    Iterator<Object> iter = props.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      Field field = getField(clazz, key.toString());
      if(field == null) continue;
      setValue(object, field, props.get(key));
    }
    return null ;
  }

  private final static void setValue(Object bean, Field field, Object value) throws Exception {
    Class clazz = bean.getClass();
    Method method = getMethod("set", field, clazz);
    if(method != null) {
      method.invoke(bean, new Object[]{value});
      return;
    }
    field.setAccessible(true);
    field.set(bean, value);
  }

  private final static Method getMethod(String prefix, Field field, Class clazz) throws Exception {
    StringBuilder name = new StringBuilder(field.getName());
    name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
    name.insert(0, prefix);
    return getMethodByName(name.toString(), field, clazz);
  }

  private final static Method getMethodByName(String name, Field field, Class clazz) {
    try{
      Method method = clazz.getDeclaredMethod(name.toString(), new Class[]{});
      if(method != null) return method;
      if(clazz == Object.class) return null;
      method = getMethodByName(name, field, clazz.getSuperclass());
      if(method != null) return method;
    }catch (Exception e) {
    }    
    return null;
  }


  private Field getField(Class clazz, String name) {
    Field field = null;
    try{
      field = clazz.getDeclaredField(name);
    }catch (Exception e) {
    }
    if(field != null) return field;
    if(clazz == Object.class) return null;
    return getField(clazz.getSuperclass(), name);
  }

}