package org.exoplatform.services.parser.util;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Stack<T> {  

  private Node<T> stack = null;

  public T pop( ) {
    T result = stack.value;
    stack = stack.next;
    return result;
  }
  
  public boolean hasNext(){
    return stack != null;
  }

  public void push(T v) { stack = new Node<T>(v, stack);}
}
