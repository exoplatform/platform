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
package org.exoplatform.services.html.refs;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
class CharsSequence {
  
  private int index;
  
  private char [] values;
  
  CharsSequence(int max){
    values = new char[max];
    index = 0;
  }
  
  void append(char c){
    if(index >= values.length) return;
    values[index] = c;
    index++;
  }
  
  void append(String string){
    char [] cs = string.toCharArray();
    for(int i = 0; i < cs.length; i++){
      if(index >= values.length) return;
      values[index] = cs[i];
      index++;
    }
  }

  char[] getValues() {
    char[] newValues = new char[index];
    System.arraycopy(values, 0, newValues, 0, index);
    return newValues;
  }  
}
