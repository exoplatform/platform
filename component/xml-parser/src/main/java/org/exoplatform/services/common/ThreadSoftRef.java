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
