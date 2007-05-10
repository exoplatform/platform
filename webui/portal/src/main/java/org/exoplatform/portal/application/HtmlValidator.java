/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.application;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 10, 2007  
 */
public class HtmlValidator extends Writer {
  private Writer finalWriter_ ;
  private CharArrayWriter writer_ ;
  
  public HtmlValidator(Writer w) {
    finalWriter_ = w ;
    writer_ = new CharArrayWriter() ;
  }
  
  public void close() throws IOException {
    writer_.close() ;
    finalWriter_.close() ;
  }

  public void flush() throws IOException {
    writer_.close() ;
  }

  @Override
  public void write(char[] buf, int offset, int len) throws IOException {
    writer_.write(buf, offset, len) ;
  }
  
  public void finish() throws IOException {
    //TODO: Validate the character array here
    finalWriter_.write(writer_.toCharArray()) ;
  }

}
