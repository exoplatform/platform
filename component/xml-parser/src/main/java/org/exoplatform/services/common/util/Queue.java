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
package org.exoplatform.services.common.util;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Queue<T> {
  
  protected Node<T> current = null;
  
  protected Node<T> first = null;
  
  protected Node<T> last = null;

  public T pop() {
    T result = first.value;
    first = first.next;
    if(first == null) current = null;
    return result;
  }
  
  public boolean hasNext(){
    return first != null;
  }

  public void push(T v) {
    Node<T> newNode = new Node<T>(v);    
    if(current != null){
      current.next = newNode;
      current = newNode;
      return;
    }
    current = newNode;
    first = current;
  } 
}
