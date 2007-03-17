/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.html.test;

import java.io.File;

import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.html.HTMLDocument;
import org.exoplatform.services.parser.html.HTMLNode;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.parser.HTML;
import org.exoplatform.services.parser.html.parser.HTMLParser;
import org.exoplatform.services.parser.html.parser.NodeImpl;
import org.exoplatform.services.parser.html.path.NodePath;
import org.exoplatform.services.parser.html.path.NodePathParser;
import org.exoplatform.services.parser.html.path.NodePathUtil;
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
    this.file_ = new File("src"+File.separatorChar+"resources"+File.separatorChar+"normal.html");
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
    NodeImpl impl = new NodeImpl("h2 id = \"dds\"".toCharArray(), HTML.getConfig("H2"), TypeToken.TAG);
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
    assertEquals(contentNode.getName(),Name.CONTENT);
    assertEquals(contentNode.getName().toString(),"CONTENT");
    assertEquals(new String(contentNode.getValue()),contentNode.getTextValue());
    
    System.out.println("NODE-VALUE: " + new String(contentNode.getValue()));
    System.out.println("NODE-TEXTVALUE: " + contentNode.getTextValue());
    
    assertEquals(node.getChildren().remove(contentNode),true);
    
    //Pass the Node which has removed from HTMLDocument into the <h2> TAG.
    HTMLNode h2Node = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html.head.title.h2"));
    assertNotNull(h2Node);
    assertEquals(h2Node.getName(),Name.H2);
    assertEquals(h2Node.getName().toString(),"H2");
    h2Node.addChild(contentNode);
    
    //Show all.
    System.out.println("\n\nShow all the content of HTML file:");
    System.out.println(node.getTextValue());
  }
}
