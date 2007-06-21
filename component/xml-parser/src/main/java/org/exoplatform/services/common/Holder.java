/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.common;

import java.util.AbstractList;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jul 30, 2006
 * 
 * backup
 * 
 */
public abstract class Holder<T> extends AbstractList<T> {

  protected T [] array = null; 

  protected ClassConfig config;

  public Holder(){
    config = Factory.createBean(getClass());
  }

  public T get(int index) {
    return array[index];
  }

  public int size() { return array.length; }

  public static class Factory {

    private static Class<?> cacheDefaultClass = null;
    private static ClassConfig cacheClassConfig = null;

    public static <T> ClassConfig createBean(Class<T> clazz){   
      if(cacheDefaultClass == clazz) return cacheClassConfig;
      cacheClassConfig = clazz.getAnnotation(ClassConfig.class);
      cacheDefaultClass = clazz;
      return cacheClassConfig;
    }

    public static <T> void class2Object(Class<T> defaultClass, T[] array){   
      for(int i =0; i < cacheClassConfig.classes().length; i++){
        try{
          array[i] = defaultClass.cast(cacheClassConfig.classes()[i].newInstance());
        }catch (Exception e) {          
          e.printStackTrace();
        }
      }
    }
    
  }

}
