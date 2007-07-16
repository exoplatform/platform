/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.javascript;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.resolver.ResourceResolver;
import org.mozilla.javascript.Script;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 24, 2007  
 */
public class DefaultJavaScriptEngine extends JavaScriptEngine {
  private Map<String, Script> scripts_ = new HashMap<String, Script>();
  
  public DefaultJavaScriptEngine() {
    
  }
  
  public Script loadScript(ResourceResolver resolver, String url, boolean reload) throws Exception {
    Script script = null ;
    if(!reload) script = scripts_.get(url) ;
    if(script == null) {
      script = loadScript(resolver, url) ;
      scripts_.put(url, script) ;
    }
    return script ;
  }
  
  public Script loadTemplate(ResourceResolver resolver, String url, boolean reload) throws Exception {
    Script script = null ;
    if(!reload) script = scripts_.get(url) ;
    if(script == null) {
      script = loadTemplate(resolver, url) ;
      scripts_.put(url, script) ;
    }
    return script ;
  }
}