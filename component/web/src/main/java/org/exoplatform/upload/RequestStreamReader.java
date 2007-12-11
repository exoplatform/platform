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
package org.exoplatform.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.services.html.refs.RefsDecoder;

/**
 * Author : Oleg Kalnichevski
 *          oleg@ural.ru
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 26, 2006
 * 
 */
class RequestStreamReader {
  
  private static final byte LF = 0x0A;
  private static final byte DASH = 0x2D;
  private static final byte CR = 0x0D;

  private static final byte[] HEADER_SEPARATOR = {CR, LF, CR, LF };
  private static final byte[] BOUNDARY_PREFIX = {CR, LF, DASH, DASH};

  private static final String CONTENT_DISPOSITION = "Content-disposition";
  private static final String FORM_DATA = "form-data";
  private static final int HEADER_PART_SIZE_MAX = 10240;
  private static final String ATTACHMENT = "attachment";
  private static final int DEFAULT_BUFSIZE = 4096;
  
  static final String CONTENT_TYPE = "content-type";


  private int head;
  private int tail;
  private int bufSize;
  private byte[] buffer;

  private UploadResource upResource_;
  private RefsDecoder refsDecoder_;

  RequestStreamReader(UploadResource upResource){
    upResource_ = upResource;    
    head = 0;
    tail = 0;
    bufSize = DEFAULT_BUFSIZE;
    buffer = new byte[bufSize];
    refsDecoder_ = new RefsDecoder();
  }

  void readBodyData(HttpServletRequest request, OutputStream output) throws IOException {
    int pad;
    int bytesRead;
    int total = 0;

    byte[] bdr = getBoundary(request.getContentType());
    byte [] boundary = new byte[bdr.length + BOUNDARY_PREFIX.length];
    int keepRegion = boundary.length + 4 ;

    InputStream input = null;
    try {
      input = request.getInputStream();

      while(upResource_.getStatus() == UploadResource.UPLOADING_STATUS) {   
        if (tail - head > keepRegion) {
          pad = keepRegion;
        } else {
          pad = tail - head;
        }
        output.write(buffer, head, tail - head - pad);
        upResource_.addUploadedBytes(tail - head - pad) ;

        total += tail - head - pad;
        System.arraycopy(buffer, tail - pad, buffer, 0, pad);

        head = 0;
        bytesRead = input.read(buffer, pad, bufSize - pad);

        if (bytesRead != -1) {
          tail = pad + bytesRead;
          continue;
        }   
        output.flush();
        total += pad;
        break;     
      }
    } finally { 
      if(input != null) input.close() ;
      if(output != null) output.close();  
    }
  }

  Map<String, String> parseHeaders(InputStream input, String headerEncoding)  throws IOException {
    String txtHeaders =  readHeaders(input, headerEncoding);
    return parseHeaders(txtHeaders);
  }

  Map<String, String> parseHeaders(String headerPart) {
    Map<String, String> headers = new HashMap<String, String>();
    char[] chars = new char[1024];
    boolean done = false;
    int j = 0;
    int i;
    String header, headerName, headerValue;
    while(!done) {
      i = 0;           
      while (i < 2 || chars[i - 2] != '\r' || chars[i - 1] != '\n') {
        chars[i++] = headerPart.charAt(j++);
      }
      header = new String(chars, 0, i - 2);
      if (header.length() < 1) {
        done = true;
        continue;
      } 
      if (header.indexOf(':') == -1) continue;          
      headerName = header.substring(0, header.indexOf(':')).trim().toLowerCase();
      headerValue = header.substring(header.indexOf(':') + 1).trim();
      if (getHeader(headers, headerName) != null) {
        headers.put(headerName, getHeader(headers, headerName) + ',' + headerValue);
        continue;
      } 
      headers.put(headerName, headerValue);        
    }
    return headers;
  }

  private String getHeader(Map<String, String> headers, String name) {
    return headers.get(name.toLowerCase());
  }

  private String readHeaders(InputStream input, String headerEncoding) throws IOException {
    int i = 0;
    byte[] b = new byte[1];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int sizeMax = HEADER_PART_SIZE_MAX;
    int size = 0;
    while (i < HEADER_SEPARATOR.length) {
      try {
        b[0] = readByte(input);
      } catch (IOException e) {
        throw new IOException("Stream ended unexpectedly");
      }
      size++;
      if (b[0] == HEADER_SEPARATOR[i]) i++; else i = 0;
      if (size <= sizeMax) baos.write(b[0]);      
    }

    if (headerEncoding != null) {
      try {        
        return baos.toString(headerEncoding);
      } catch (Exception e) {
      }
    } 
    return baos.toString("UTF-8");
  }
 
  private byte readByte(InputStream input) throws IOException {
    if (head != tail) return buffer[head++];  
    head = 0;
    tail = input.read(buffer, head, bufSize);
    if (tail == -1) throw new IOException("No more data is available");
    return buffer[head++];
  }

  private byte[] getBoundary(String contentType) {
    ParameterParser parser = new ParameterParser();
    parser.setLowerCaseNames(true);
    Map<String, String> params = parser.parse(contentType, ';');
    String boundaryStr = params.get("boundary");

    if (boundaryStr == null) return null;    
    try {
      return boundaryStr.getBytes("ISO-8859-1");
    } catch (Exception e) {
      return boundaryStr.getBytes();
    }
  }

  String getFileName(Map<String, String> headers) {
    String cd = getHeader(headers, CONTENT_DISPOSITION);
    if (cd == null)  return null;
    String cdl = cd.toLowerCase();
    if (!cdl.startsWith(FORM_DATA) && !cdl.startsWith(ATTACHMENT))  return null;
    ParameterParser parser = new ParameterParser();
    parser.setLowerCaseNames(true);
    Map<String, String> params = parser.parse(cd, ';');
    if (params.containsKey("filename")) {  
      String fileName = params.get("filename");
      fileName = refsDecoder_.decode(fileName);
      if (fileName != null)  return fileName.trim();
      return "";
    }
    return null;
  }
  
}
