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