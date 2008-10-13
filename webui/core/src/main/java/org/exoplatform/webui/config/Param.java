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
package org.exoplatform.webui.config;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.InputStream;

import org.exoplatform.resolver.ResourceResolver;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.xml.object.XMLObject;

public class Param {
  
	private String name;
	private String value;
  private transient Object object ;
  
  public String getName() { return name ; } 
  public void setName(String name) { this.name = name; }
  
  public String getValue() { return value ; }
  public void setValue(String value) { this.value = value; }
  
  public Object getMapXMLObject(WebuiRequestContext context)  throws Exception {
    if(object != null)  return object ;
    ResourceResolver resolver = context.getResourceResolver(value) ;
    InputStream is = resolver.getInputStream(value) ;
    object = XMLObject.getObject(is) ;
    is.close() ;
    return object ;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getMapGroovyObject(WebuiRequestContext context)  throws Exception {
    try {
      if(object != null)  return (T)object ;
      ResourceResolver resolver = context.getResourceResolver(value) ;
      InputStream is = resolver.getInputStream(value) ;
      //TODO if is == null throw an exception saying the it's impossible to find the file
      Binding binding = new Binding();
      GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
      object = shell.evaluate(is);
      is.close() ;
      return (T)object ;
    } catch (Exception ex) {
      System.out.println("A  problem in the groovy script : " + value) ;
      ex.printStackTrace();
      throw ex;
    }
  }
  
  public <T> T getFreshObject(WebuiRequestContext context) throws Exception{
    try{
      ResourceResolver resolver = context.getResourceResolver(value) ;
      InputStream is = resolver.getInputStream(value) ;
      Binding binding = new Binding();
      GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), binding);
      object = shell.evaluate(is);
      is.close() ;
      return (T)object ;
    }catch (Exception ex) {
      System.out.println("A  problem in the groovy script : " + value) ;
      ex.printStackTrace();
      throw ex;
    }
  }
}