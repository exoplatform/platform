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
package org.exoplatform.services.token.attribute;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.token.Node;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 6, 2006
 */
@SuppressWarnings("serial")
public class Attributes extends ArrayList<Attribute> { 
 
  private Node<?> node;
  
  public Attributes(Node<?> n){
    this.node = n;
  }  
  
  public Attribute get(String name){
    int i = indexOf(name);
    if(i < 0) return null;
    return get(i);
  }

  public String getAttributeValue(String name){
    int i = indexOf(name);
    if(i < 0) return null;
    return get(i).getValue();
  }

  public void set(List<Attribute> list){
    for(Attribute ele : list){
      int i = indexOf(ele);
      if(i < 0) add(ele);
      else get(i).setValue(ele.getValue());
    }
    node.setValue(toString().toCharArray());
  } 

  public void set(Attribute attr){  
    int i = indexOf(attr);
    if(i < 0) add(attr);
    else get(i).setValue(attr.getValue());
    node.setValue(toString().toCharArray());
  } 

  public void remove(String name){ 
    int i = indexOf(name);   
    if(i < 0) return;
    remove(i);
    node.setValue(toString().toCharArray());
  }     
  
  public void removeAll(){ 
    clear();
    node.setValue(toString().toCharArray());
  }
  
  public int indexOf(Object elem) {
    if(elem instanceof Attribute) {
      return super.indexOf(elem);
    }
    if(elem == null) return -1;
    for(int i=0; i<size(); i++ ){
      if(get(i).equals(elem)) return i;
    }
    return -1;
  }
  
  public String toString(){
    StringBuilder builder = new StringBuilder();
    builder.append(node.getName().toString());
    if(size() > 0) builder.append(' ');
    for(Attribute ele : this){
      builder.append(ele.getName()).append('=');
      builder.append('"').append(ele.getValue()).append('"');
      builder.append(' ');
    }
    return builder.toString();
  }
  
}
