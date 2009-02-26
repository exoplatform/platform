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

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Nov 28, 2006  
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.parser.HTMLParser;
import org.exoplatform.services.html.path.NodePath;
import org.exoplatform.services.html.path.NodePathParser;
import org.exoplatform.services.html.path.NodePathUtil;
import org.exoplatform.services.token.attribute.Attribute;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.Attributes;
import org.exoplatform.test.BasicTestCase;

public class TestHTMLParser extends BasicTestCase {
  
  private File file_ ;
  
  public void setUp() throws Exception {
    //initializes the File object.
    file_ = new File(ClassLoader.getSystemResource("normal.html").getFile());
    System.out.println(file_.getAbsolutePath());
  }
  
  public void testFile() throws Exception {
    //Checks the existing of the HTML file.
    assertNotNull(file_);    
    
    //Checks the charset type of the HTML file.
    HTMLDocument document  = HTMLParser.createDocument(file_, null);    
    assertEquals("ASCII", HTMLParser.getCharset());    
    System.out.println("\n\n\n == > charset " + HTMLParser.getCharset() +"\n\n");    
    assertNotNull(document);
    
    //Gets the NodePath object locating the path of a TAG in the HTML file.
    NodePath path = NodePathParser.toPath("html.body.h2");
    
    //Looks for the Node coresponding to the NodePath object of the HTML document.
    //and checks the existing of this TAG.
    HTMLNode node = NodePathUtil.lookFor(document.getRoot(), path);
    assertEquals(node.getName(), Name.H2);
    assertEquals(node.getName().toString(), "H2");
    
    //Similar as above.
    path = NodePathParser.toPath("html.body.font[1]");
    node = NodePathUtil.lookFor(document.getRoot(), path);
    assertNotNull(node);
    
    //Gets all the attributes of the Node object in the HTML document.
    //simultaneously checks the existing of an Attribute of this Node.
    Attributes attributes = AttributeParser.getAttributes(node);
    assertEquals(attributes.get("size").getValue(), "4");
    
    //Removes an Attribute of a Node.
    //and then checks the existing of this Attribute in the attribute collection of the Node.
    attributes.remove("size");
    System.out.println(node.getTextValue());
    //assertNotNull(attributes.get("size"));   
  }
  public void testDocumentType() throws Exception {
    HTMLDocument document;
    String text = "<html><body><h3>dsfsdf</h3></body></html>";
    document = HTMLParser.createDocument(text);
    assertNotNull(document);
    assertEquals("ASCII", HTMLParser.getCharset());
    System.out.println("CHARSET: " + HTMLParser.getCharset());
    /*
    URL url = new URL("http://www.24h.com.vn");
    document = HTMLParser.createDocument(url.openStream(), null);
    assertNotNull(document);
    System.out.println("ROOT-NAME: " + document.getRoot().getName().toString());
    System.out.println("ROOT-VALUE: " + new String(document.getRoot().getValue()));
    */
  }
  public void testRoot() throws Exception {
    HTMLDocument document = HTMLParser.createDocument(this.file_,null);
    assertNotNull(document);
    //HTMLNode root = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html"));
    HTMLNode root = document.getRoot();
    assertNotNull(root);
    System.out.println("ROOT-NAME: " + root.getName());
    System.out.println("ROOT-VALUE: " + root.getValue().toString());
    System.out.println("ROOT-TEXTVALUE: " + root.getTextValue());    
  } 
  public void testHead() throws Exception {
    HTMLDocument document = HTMLParser.createDocument(this.file_,null);
    String pathStr="html.head";
    NodePath path=NodePathParser.toPath(pathStr);
    HTMLNode node = NodePathUtil.lookFor(document.getRoot(),path);
    assertNotNull(node);
    assertEquals(node.getName(), Name.HEAD);
    assertEquals(node.getName().toString(),"HEAD");
    List<HTMLNode> children = node.getChildrenNode();
    assertNotNull(children);
    int i=0;
    for(HTMLNode child: children){      
      assertNotNull(child);
      ++i;
      //System.out.println(child.getTextValue());
      System.out.println("Child \'" + i + "\' :" + new String(child.getValue()));
    }
  }
  public void testBody() throws Exception {
    HTMLDocument document =HTMLParser.createDocument(this.file_,null);
    String pathStr="html.body";
    NodePath path=NodePathParser.toPath(pathStr);
    HTMLNode node = NodePathUtil.lookFor(document.getRoot(),path);
    assertNotNull(node);
    assertEquals(node.getName(),Name.BODY);
    assertEquals(node.getName().toString(),"BODY");
  }
//Test for TABLE.
  public void testTable() throws Exception {
    //FILE
    assertNotNull(this.file_);
    System.out.println("FILE PATH: " + this.file_.getCanonicalPath());
    
    //HTMLDocument. 
    HTMLDocument htmlDocument = HTMLParser.createDocument(this.file_,null);
    assertNotNull(htmlDocument.getDoctype());
    System.out.println("DOCTYPE: " + htmlDocument.getDoctype().getValue().toString());      
    
    //NodePath.
    NodePath tablePath=NodePathParser.toPath("html.body.table[1]");
    assertNotNull(tablePath);
    System.out.println(tablePath.toString());
    
    //HTMLNode.
    HTMLNode node = NodePathUtil.lookFor(htmlDocument.getRoot(),tablePath);
    assertNotNull(node);
    assertEquals(node.getName(),Name.TABLE);
    assertEquals(node.getName().toString(),"TABLE");
    System.out.println("NODE-NAME: " + node.getName());
    System.out.println("NODE-VALUE: " + new String(node.getValue()));
    System.out.println("NODE-TEXT VALUE: " + node.getTextValue());    
    
    //Attributes.
    Attributes attrs = AttributeParser.getAttributes(node);    
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
    //Modify the value of an Atribute.
    System.out.println("\n\nModify: ");
    for(int j=0;j<attrs.size();j++){
      if(attrs.get(j).getName().equals("caption")){
        attrs.get(j).setValue("Sets a new Caption to the Table!");
        //attrs.set(attrs.get(j));
      }
      if(attrs.get(j).getName().equals("border")){
        //System.out.println("\n\n\n\n= ===s=fsdf\n\n");
        //sysout
        attrs.get(j).setValue("0");
        //attrs.set(attrs.get(j));
      }
    }
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
    
    //Add a new Attribute to the attribute collection of the Node.
    Attribute newAttr = new Attribute("bgcolor","red");
    attrs.set(newAttr);
    assertEquals(newAttr.getName(),"bgcolor");
    assertEquals(newAttr.getValue(),"red");
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
    
    //Clear (remove) an Attribute in the attribute collection of the Node.
    System.out.println("\n\nRemove:");
    assertEquals(true, attrs.contains(new Attribute("bgcolor","red")));
    attrs.remove(new Attribute("bgcolor","red"));
    assertNull(attrs.get("bgcolor"));
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
    //Clear all the attributes of the Node.
    attrs.clear();
    assertEquals(true, attrs.isEmpty());
    //assertEquals(attrs,null);
    
    //Add a new Atribute collection to the Node.
    System.out.println("\n\nAdd:");
    List<Attribute> attrList = new ArrayList<Attribute>();
    attrList.add(new Attribute("caption","New Table"));
    attrList.add(1,new Attribute("border","2"));
    attrList.add(2,new Attribute("bgcolor","blue"));
    assertEquals(true, attrs.addAll(0,attrList));
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
    
    //Insert a new Attribute to the attribute collection of the Node.
    attrs.add(attrs.size()-1,new Attribute("cellspacing","2"));
    //assertEquals(attrs.get(attrs.size()-1),new Attribute("cellspacing","2"));
    assertNotNull(attrs.get(attrs.size()-1));
    System.out.println("\n\nInsert:");
    for(Attribute attr : attrs){      
      System.out.println("NAME: " + attr.getName() + " & " + "VALUE: " + attr.getValue());
    }
  }
}
  
