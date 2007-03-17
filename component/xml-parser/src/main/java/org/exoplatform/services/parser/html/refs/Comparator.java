/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.parser.html.refs;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 10, 2006
 */
public interface Comparator<T,V> {  
  
  public int compareName();
  
  public int compareValue();
  
}
