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

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

import org.exoplatform.services.common.ServiceConfig.ServiceType;


/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 3, 2007
 */
public class ServicesContainer {

  private static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

  /*public static <T> void put(Class<T> clazz) { put(clazz.getName(), clazz); }

  public static <T> void put(String id, Class<T> clazz) {
    try {
      map.put(id, create(clazz));
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
  }*/

  public static <T> T get(Class<T> clazz) { return get(clazz.getName(), clazz); }

  public static <T> T get(String id, Class<T> clazz) {
    ServiceType type = ServiceType.INSTANCE;
    ServiceConfig config = clazz.getAnnotation(ServiceConfig.class);
    if(config != null) type = config.type();
    return get(type, id, clazz);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(ServiceType type, String id, Class<T> clazz) {
    if(type == ServiceType.INSTANCE) {
      try {
        return create(clazz);
      }catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    if(type == ServiceType.SINGLE_FINAL) {
      Object service = map.get(id);
      if(service != null) return clazz.cast(service);
      try{
        service = create(clazz);
        map.put(id, service);
        return clazz.cast(service);
      }catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

//  if(type == ServiceType.SOFT_REFERENCE) { 
    ThreadSoftReference<T> thread = (ThreadSoftReference<T>)map.get(clazz);
    if(thread == null) {
      thread = new ThreadSoftReference<T>(clazz);
      map.put(id, thread);
    }
    try {
      return clazz.cast(thread.getRef());
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
    /*}

    if(type == ServiceType.LAZY_FINAL) return clazz.cast(map.get(id));

    return null;*/
  }

  @SuppressWarnings("unchecked")
  private static <T> T create(Class<T> clazz) throws Exception {
    Constructor<T> [] constructors = (Constructor<T> [])clazz.getDeclaredConstructors();
    Arrays.sort(constructors, new Comparator<Constructor<T>>() {
      public int compare(Constructor<T> cons1, Constructor<T> cons2) {
        return cons1.getParameterTypes().length - cons2.getParameterTypes().length;
      }
    });
    if(constructors.length < 1) throw new Exception("Not constructor in class "+clazz);
    Constructor<T> constructor = constructors[0];
    Class<?> [] classes = constructors[0].getParameterTypes();
    if(classes.length < 1) return clazz.newInstance();
    Object [] objs = new Object[classes.length];
    for(int i = 0; i< classes.length; i++){
      objs[i] = get(classes[i]);
    }
    constructor.setAccessible(true);
    return clazz.cast(constructor.newInstance(objs));
  }

  private static class ThreadSoftReference<T> extends ThreadLocal<SoftReference<T>> {

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    private ThreadSoftReference(Class<?> clazz) { this.clazz = (Class<T>)clazz; }  

    private T getRef() throws Exception {
      SoftReference<T> sr = get();
      if (sr == null || sr.get() == null) {
        sr = new SoftReference<T>(create(clazz));
        set(sr);
      }
      return sr.get();
    }

  }

}
