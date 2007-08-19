/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui;

import org.exoplatform.webui.config.InitParams;

/**
 * Created by The eXo Platform SAS
 * May 10, 2006
 * 
 * A utility class that provides static methods to create new objects, of a given type
 */
public class Util {
  /**
   * The default parameters given to the constructor
   */
  static Class<?>[] CONSTRUCTOR_PARAMS = {InitParams.class} ; 
  /**
   * 
   * @param type The type of the object to create, given as a String
   * @param params The parameters to give to the constructor
   * @return A new object of the given type
   * @throws Exception
   */
  static public Object createObject(String type, InitParams params) throws Exception {
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    Class<?> clazz =   cl.loadClass(type) ; 
    return  createObject(clazz, params) ;
  }
  /**
   * 
   * @param <T> The type of the object to create
   * @param type The type parameter given as a Class object
   * @param params The parameters to give to the constructor
   * @return The object of type T
   * @throws Exception
   */
  static public <T> T createObject(Class<T> type, InitParams params) throws Exception {
    if(params == null) {
      return type.getConstructor().newInstance() ;
    } 
    Object[] args =  {params} ;
    return type.getConstructor(CONSTRUCTOR_PARAMS).newInstance(args)  ;
  }
}
