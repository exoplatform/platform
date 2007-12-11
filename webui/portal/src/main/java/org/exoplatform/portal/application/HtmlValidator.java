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
