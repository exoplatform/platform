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

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

import org.exoplatform.javascript.DefaultJavaScriptEngine;
import org.exoplatform.test.BasicTestCase;
import org.mozilla.javascript.Script;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class TestTemplateEngine extends BasicTestCase {
  
  public TestTemplateEngine(String name){
    super(name);
  }

  public void testEngine() throws Exception {
    DefaultJavaScriptEngine engine = new DefaultJavaScriptEngine() ;
    HashMap<String, Object>  context = new HashMap<String, Object>() ;
    
    ClassLoader loader = Thread.currentThread().getContextClassLoader() ;
    InputStream is = loader.getResourceAsStream("org/exoplatform/javascript/TestTemplate.jstmpl") ;
    byte[]  buf  = new byte[is.available()] ;
    is.read(buf) ;
    Script script = engine.compileTemplate("TestTemplate", new String(buf)) ;
    StringWriter writer = new StringWriter() ;
    context.put("helloObject", "Hello Object") ;
    engine.merge(script, context, writer) ;
    System.out.println(writer.getBuffer());
  }
  
}