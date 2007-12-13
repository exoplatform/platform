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
  public void   setVariables(Map<String, Object> variables) { variables_ =  variables ; }
  
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