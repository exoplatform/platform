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
