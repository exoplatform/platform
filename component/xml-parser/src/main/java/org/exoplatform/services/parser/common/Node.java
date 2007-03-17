/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.common;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 6, 2006
 */
public interface Node<T> {
  
  public T getName();
  
  public char[] getValue();
  
  public void setValue(char[] value);  
}
