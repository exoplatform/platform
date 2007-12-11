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

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class Attribute {
  
  private String name;
  
  private String value;
  
  public Attribute(String n, String v){
    name = n;
    value = v;
  }
  
  public String getName(){
    return name;
  }    
 
  public void setName( String n){
    name = n;
  }  
  
  public String getValue(){
    return value;
  }  
  
  public void setValue( String v){
    value = v;
  }
  
  public boolean equals(Object obj){
    if(obj == this) return true;
    if(obj instanceof Attribute){
      return ((Attribute)obj).getName().equalsIgnoreCase(name);
    }
    if(obj instanceof String){
      return name.equalsIgnoreCase((String)obj);
    }
    return super.equals(obj);
  }  

}
