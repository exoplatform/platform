

package org.exoplatform.services.parser.util;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 19, 2006  
 */
public class Tree<E> {
  
  protected E value;  
  protected transient Tree parent;  
  protected transient Tree<E> firstChild;  
  protected transient int size = 0;  
  protected transient Tree<E> nextSibling;
  
  public Tree(E value){
    this.value = value;
  }
  
  public Tree(E value, Tree<E> parent){
    this.value = value;
    this.parent = parent;
    parent.addChild(this);
  }
  
  public void addChild(Tree<E> node){
    if(node.parent != this) node.parent = this;    
    size++;
    if(firstChild == null){
      this.firstChild = node;
      return;
    }
    Tree<E> n = firstChild;
    while(n.nextSibling != null){
      n= n.nextSibling;
    }    
    n.nextSibling = node;
  }
  
  public void insertChild(int idx, Tree<E> node){
    checkRange(idx);
    if(node.parent != this) node.parent = this;    
    size++;
    Tree<E> temp;   
    if(idx == 0){
      temp = firstChild;
      firstChild = node;
      node.nextSibling = temp;
      return;
    }
    Tree<E> n = getElement(idx-1);
    temp = n.nextSibling;
    n.nextSibling = node;
    node.nextSibling = temp;
  }
  
  public Iterator<E> iteratorChildren(){
    return new Iterator<E>(firstChild);
  }
  
  public Tree<E> getChild(int idx) {
    checkRange(idx);    
    return getElement(idx);
  }
  
  public Tree<E> removeChild(int idx) {
    checkRange(idx);
    Tree<E> n = getElement(idx - 1);
    if(n == null) return null;
    Tree<E> temp = n.nextSibling;
    n.nextSibling = temp.nextSibling;
    size--;
    return temp;
  }
  
  @SuppressWarnings("unchecked")
  public Tree<E> [] getChildren(){    
    Tree<E> [] values = new Tree[size];    
    int i = 0 ;
    Tree n = firstChild ;
    while(i < size){
      values[i] = n;
      n = n.nextSibling;
      i++;
    }
    return values;
  }
  
  private Tree<E> getElement(int idx){
    int i = 0;
    Tree<E> n = firstChild;
    while(n.nextSibling != null){
      if(i == idx) break;
      n = n.nextSibling;
      i++;
    }
    return n;
  }

  private void checkRange(int index) {
    if(firstChild == null) throw new IndexOutOfBoundsException("Node has any children");
    if (index >= size) 
      throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size);
    if(index < 0) throw new IndexOutOfBoundsException("Negative index : "+index);
  }
  
  public E getValue() { return value; }

  public void setValue(E value) { this.value = value; }

  protected Tree<E> getNextSibling() { return nextSibling; }

  public int getSize() { return size; }

  @SuppressWarnings("unchecked")
  public <T extends Tree> T getParent() { return (T)parent; }   
}
