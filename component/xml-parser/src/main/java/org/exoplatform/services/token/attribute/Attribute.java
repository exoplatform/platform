/*
 * Copyright 2004-2006 The eXoPlatform        All rights reserved.
 *
 * Created on January 24, 2006, 7:50 PM
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
