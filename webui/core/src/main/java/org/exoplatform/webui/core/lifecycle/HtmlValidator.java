/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core.lifecycle;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.exoplatform.services.common.util.Stack;
import org.exoplatform.services.html.tidy.HTMLTidy;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 10, 2007  
 */
public class HtmlValidator extends Writer {

  private static boolean DEBUG_MODE = false;
  
  private Writer finalWriter_ ;
  private HTMLTidy tidy_;
  private StringBuilder content_;
  private Stack<Integer> queue ;
  
  public HtmlValidator(Writer w) {
    finalWriter_ = w ;
    
    if(!DEBUG_MODE) return;
    tidy_ = new HTMLTidy();
    content_ = new StringBuilder();
    queue = new Stack<Integer>();
  }
  
  public void close() throws IOException {
    finalWriter_.close() ;
  }

  public void flush() throws IOException {
    finalWriter_.flush();
  }
  
  public void startComponent() {
    if(DEBUG_MODE) queue.push(content_.length());
  }

  @Override
  public void write(char[] buf, int offset, int len) throws IOException {
    finalWriter_.write(buf, offset, len);
    if(DEBUG_MODE) content_.append(buf, offset, len);
  }
  
  public void endComponent() throws Exception {
    if(!DEBUG_MODE) return;
    Integer start = queue.pop();
    String chunk = content_.substring(start);
    content_.delete(start, content_.length()-1);
    
    List<String>  messages = tidy_.check(chunk.toCharArray());
    if(messages.size() < 1)  return;
    StringBuilder builder = new StringBuilder();

    for(String ele :  messages) {
      builder.append(ele).append('\n');
    }
    throw new Exception(builder.toString());
  }
  
}
