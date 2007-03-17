/*
 * Copyright 2004-2006 The  eXo Platform SARL        All rights reserved.
 *
 * Created on January 24, 2006, 7:48 PM
 */

package org.exoplatform.services.parser.html.parser;

import java.io.File;
import java.io.InputStream;

import org.exoplatform.services.parser.chardet.Detector;
import org.exoplatform.services.parser.chardet.ICharsetDetectionObserver;
import org.exoplatform.services.parser.chardet.PSMDetector;
import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.html.HTMLDocument;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.text.CharsDecoder;

/**
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public final class HTMLParser {

  private static String charset_ = null;

  public static synchronized HTMLNode clone(HTMLNode node){
    NodeImpl nodeImpl = (NodeImpl)node;
    HTMLNode newNode = null;
    if(nodeImpl.getType() == TypeToken.CONTENT || nodeImpl.getType() == TypeToken.COMMENT){
      newNode = new NodeImpl(nodeImpl.getValue(), nodeImpl.getConfig());
    }else{
      newNode = new NodeImpl(nodeImpl.getValue(), nodeImpl.getConfig(), nodeImpl.getType());
    }
    return newNode;
  }

  public static synchronized HTMLDocument createDocument(char[] data) throws Exception { 
    HTMLDocument document = new HTMLDocument();
    CharsToken tokens = new CharsToken();
    tokens.setDocument(document);
    Services.TOKEN_PARSER.getRef().createBeans(tokens, data);
    Services.parse(tokens, document);
    return document;
  }

  public static synchronized HTMLDocument createDocument(String text) throws Exception { 
    return createDocument(text.toCharArray());
  }

  public static synchronized HTMLDocument createDocument(byte[] data, String charset) throws Exception {
    if(charset == null) charset = detect(data);
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return createDocument(chars);
  }  

  public static synchronized HTMLDocument createDocument(InputStream input, String charset) throws Exception {      
    return createDocument(Services.DATA_BUFFER.getRef().loadInputStream(input).toByteArray(), charset);  
  }

  public static synchronized HTMLDocument createDocument(File file, String charset) throws Exception {
    return createDocument(Services.DATA_BUFFER.getRef().load(file), charset);
  }  

  private static String detect(byte [] buf){
    Detector det = new Detector(PSMDetector.ALL) ;
    charset_ = null;
    det.init(new ICharsetDetectionObserver() {
      public void notify(String charset) {        
        charset_ = charset;
      }
    });

    boolean isAscii = true ;
    int len = buf.length;
    
    isAscii = det.isAscii(buf, len);   
    if (!isAscii) det.doIt(buf, len, false);
    det.dataEnd();
    
    if (isAscii) charset_ = "ASCII";
    return charset_;
  }
  
  public static String getCharset(){ return charset_; }
}