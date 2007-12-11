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
package org.exoplatform.services.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Oct 4, 2006  
 */
public class ServiceFactory {
  
  private Map<Class<?>,Object> services;
  
  public Map<Class<?>, Object> getServices() {
    return services;
  }
  
  private void setService(Object object) throws Exception {
    Class<?> clazz = object.getClass();
    Field [] fields = clazz.getDeclaredFields();
    for(Field field : fields){
      Object value = services.get(field.getType());
      if(value == null) continue;
      field.setAccessible(true);
      field.set(object, value);
    }
  }
  
  public void set(List<Class<?>> classes, Map <Class<?>,Object> map) throws Exception {
    this.services = map;
    Iterator<Class<?>> iter = classes.iterator();
    while(iter.hasNext()){
      Class<?> clazz = iter.next();
      Object obj = createInitConstructor(clazz);
      if(obj == null) continue;
      iter.remove();
    }
        
    iter = classes.iterator();
    while(iter.hasNext()){
      Class<?> clazz = iter.next();
      Object obj = createEmptyConstructor(clazz);
      if(obj == null) continue;
      iter.remove();
    }    
    
    iter = classes.iterator();
    while(iter.hasNext()){
      Class<?> clazz = iter.next();
      Object obj = createParamsConstructor(clazz);
      if(obj == null) continue;
      iter.remove();      
    }    
    
    Iterator<Class<?>> iterator = services.keySet().iterator();
    while(iterator.hasNext()){
      setService(services.get(iterator.next()));
    }
  }
  
  public Object createService(Class<?> clazz, Map <Class<?>,Object> map) throws Exception {
    this.services = map;
    return createService(clazz);
  }

  private Object createService(Class<?> clazz) throws Exception {    
    Object obj = createInitConstructor(clazz);
    if(obj == null) obj = createEmptyConstructor(clazz);
    if(obj == null) obj = createParamsConstructor(clazz);
    if(obj == null) {
      throw new Exception("Can't create new Object for "+clazz.getName()
                                +" \n declare constructor for this class. ");
    }
    setService(obj);
    services.put(clazz, obj);
    return obj;
  }
  
  private Object createEmptyConstructor(Class<?> clazz) throws Exception {
    Constructor<?> constructor = null;
    try{
      constructor = clazz.getDeclaredConstructor(new Class<?>[]{});
    }catch(NoSuchMethodException exp){
      return null;
    }
    if(constructor == null) return null;
    constructor.setAccessible(true);
//    System.out.println(constructor.toString());
    Object value = constructor.newInstance(new Object[]{});
    services.put(clazz, value);
    return value;
  }
  
  private Object createInitConstructor(Class<?> clazz) throws Exception {
    Constructor<?> [] constructors = clazz.getDeclaredConstructors();
    Constructor<?> constructor = null;
    InitParams init = null;
    for(Constructor<?> ele : constructors){
      init = ele.getAnnotation(InitParams.class);
      if(init != null) break;
    }
    if(constructor == null) return null;
    return createParamsConstructor(clazz, constructor, init.value());
  }
  
  @SuppressWarnings("unchecked")
  private Object createParamsConstructor(Class clazz) throws Exception {
    Constructor<?> [] constructors = clazz.getDeclaredConstructors();
    if(constructors == null || constructors.length < 1) return null;
    Constructor<?> constructor = constructors[0];
    Class [] classes = constructor.getParameterTypes();
    return createParamsConstructor(clazz, constructor, classes);
  } 
  
  @SuppressWarnings("unchecked")
  private Object createParamsConstructor(Class clazz, Constructor<?> constructor, Class [] classes) throws Exception {
    Object [] objs = new Object[classes.length];
    for(int i = 0; i< classes.length; i++){
      Object obj = services.get(classes[i]);
      if(obj == null){
        Constructor<?> cons = classes[i].getDeclaredConstructor(new Class[]{});
        cons.setAccessible(true);
        obj = cons.newInstance(new Object[]{});
      }
      objs[i] = obj;
    }
    constructor.setAccessible(true);
    Object value = constructor.newInstance(objs);
    services.put(clazz, value);
    return value;
  }
  
}
