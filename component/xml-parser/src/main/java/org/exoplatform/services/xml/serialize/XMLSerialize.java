/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.serialize;

import org.exoplatform.services.common.ThreadSoftRef;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Apr 9, 2007
 */
public class XMLSerialize {
  
  static ThreadSoftRef<XMLSerialize> SERVICE = new ThreadSoftRef<XMLSerialize>(XMLSerialize.class);
  
  public final static XMLSerialize getInstance() { return SERVICE.getRef(); } 
  
  static ThreadSoftRef<ReflectUtil> REFLECT_UTIL = new ThreadSoftRef<ReflectUtil>(ReflectUtil.class);
  
  public XMLSerialize() {
  }
  
  @SuppressWarnings("unused")
  public XMLMapper getXMLMapper(Class<?> clazz) { return Bean2XML.getInstance(); }
  
  @SuppressWarnings("unused")
  public BeanMapper getBeanMapper(Class<?> clazz) { return XML2Bean.getInstance(); }
  
}
