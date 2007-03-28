package org.exoplatform.web.application;

abstract public class ApplicationSession {
  abstract public Object getAttribute(String name) throws Exception ;
  abstract public void   setAttribute(String name, Object value, boolean replicated) throws Exception ;
  
  abstract public Object getUserAttribute(String name) throws Exception ;
  abstract public void   setUserAttribute(String name, Object value, boolean replicated) throws Exception ;
  
  abstract public String getId() ;
}