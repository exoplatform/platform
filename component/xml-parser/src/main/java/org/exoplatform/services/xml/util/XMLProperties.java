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
package org.exoplatform.services.xml.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.text.TextConfig;
import org.exoplatform.services.token.TypeToken;
import org.exoplatform.services.xml.parser.XMLDocument;
import org.exoplatform.services.xml.parser.XMLNode;
import org.exoplatform.services.xml.parser.XMLParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 29, 2006
 */
@SuppressWarnings("serial")
public class XMLProperties extends  HashMap<String, String> {
  
  protected XMLDocument document;
  protected XMLNode node_ ;  
  
  public XMLProperties(InputStream input, String name, boolean readonly) throws Exception {
    toProperties(XMLParser.createDocument(input, TextConfig.CHARSET), name, readonly);
  }
  
  public XMLProperties(File file, String name, boolean readonly) throws Exception {
    toProperties(XMLParser.createDocument(file, TextConfig.CHARSET), name, readonly);
  }
  
  public void toProperties(XMLDocument xmlDocument, String name, boolean readonly) throws Exception {
    XMLNode node = null;
    XMLNode root = xmlDocument.getRoot();
    if (root.getChildren().size() > 0) {
      node = getNode(root, name);
    }
    if(!readonly) {
      this.node_ = node;
      this.document = xmlDocument;
      if(node_ == null){
        node_ = new XMLNode(name.toCharArray(), name, TypeToken.TAG);
        document.getRoot().addChild(node_);        
      }
    }

    if(node == null) return;
    List<XMLNode> children = node.getChildren();
    if(children == null) return;
    for(XMLNode child : children){
      String value = null;
      if(child.getChildren().size() > 0) value = child.getChild(0).getTextValue();
      super.put(child.getNodeValue(), value);
    }    
  }
  
  public byte [] getBytes() throws Exception {
    if(document == null) return null;
    Iterator<String>  iter = keySet().iterator();
    node_.getChildren().clear();
    while(iter.hasNext()){
      String key  = iter.next();
      XMLNode ele = new XMLNode(key.toCharArray(), key, TypeToken.TAG);
      XMLNode child = new XMLNode(get(key).toCharArray(), null, TypeToken.CONTENT);
      ele.addChild(child);
      node_.addChild(ele);
    }
    return document.getTextValue().getBytes(TextConfig.CHARSET);
  }
  
  public OutputStream toOutputStream() throws Exception {
    if(document == null) return null;
    ByteArrayOutputStream output = new ByteArrayOutputStream();    
    output.write(getBytes());
    return output;
  }
  
  protected XMLNode getNode(XMLNode root, String name) {
    if(root.getName() == null) return null;
    if(root.getName().equalsIgnoreCase(name)) return root;
    List<XMLNode> children = root.getChildren();
    for(XMLNode child : children){
      XMLNode value = getNode(child, name);
      if(value != null) return value;
    }
    return null;
  }  
}
