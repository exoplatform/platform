/***************************************************************************
 * Copyright 2001-2003 The eXoPlatform Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.xml.parser;

import java.io.File;
import java.io.InputStream;

import org.exoplatform.services.chars.CharsDecoder;
import org.exoplatform.services.common.DataReader;
import org.exoplatform.services.common.ServicesContainer;
import org.exoplatform.services.common.ServiceConfig.ServiceType;
import org.exoplatform.services.token.TypeToken;

/**
 * Created by eXoPlatform Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLParser {
  
  private final static String READER_ID = "XMLParserReader";
  
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
    DataReader reader = ServicesContainer.get(ServiceType.SOFT_REFERENCE, READER_ID, DataReader.class);
    return createDocument(reader.loadInputStream(input).toByteArray(), charset);  
  }
  
  public static synchronized XMLDocument createDocument(File file, String charset) throws Exception {
    DataReader reader = ServicesContainer.get(ServiceType.SOFT_REFERENCE, READER_ID, DataReader.class);
    return createDocument(reader.load(file), charset);
  } 
 
}
