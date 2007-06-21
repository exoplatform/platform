/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.common;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 19, 2006
 */
public class ThreadSoftRef<T> extends ThreadLocal<SoftReference<T>> {

  private Class<T> clazz;

  @SuppressWarnings("unchecked")
  public ThreadSoftRef (Class<?> clazz) { this.clazz = (Class<T>)clazz; }  

  public T getRef() {
    SoftReference<T> sr = get();
    if (sr == null || sr.get() == null) {
      try{        
        Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[]{});
        constructor.setAccessible(true);
        sr = new SoftReference<T>(constructor.newInstance(new Object[]{}));
      }catch(Exception exp) {
        exp.printStackTrace();
      }
      set(sr);
    }
    return sr.get();
  }  
 
}
