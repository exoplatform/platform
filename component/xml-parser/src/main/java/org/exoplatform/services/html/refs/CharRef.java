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
class CharRef {
  
  private int value;
  
  private String name;
  
  CharRef(String n, int v) {
    name = n;
    value = v;
    if (name == null) name = "";
  }
  
  String getName() {
    return name;
  }
  
  void setName(String name) {
    this.name = name;
  }
  int getValue() {
    return value;
  }
  
  void setValue(int value) {
    this.value = value;
  }
  
  int compare(CharRef r) {
    return getName().compareTo(r.getName());
  }        
}
