package org.exoplatform.webui.config;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.InputStream;

import org.exoplatform.templates.groovy.ResourceResolver;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.xml.object.XMLObject;

public class Param {
  
	private String name;
	private String value;
  private transient Object object ;
  
  public String getName() { return name ; } 
  public void setName(String name) { this.name = name; }
  
  public String getValue() { return value ; }
  public void setValue(String value) { this.value = value; }
  
  public Object getMapXMLObject(RequestContext context)  throws Exception {
    if(object != null)  return object ;
    ResourceResolver resolver = context.getResourceResolver(value) ;
    InputStream is = resolver.getInputStream(value) ;
    object = XMLObject.getObject(is) ;
    is.close() ;
    return object ;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getMapGroovyObject(RequestContext context)  throws Exception {
    try {
      if(object != null)  return (T)object ;
      ResourceResolver resolver = context.getResourceResolver(value) ;
      InputStream is = resolver.getInputStream(value) ;
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
}