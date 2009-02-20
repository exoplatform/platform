/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.application.gadget;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Oct 23, 2008  
 */
public class Source {
  
  private String name;
  private byte [] content;
  private String mimeType = "text/plain";
  private String encoding = "UTF-8";
  private long length = 0;
  private Calendar lastModified;
  
  public Source(String name) {
    this.name = name;
  }
  
  public Source(String name, String mimeType, String encoding) {
    this.name = name;
    this.mimeType = mimeType;
    this.encoding = encoding;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getMimeType() { return mimeType; }
  public void setMimeType(String mimeType) { this.mimeType = mimeType; }

  public String getEncoding() { return encoding; }
  public void setEncoding(String encoding) { this.encoding = encoding; }

  public Calendar getLastModified() { return lastModified; }
  public void setLastModified(Calendar lastModified) { this.lastModified = lastModified; }
  
  public long getLength() { return length; }

  public void setTextContent(String text) throws Exception {
    String textContent = (text == null) ? "" : text;
    content = textContent.getBytes(encoding);
    length = content.length;
  }
  public String getTextContent() throws Exception { return new String(content, encoding); }
  
  public void setStreamContent(InputStream is) throws Exception {
    content = new byte[is.available()];
    is.read(content);
    length = content.length;
  }
  public InputStream getStreamContent() { return new ByteArrayInputStream(content); }

}