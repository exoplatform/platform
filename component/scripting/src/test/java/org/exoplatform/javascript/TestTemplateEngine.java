/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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