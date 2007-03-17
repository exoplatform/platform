/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.model;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 16, 2006
 */
public class ReflectionUtil {

  static public Object[] EMPTY_ARGS = { } ;

  static private HashMap<String, SoftReference<Method>> getMethodCache_ = new HashMap<String, SoftReference<Method>>() ;
  static private HashMap<String, SoftReference<Method>> setMethodCache_ = new HashMap<String, SoftReference<Method>>() ;

  static public Method getGetBindingMethod(Object bean, String bindingField) throws Exception {
    String key = bindingField + "@" +  bean.getClass() ;
    Method method = null ;
    SoftReference<Method> sref = getMethodCache_.get(key) ;
    if(sref != null) method  =  sref.get() ;
    if(method == null) {     
      Exception exp = null;
      try{
        method = getBindingMethod(bean, bindingField, new Class[]{}, "get");
      }catch (Exception e) {
        exp = e;
      }
      if(method == null){
        try{
          method = getBindingMethod(bean, bindingField, new Class[]{}, "is");
        }catch(Exception exp2){          
        }
      }
      if(method == null && exp != null) throw exp;
      getMethodCache_.put(key, new SoftReference<Method>(method)) ;
    }
    return method ;
  }

  static public Method getSetBindingMethod(Object bean, String bindingField, Class[] args) throws Exception {
    String key = bindingField + "@" +  bean.getClass() ;
    Method method = null ;
    SoftReference<Method> sref = setMethodCache_.get(key) ;
    if(sref != null) method  =  sref.get() ;
    if(method == null) {      
      method = getBindingMethod(bean, bindingField, args, "set");
      setMethodCache_.put(key, new SoftReference<Method>(method)) ;
    }
    return method ;
  }

  static private Method getBindingMethod(Object bean, String bindingField, 
                                         Class[] classes, String prefix ) throws Exception {
    StringBuilder b  = new StringBuilder() ;
    b.append(prefix);
    b.append(Character.toUpperCase(bindingField.charAt(0))) ;
    b.append(bindingField.substring(1)) ;    
    return bean.getClass().getMethod(b.toString(), classes) ;
  }

}
