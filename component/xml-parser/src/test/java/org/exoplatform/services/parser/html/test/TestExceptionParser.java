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
import java.util.List;

import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.parser.HTML;
import org.exoplatform.services.html.parser.HTMLParser;
import org.exoplatform.services.html.path.NodePath;
import org.exoplatform.services.html.path.NodePathParser;
import org.exoplatform.services.html.path.NodePathUtil;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Nov 30, 2006  
 */
public class TestExceptionParser extends BasicTestCase {
  private File file_;
  
  public void setUp()throws Exception{
//    this.file_= new File("src"+File.separatorChar+"resources"+File.separatorChar+"normal.html");
    this.file_ = new File(ClassLoader.getSystemResource("normal.html").getFile()) ;
    //assertNotNull(this.file_);
    System.out.println("\n\nFILE PATH: " + this.file_.getCanonicalPath());
  }
  public void testExistFile()throws Exception {
    assertNotNull(this.file_);
    //assertNull(this.file_.getParentFile());
    assertNotNull(this.file_.getParentFile());
    assertEquals(true, this.file_.canRead());
    assertEquals(true, this.file_.canWrite());
    assertEquals(true, this.file_.exists());
    
//    assertEquals(false, this.file_.isAbsolute());
    
    assertEquals(false, this.file_.isDirectory());
    assertEquals(true, this.file_.isFile());
    assertEquals(false, this.file_.isHidden());
    
    System.out.println("FILE-NAME: " + this.file_.getName());
    System.out.println("FILE-PARENT: " + this.file_.getParent());
    System.out.println("FILE-PATH: " + this.file_.getPath());
    System.out.println("FILE-CLASS: " + this.file_.getClass().getName());
  }
  
  public void testHTMLDocument() throws Exception{
    String text = "<html>" +
                   "<head>" +
                    "<title>Let me introduce to myself!</title>" +
                    "</head>" +
                    "<body>" +
                      "<table bgcolor='red' border='1' cellspacing='2' cellpadding='3'>" +
                        "<tbody>" +
                        "<tr>" +
                          "<td><h1>Cell 1</h1></td><td><h2>Cell 2<h2></td>" +
                        "</tr>" +
                        "</tbody>" +
                      "</table>" +
                      "<!--This is a comment! -->"+                      
                    "</body>"+
                  "</html>";
    HTMLDocument document = HTMLParser.createDocument(text);
    assertNotNull(document);
    System.out.println("DOCUMENT-TEXTVALUE: \n" + document.getTextValue());
    
    //assertEquals(HTMLParser.getCharset(),"ASCII");
    assertNull(HTMLParser.getCharset());    
    System.out.println("CHARSET: " + HTMLParser.getCharset());
    
    //assertNull(document.getRoot());
    assertNotNull(document.getRoot());
    System.out.println("ROOT-NAME: " + document.getRoot().getName().toString());
    System.out.println("ROOT-VALUE: " + new String(document.getRoot().getValue()));
    System.out.println("ROOT_TEXTVALUE: \n" + document.getRoot().getTextValue());
    assertEquals(document.getRoot().getName(),Name.HTML);
    
    assertNull(document.getDoctype());
    
    //ROOT.
    HTMLNode root = document.getRoot();
    assertNotNull(root);
    assertEquals(root.getParent(),null);
    List<HTMLNode> children = root.getChildren();
    //ArrayList<HTMLNode> children = (ArrayList<HTMLNode>)root.getChildren();
    assertNotNull(children);
    assertEquals(children.size(),2);
    assertEquals(children.get(0).getChildren().size(),1);
    assertEquals(children.get(0).getChildren().get(0).getChildren().size(),1);
    
    for(int i=0;i<children.size();i++){
      if (i==0) {
        assertEquals(children.get(i).getName(),Name.HEAD);
        assertEquals(children.get(i).getName().toString(),"HEAD");
      }
      if(i==1){
        assertEquals(children.get(i).getName(),Name.BODY);
        assertEquals(children.get(i).getName().toString(),"BODY");
      }
    }
    
    List<HTMLNode> children_ = root.getChildrenNode();
    assertEquals(children_.size(),2);
    assertEquals(children.size(),children_.size());
    
    //BODY.
    NodePath path_ = NodePathParser.toPath(children.get(1));
    assertNotNull(path_);
    System.out.println("NODEPATH-CLASS: " + path_.getClass().getName());
    System.out.println("PATH OF BODY: " + path_.toString());
    HTMLNode bodyNode = NodePathUtil.lookFor(document.getRoot(),path_);
    assertEquals(bodyNode, children.get(1));
    
    String bodyPath = "html.body";    
    HTMLNode bodyNode1 = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath(bodyPath));
    assertNotNull(bodyNode1);
    assertEquals(bodyNode1, bodyNode);
    
    //Test Node.getChildren() and Node.getChildrenNode() methods.
    List<HTMLNode> bodyChildren = bodyNode.getChildren();
    List<HTMLNode> bodyChildren1=bodyNode.getChildrenNode();
    assertEquals(bodyChildren.size(),bodyChildren1.size());
    assertEquals(bodyChildren.size(),2);
    assertEquals(bodyChildren1.size(),2);
 
    //TABLE
    String pathStr="html.body.table[0]";
    NodePath path = NodePathParser.toPath(pathStr);
    assertNotNull(path);
    HTMLNode tableNode = NodePathUtil.lookFor(document.getRoot(),path);
    assertNotNull(tableNode);
    assertEquals(tableNode.getName(),Name.TABLE);
    assertEquals(tableNode.getName().toString(),"TABLE");
    assertEquals(tableNode.getChildren().size(),1);
    assertEquals(true, tableNode.isNode(Name.TABLE));
    //assertNotSame(tableNode.isNode(Name.TABLE),tableNode.isNode(tableNode.getName().toString()));
    assertEquals(tableNode.isNode(Name.TABLE),tableNode.isNode(tableNode.getName().name()));
    assertEquals(tableNode.isNode(Name.TABLE),tableNode.isNode(tableNode.getName().toString()));
        
    assertEquals(new String(tableNode.getValue()),"table bgcolor='red' border='1' cellspacing='2' cellpadding='3'");
    System.out.println("\n\nTABLE-VALUE: " +new String(tableNode.getValue()));
    /*
    assertEquals(tableNode.getTextValue(),("<table bgcolor='red' border='1' cellspacing='2' cellpadding='3'>" +
                        "<tbody>" +
                        "<tr>" +
                          "<td><h1>Cell 1</h1></td><td><h2>Cell 2<h2></td>" +
                        "</tr>" +
                        "</tbody>" +
                      "</table>"));
     *///==>ERROR.
    //System.out.println("TABLE-TEXTVALUE: " + tableNode.getTextValue());
    
    List<HTMLNode> tableChildren = tableNode.getChildrenNode();
    assertEquals(tableChildren.size(),1);
    assertEquals(tableChildren.get(0).getName(),Name.TBODY);
    assertEquals(tableChildren.get(0).getChildren().get(0).getName(),Name.TR);
    assertEquals(tableChildren.get(0).getChildren().get(0).getChildren().get(0).getName(),Name.TD );
    assertEquals(tableChildren.get(0).getChildren().get(0).getChildren().size(),2);
    
    assertNotNull(HTML.getConfig(Name.TABLE));
    
    HTMLNode _node = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html.body"));
    assertNotNull(_node);
    
    //There are two Ways for creating a Table.
    //1st Way.
   /* NodeImpl nodeImpl =new NodeImpl("table bgcolor='blue' border='0'".toCharArray(),HTML.getConfig("TABLE"),TypeToken.TAG);    
    _node.addChild(nodeImpl);    
    HTMLNode _node1 = new NodeImpl("tbody".toCharArray(),HTML.getConfig(Name.TBODY),TypeToken.TAG);
    nodeImpl.addChild(_node1);    
    HTMLNode _node11=new NodeImpl("tr".toCharArray(),HTML.getConfig(Name.TR),TypeToken.TAG);
    _node1.addChild(_node11);
    HTMLNode _node111a = new NodeImpl("td".toCharArray(),HTML.getConfig(Name.TD),TypeToken.TAG);
    _node111a.setValue("td id=\"td1\"".toCharArray());
    HTMLNode _node111b = new NodeImpl("td".toCharArray(),HTML.getConfig(Name.TD),TypeToken.TAG);
    _node11.addChild(_node111a);
    _node11.addChild(_node111b);  
        
    System.out.println("NODE111a: -TEXTVALUE: \n" + _node111a.getTextValue());
    System.out.println("NODE111a: -VALUE: " + new String(_node111a.getValue()));
    
    //2nd Way.
    HTMLDocument doc = HTMLParser.createDocument("<table bgcolor=\"red\" border=\"1\"><tbody><tr><td></td></tr></tbody></table>");
    HTMLNode table = NodePathUtil.lookFor(doc.getRoot(),NodePathParser.toPath("html.body.table"));
    _node.addChild(table);    
    
    //-------------------------------
    HTMLNode contentNode = new NodeImpl("sfsdfsdf Content".toCharArray(),HTML.getConfig(Name.CONTENT),TypeToken.CONTENT);
    _node.addChild(contentNode);
    
    HTMLNode commentNode = new NodeImpl("sdfsdfsdf Comment".toCharArray(),HTML.getConfig(Name.COMMENT),TypeToken.COMMENT);
    _node.addChild(commentNode);
    
    System.out.println(document.getTextValue());*/
  }
}
