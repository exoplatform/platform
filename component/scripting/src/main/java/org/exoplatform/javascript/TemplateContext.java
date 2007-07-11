/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.javascript;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.resolver.ResourceResolver;
import org.mozilla.javascript.Script;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 29, 2007  
 */
public class TemplateContext {
  private JavaScriptEngine jsengine_ ;
  private ResourceResolver resolver_ ;
  private Writer writer_ ;
  private boolean cacheTemplate = true ;
  protected Map<String, Object> variables_ = new HashMap<String, Object>() ;
  
  public TemplateContext(JavaScriptEngine jsengine, ResourceResolver resolver, Writer writer) {
    jsengine_ = jsengine ;
    resolver_ = resolver ;
    writer_ =  writer ;
  }
  
  public JavaScriptEngine getJavaScriptEngine() { return jsengine_ ; }
  
  public ResourceResolver getResourceResolver() {  return resolver_; }
  
  public Writer getWriter()  { return writer_ ; }
  public void   setWriter(Writer writer) { writer_ = writer; }
  
  public boolean getCacheTemplate() { return cacheTemplate ; }
  public void    setCacheTemplate(boolean b) { cacheTemplate = b ; }

  public Object getVariable(String name) { return variables_.get(name) ; } 
  public void   setVariable(String name, Object obj) { variables_.put(name, obj) ; }
  
  public void render(String templateUrl) throws Exception {
    variables_.put("TemplateContext", this) ;
    Script template = jsengine_.loadTemplate(resolver_, templateUrl,  cacheTemplate) ;
    jsengine_.merge(template, variables_, writer_) ;
    variables_.remove("TemplateContext") ;
  }
  
  public void include(String templateUrl) throws Exception {
    Script template = jsengine_.loadTemplate(resolver_, templateUrl,  cacheTemplate) ;
    jsengine_.merge(template, variables_, writer_) ;
  }
}