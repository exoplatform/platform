/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.token;

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
