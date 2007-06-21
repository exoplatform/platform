/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.common.util;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 19, 2006
 */
public class Node<T> {

  public T value;
  
  public Node<T> next;

  Node(T v) {
    value = v; 
  }
  
  Node(T v, Node<T> n) {
    value = v; 
    next = n;
  }
}
