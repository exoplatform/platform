/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.web.command;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.activation.UnknownObjectException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.web.WebAppController;
import org.exoplatform.web.WebRequestHandler;

/**
 * Created by The eXo Platform SAS
 * Mar 21, 2007  
 */
public class CommandHandler extends WebRequestHandler {

  public String[] getPath() { return new String[] { "/command"} ; }

  public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
    Map props = req.getParameterMap() ;
    String type =  req.getParameter("type");
    if(type == null || type.trim().length() < 1) throw new NullPointerException("Unknown type command handler");
    Command command = createCommand(type, props);
    if(command == null) throw new UnknownObjectException("Unknown command handler with type is "+type);
    command.execute(controller, req, res);
  }

  /**
   * This method should use the java reflection to create the command object according to the command
   * type, then  populate the command  properties  
   * 
   * @param type  The command class type 
   * @param props    list of the properties that should be set in the command object
   * @return         The command object instance
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public Command createCommand(String type, Map props) throws Exception  {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
    Class<?> clazz =  classLoader.loadClass(type);
    Object object = clazz.newInstance();
    Iterator<Object> iter = props.keySet().iterator();
    while(iter.hasNext()) {
      Object key = iter.next();
      Field field = getField(clazz, key.toString());
      if(field == null) continue;
      setValue(object, field, props.get(key));
    }    
    return (Command)object ;
  }

  private final void setValue(Object bean, Field field, Object value) throws Exception {
    Class type = field.getType();
    if(type.isArray() && !value.getClass().isArray()) {
      value = toValues(type, new Object[]{value});
    } else if(type.isArray() && value.getClass().isArray()){
      value = toValues(type, value);
    } else {
      if(!type.isArray() && value.getClass().isArray()) value = Array.get(value, 0);
      value = toValue(type, value);
    }
    Class clazz = bean.getClass();
    Method method = getMethod("set", field, clazz);
    if(method != null) {
      method.invoke(bean, new Object[]{value});
      return;
    }
    field.setAccessible(true);
    field.set(bean, value);
  }

  private final Method getMethod(String prefix, Field field, Class clazz) throws Exception {
    StringBuilder name = new StringBuilder(field.getName());
    name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
    name.insert(0, prefix);
    return getMethodByName(name.toString(), field, clazz);
  }

  private final Method getMethodByName(String name, Field field, Class clazz) {
    try{
      Method method = clazz.getDeclaredMethod(name.toString(), new Class[]{field.getType()});
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
  
  private Object toValues(Class<?> clazz, Object objects) {
    Class componentType = clazz.getComponentType();
    Object newValues = Array.newInstance(componentType, Array.getLength(objects));
    for(int i = 0; i < Array.getLength(objects); i++) {
      Array.set(newValues, i, toValue(componentType, Array.get(objects, i)));
    }
    return clazz.cast(newValues);
  }
  
  private Object toValue(Class<?> clazz, Object object) {
    if(clazz == int.class) return new Integer(object.toString()).intValue();
    if(clazz == short.class) return new Short(object.toString()).shortValue();
    if(clazz == float.class) return new Float(object.toString()).floatValue();
    if(clazz == double.class) return new Double(object.toString()).doubleValue();
    if(clazz == boolean.class) return new Boolean(object.toString()).booleanValue();
    if(clazz == char.class) return object.toString().trim().charAt(0);
    try{
      Constructor<?> constructor = clazz.getConstructor(new Class[]{String.class});
      return constructor.newInstance(new Object[]{object.toString()});
    }catch (Exception e) {
    }
    return object.toString();
  }

}