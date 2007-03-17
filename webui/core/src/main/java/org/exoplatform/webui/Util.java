/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui;

import org.exoplatform.webui.config.InitParams;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 10, 2006
 */
public class Util {
  
  static Class[] CONSTRUCTOR_PARAMS = {InitParams.class} ; 
  
  static public Object createObject(String type, InitParams params) throws Exception {
    ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
    Class<?> clazz =   cl.loadClass(type) ; 
    return  createObject(clazz, params) ;
  }
  
  static public <T> T createObject(Class<T> type, InitParams params) throws Exception {
    if(params == null) {
      return type.getConstructor().newInstance() ;
    } 
    Object[] args =  {params} ;
    return type.getConstructor(CONSTRUCTOR_PARAMS).newInstance(args)  ;
  }
}
