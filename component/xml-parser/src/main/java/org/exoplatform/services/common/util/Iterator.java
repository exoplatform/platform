package org.exoplatform.services.common.util;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Iterator<E> {
  
  private Tree<E> node;
  
  Iterator(Tree<E> node){
    this.node = node;
  }
  
  public Tree<E> next(){
    if(node == null) return null;
    Tree<E> value = node;
    node = node.nextSibling;
    return value;      
  } 
  
  public boolean hasNext(){
    return node != null;
  }    
}
