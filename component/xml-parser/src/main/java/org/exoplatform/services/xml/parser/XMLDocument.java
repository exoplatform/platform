/***************************************************************************
 * Copyright 2001-2003 The eXoPlatform Studio        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.xml.parser;
/**
 * Created by eXoPlatform Studio
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class XMLDocument  {
  
  private XMLNode root;
  
  private XMLNode xmlType ;
  
  private boolean encode = false;
  
  public XMLDocument(XMLNode root){  this.root = root; }  
  
  public XMLNode getRoot(){ return root; }

  public XMLNode getXmlType() { return xmlType; }

  public void setXmlType(XMLNode xmlType) { this.xmlType = xmlType; }
  
  public String getTextValue(){
    StringBuilder builder = new StringBuilder();
    if(xmlType != null){
      builder.append('<').append(xmlType.getNodeValue()).append(">\n");
    }else{
      builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    }    
    root.buildValue(builder, encode);    
    return builder.toString();
  }

  public boolean isEncode() { return encode; }

  public void setEncode(boolean encode) { this.encode = encode; }
}
