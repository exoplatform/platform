/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.templates.groovy;

import java.io.Writer;
import java.util.HashMap;

import org.exoplatform.resolver.ApplicationResourceResolver;
import org.exoplatform.resolver.ResourceResolver;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 8, 2006
 */
@SuppressWarnings("serial")
public class BindingContext extends  HashMap<String, Object> {
  
  protected ResourceResolver  resolver_ ;
  protected Writer writer_ ;
  protected GroovyTemplateService service_ ;
  
  public BindingContext(ResourceResolver resolver, Writer w) {
    resolver_ = resolver ;
    writer_ =  w ;
  }
  
  public ResourceResolver  getResourceResolver() {  return resolver_ ; }
  
  public Writer  getWriter() { return writer_ ; }
  
  public void setGroovyTemplateService(GroovyTemplateService service) { service_ = service ; }
   
  public BindingContext clone() {
    BindingContext newContext = new BindingContext(resolver_, writer_) ;
    newContext.putAll(this) ;
    newContext.setGroovyTemplateService(service_) ;
    return newContext ;  
  }
  
  final public  void include(String name) throws Exception  {
    service_.include(name, clone()) ;
  }
  
}