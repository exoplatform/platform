package org.exoplatform.services.parser.util;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Queue<T> {
  
  protected Node<T> current = null;
  
  protected Node<T> first = null;

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
