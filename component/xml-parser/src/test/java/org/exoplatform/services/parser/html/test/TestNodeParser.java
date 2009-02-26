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
package org.exoplatform.services.parser.html.test;

import java.io.File;

import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.parser.HTMLParser;
import org.exoplatform.services.html.parser.NodeImpl;
import org.exoplatform.services.html.path.NodePath;
import org.exoplatform.services.html.path.NodePathParser;
import org.exoplatform.services.html.path.NodePathUtil;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Nov 29, 2006  
 */
public class TestNodeParser extends BasicTestCase {
  
  private File file_;
  
  public void setUp() throws Exception {
    this.file_ = new File(ClassLoader.getSystemResource("normal.html").getFile());
    //assertNotNull(this.file_ );
    System.out.println("FILE PATH: " + this.file_.getAbsolutePath());
  }
  public void testNode() throws Exception {
    //assertNotNull(this.file_);
    System.out.println("FILE PATH: " + this.file_.getAbsolutePath());
    
    //HTMLDocument.
    String text ="<html>" +
                   "<head>" +
                     "<title>My own HTML file</title>" +
                   "</head>" +
                   "<body>" +
                     "<h2>This is a test exercise for me!</h2>" +
                   "</body>"+
                 "</html>";    
    HTMLDocument document = HTMLParser.createDocument(text);
    assertNotNull(document);
    
    String pathStr="html.head.title";    
    NodePath path = NodePathParser.toPath(pathStr);
    assertNotNull(path);
    assertEquals(path.toString(),"HTML[0].HEAD[0].TITLE[0]");
    System.out.println("PATH: " + path.toString());
    
    HTMLNode node = NodePathUtil.lookFor(document.getRoot(),path);
    assertNotNull(node);
    assertEquals(node.getName(),Name.TITLE);
    
    //Add a Tag to HTMLDocument.
    NodeImpl impl = new NodeImpl("h2 id = \"dds\"".toCharArray(), Name.H2);
    node.addChild(impl);
    assertNotNull(node.getChildrenNode().get(1));
    assertEquals(node.getChildren().get(1).getName(),Name.H2);
    System.out.println("THE NEW NODE-NAME: " + node.getChildrenNode().get(1).getName().toString());
    System.out.println("THE NEW NODE-VALUE: " + new String(node.getChildren().get(1).getValue()));
    
    //Add a Table to HTMLDocument.
    HTMLDocument doc = HTMLParser.createDocument("<table border='1'><tr></tr></table>");    
    HTMLNode table =  NodePathUtil.lookFor(doc.getRoot(), NodePathParser.toPath("html.body.table"));    
    node.addChild(table);    
    
    //Remove a Node which is text in format from HTMLDocument.
    System.out.println("\n\nRemove:");
    HTMLNode contentNode = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html.head.title.content"));
    assertNotNull(contentNode);
    assertEquals(Name.CONTENT, contentNode.getName());
    assertEquals("CONTENT", contentNode.getName().toString());
    assertEquals(new String(contentNode.getValue()),contentNode.getTextValue());
    
    System.out.println("NODE-VALUE: " + new String(contentNode.getValue()));
    System.out.println("NODE-TEXTVALUE: " + contentNode.getTextValue());
    
    assertEquals(true, node.getChildren().remove(contentNode));
    
    //Pass the Node which has removed from HTMLDocument into the <h2> TAG.
    HTMLNode h2Node = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html.head.title.h2"));
    assertNotNull(h2Node);
    assertEquals(Name.H2, h2Node.getName());
    assertEquals("H2", h2Node.getName().toString());
    h2Node.addChild(contentNode);
    
    //Show all.
    System.out.println("\n\nShow all the content of HTML file:");
    System.out.println(node.getTextValue());
  }
}
