/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.javascript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.exoplatform.resolver.ResourceResolver;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 24, 2007  
 */
public class JavaScriptEngine {
  
  public Script loadScript(ResourceResolver resolver, String url, boolean reload) throws Exception {
    return loadScript(resolver, url) ;
  }
  
  public Script loadTemplate(ResourceResolver resolver, String url, boolean reload) throws Exception {
    return loadTemplate(resolver, url) ;
  }
  
  public Script loadScript(ResourceResolver resolver, String url) throws Exception {
    InputStream is = resolver.getInputStream(url) ;
    Reader reader = new InputStreamReader(is) ;
    Script script = compileScript(url, reader) ;
    return script ;
  }
  
  public Script loadTemplate(ResourceResolver resolver, String url) throws Exception {
    String template = new String(resolver.getResourceContentAsBytes(url)) ;
    Script script = compileTemplate(url, template) ;
    return script ;
  }
  
  public void  runScript(Script script, Map<String, Object> context) throws Exception {
    Context cx = Context.enter();
    try {
      cx.setApplicationClassLoader(Thread.currentThread().getContextClassLoader()) ;
      Scriptable scope = cx.initStandardObjects() ;
      Iterator<Map.Entry<String, Object>> i = context.entrySet().iterator() ;
      while(i.hasNext()) {
        Map.Entry<String, Object> entry = i.next() ;
        scope.put(entry.getKey(), scope, entry.getValue()) ;
      }
      script.exec(cx, scope) ;
    } finally {
      Context.exit();
    }
  }
  
  public void merge(Script template, Map<String, Object> context, Writer out) throws Exception {
    context.put("_w", out) ;
    runScript(template, context) ;
  }
  
  public Script compileScript(String name, String script) throws Exception {
    Context cx = Context.enter();
    try {
      cx.setApplicationClassLoader(Thread.currentThread().getContextClassLoader()) ;
      Script scriptObject = cx.compileString(script, name, 1, null) ;
      return scriptObject ;
    } finally {
      Context.exit();
    }
  }
  
  public Script compileScript(String name, Reader reader) throws Exception {
    Context cx = Context.enter();
    try {
      cx.setApplicationClassLoader(Thread.currentThread().getContextClassLoader()) ;
      Script scriptObject = cx.compileReader(reader, name, 1, null) ;
      return scriptObject ;
    } finally {
      Context.exit();
    }
  }
  
  public Script compileTemplate(String name, String template) throws Exception {
    char[]  buf = template.toCharArray() ;
    StringBuilder script = new StringBuilder(10000) ;
    StringBuilder text = new StringBuilder(1500) ;
    StringBuilder code = new StringBuilder(1500) ;
    int pos =  0 ;
    boolean codeBlock = false ;
    boolean codeBlockReturn = false ;
    while(pos < buf.length) {
      //ignore \r character
      if(buf[pos] == 13) {
        pos++ ;
        continue ;
      }
      
      if(buf[pos] == '<' && buf[pos +1] == '%') {  //Start A block code
        pos++ ;
        codeBlockReturn = false ;
        if(buf[pos + 1] == '=') {
          pos++ ;
          codeBlockReturn = true ;
        }
        codeBlock = true ;
        if(text.length() > 0) {
          String tmp = text.toString() ;
          if(tmp.length() > 0) {
            String[] lines = tmp.split("\n") ;
            for(int i = 0; i < lines.length; i++) {
              if(i != lines.length - 1) {
                script.append("_w.append(\"").append(lines[i]).append("\\n\"); \n") ;
              } else {
                script.append("_w.append(\"").append(lines[i]).append("\"); \n") ;
              }
            }
          }
          text.setLength(0) ;
        }
      } else if(buf[pos] == '%' && buf[pos +1] == '>') {  
        //End a  block of code, push the block of code to the script buffer, 
        pos++ ;
        codeBlock = false ;
        if(codeBlockReturn) {
          //If the block of code start with <%= ...%>,  write the return result of the block code to 
          //the writer
          script.append("\n_w.append(").append(code).append("); \n") ;
        } else {
          //just execute the block of code
          script.append(code).append('\n') ;
        }
        code.setLength(0) ;
      } else {  // Extract  text or block code
        if(codeBlock) {
          code.append(buf[pos]) ;
        } else {
          if(buf[pos] == '\"') text.append('\\') ;
          text.append(buf[pos]) ;
        }
      }
      pos++ ;
    }
    if(text.length() > 0) {
      String tmp = text.toString() ;
      if(tmp.length() > 0) {
        String[] lines = tmp.split("\n") ;
        for(String line : lines) script.append("_w.append(\"").append(line).append("\\n\"); \n") ;
      }
    }
    return compileScript(name, script.toString())  ;
  }
}