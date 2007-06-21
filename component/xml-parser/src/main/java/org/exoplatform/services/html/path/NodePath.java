/***************************************************************************
 * Copyright 2003-2006 by  eXo Platform SARL - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html.path;

import org.exoplatform.services.html.Name;
/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 15, 2006
 */
public class NodePath {

  protected Index [] indexs;
  
  public NodePath(Index[] indexs) {
    this.indexs = indexs;
  }
  
  public Index[] getIndexs() { return indexs;   }
   
  public String toString(){
    StringBuilder builder = new StringBuilder();
    for(Index index : indexs){
      if(builder.length()> 0) builder.append('.');
      builder.append(index.getName()).append('[').append(index.getIdx()).append(']');
    }
    return builder.toString();
  }

  public static class Index {

    private Name name ;
    private int idx;

    public Index(Name name, int idx){
      this.name = name;
      this.idx = idx; 
    }

    public Name getName() { return name; }
    public void setName(Name name) { this.name = name; }

    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

  }

  
}
