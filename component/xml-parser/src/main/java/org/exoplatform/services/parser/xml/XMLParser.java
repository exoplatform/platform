/***************************************************************************
 * Copyright 2001-2003 The  eXo Platform SARL        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.xml;

import java.io.File;
import java.io.InputStream;

import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.text.CharsDecoder;

/**
 * Created by exoplatform Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLParser {
  
  public static synchronized XMLDocument createDocument(char[] data) throws Exception { 
    XMLNode root = new XMLNode("document".toCharArray(), "document", TypeToken.TAG);
    XMLDocument document = new XMLDocument(root);
    XMLToken tokens = new XMLToken();
    document.setXmlType(tokens.getXmlType());
    Services.TOKEN_PARSER.getRef().createBeans(tokens, data);
    DOMParser.parse(tokens, root);
    return document;
  }
  
  public static synchronized XMLDocument createDocument(String text) throws Exception {     
    return createDocument(text.toCharArray());
  }
  
  public static synchronized XMLDocument createDocument(byte[] data, String charset) throws Exception {   
    char [] chars = CharsDecoder.decode(charset, data, 0, data.length);
    return createDocument(chars);
  }  
  
  public static synchronized XMLDocument createDocument(InputStream input, String charset) throws Exception {    
    return createDocument(Services.DATA_BUFFER.getRef().loadInputStream(input).toByteArray(), charset);  
  }
  
  public static synchronized XMLDocument createDocument(File file, String charset) throws Exception {    
    return createDocument(Services.DATA_BUFFER.getRef().load(file), charset);
  } 
 
}
