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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.html.parser.HTMLParser;
import org.exoplatform.services.html.path.NodePathParser;
import org.exoplatform.services.html.path.NodePathUtil;
import org.exoplatform.services.html.util.HyperLinkUtil;
import org.exoplatform.services.html.util.URLCreator;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Nov 30, 2006  
 */
public class TestURL extends BasicTestCase {
	//test ContentBuilder.java
	private URL url_;

	public void setUp() throws Exception {
		this.url_ = new URL("http://htmlparser.sourceforge.net/");
		try{

			System.out.println("URL: " + this.url_.toString());
			System.out.println("URL-CONTENT: " + this.url_.getContent().toString());
			System.out.println("URL-AUTHORITY: " + this.url_.getAuthority());    
			System.out.println("URL-FILE: " + this.url_.getFile());
			System.out.println("URL-HOST: " + this.url_.getHost());
			System.out.println("URL-PATH: " + this.url_.getPath());
			System.out.println("URL-DefaultPort: " + this.url_.getDefaultPort());
			System.out.println("URL-Port: " + this.url_.getPort());
			System.out.println("URL-Protocol: " + this.url_.getProtocol());
			System.out.println("URL-Query: " + this.url_.getQuery());
			System.out.println("URL-Reference: " + this.url_.getRef());
			System.out.println("URL-UserInfo: " + this.url_.getUserInfo());
			System.out.println("URL-ExternalForm: " + this.url_.toExternalForm());
			System.out.println("URL-URI: " + this.url_.toURI().toASCIIString());
			System.out.println("URL-URI: " + this.url_.toURI().toString());
		} catch (java.net.UnknownHostException e) {
			return;
		} catch (java.io.IOException e) {
			return;
		}
	}
	/*
  public void testContentBuilder()throws Exception {
    assertNotNull(this.url_);
    HTMLDocument document = HTMLParser.createDocument(this.url_.openStream(),null);
    
    this.contentBuilder =  new ContentBuilder();    
    RefsDecoder refsDecoder = new RefsDecoder();    
    List<char[]> charArrList = new ArrayList<char[]>();    
    
    this.contentBuilder.build(document.getRoot(),charArrList,refsDecoder);
    
    assertNotNull(document);
    assertNotSame(charArrList.size(),0);
    
    System.out.println("\n\nSHOW ALL CONTENT: ");
    int i=1;
    for(char[] charArr : charArrList){
      System.out.println("LINE-" + i + "\n");
      System.out.println(new String(charArr));
      ++i;
    }
  }*/
  /*
  public void testContentBuilder1()throws Exception {
    String text = "<html>" +
    "<head>" +
     "<title>Let me introduce to myself!</title>" +
     "</head>" +
     "<body>" +
       "<table bgcolor='red' border='1' cellspacing='2' cellpadding='3'>" +
         "<tbody>" +
         "<tr>" +
           "<td>Cell 1</td><td>Cell 2</td>" +
           "<td>cell 3</td><td>Cell 4 </td>" +
         "</tr>" +
         "</tbody>" +
       "</table>" +
       "<!--This is a comment! -->"+                      
     "</body>"+
   "</html>";
    
    HTMLDocument document =HTMLParser.createDocument(text);
    assertNotNull(document);
    
    ContentBuilder contentBuilder = new ContentBuilder();
    RefsDecoder refsDecoder = new RefsDecoder();
    List<char[]> charArrList = new ArrayList<char[]>();
    
//    contentBuilder.build(document.getRoot(), charArrList,refsDecoder);
    NodePath path  = NodePathParser.toPath("html.body.table");
    HTMLNode node = NodePathUtil.lookFor(document.getRoot(), path);
//    System.out.println(node.getTextValue());
//    assertEquals(node.getChildren().get(0).getName(), Name.CONTENT);
    
    charArrList.clear();
    contentBuilder.build(node, charArrList, refsDecoder);
    
//    assertEquals(3, charArrList.size());
    
    System.out.println("\n\nSHOW ALL CONTENT: ");
    int i=1;
    for(char[] charArr : charArrList){
      System.out.println("LINE-" + i + "\n");
      System.out.println(new String(charArr));
      ++i;
    }
  }
  */
  public void testHyperLink() throws Exception {
    String text = "<html>" +
    "<head>" +
     "<title>Let me introduce to myself!</title>" +
     "</head>" +
     "<body>" +
       "<a href=\"http://www.exoplatform.com\"> Please visit our excellent news website!</a><br>" +
       "<table bgcolor='red' border='1' cellspacing='2' cellpadding='3'>" +
         "<tbody>" +
         "<tr>" +
           "<td>Cell 1</td><td>Cell 2</td>" +
           "<td>cell 3</td><td>Cell 4 </td>" +
         "</tr>" +
         "<tr>" +
           "<td>" +
             "<a href=\"/homepage.html\">HomePage</a>" +
            "</td>" +
            "<td>" +
             "<a href=\"/sitemap.html\">SiteMap</a>" +
            "</td>" +
         "</tr>" +
         "</tbody>" +
       "</table>" +
       "<!--This is a comment! -->" +
       "<img src=\"/images/home.gif\" alt=\"short desc\" usemap=\"#homePage.htm\" />"+                      
     "</body>"+
   "</html>";
    
    HTMLDocument document = HTMLParser.createDocument(text);
    assertNotNull(document);
    assertEquals(5,document.getRoot().getChildrenNode().get(1).getChildrenNode().size());
    //Note-Begin.
    assertEquals(document.getRoot().getChildrenNode().get(1).getChildren().size(),document.getRoot().getChildrenNode().get(1).getChildrenNode().size());
    //Note-End.
    System.out.println("\n CHECK DOCUMENT:");
    for(HTMLNode child: document.getRoot().getChildrenNode().get(1).getChildrenNode()){
      System.out.println(child.getName().toString() + " : " + new String(child.getValue()));
    }
    
    HyperLinkUtil hyperlinkUtil = new HyperLinkUtil();
    List<String> linkList = new ArrayList<String>();
    
    //SiteLink---------------------------------------------------------------------------------
    linkList = hyperlinkUtil.getSiteLink(document.getRoot());
    assertEquals(3,linkList.size());
    assertEquals("http://www.exoplatform.com",linkList.get(0).toString());
    
    System.out.println("\nSHOW ALL HYPERLINKs: ");
    for(int i=0;i<linkList.size();i++){
     System.out.println(linkList.get(i).toString()); 
    }
    
    HTMLNode tableNode = NodePathUtil.lookFor(document.getRoot(),NodePathParser.toPath("html.body.table"));
    linkList.clear();
    linkList = hyperlinkUtil.getSiteLink(tableNode);
    assertEquals(2,linkList.size());
    assertEquals("/homepage.html",linkList.get(0).toString());
    
    //ImageLink--------------------------------------------------------------------------------
    String imageLink = hyperlinkUtil.getSingleImageLink(document.getRoot());
    assertEquals("/images/home.gif",imageLink);
    System.out.println("\nIMAGE-LINK: " + imageLink);
    
    //CreateFullNormalLink.---------------------------------------------------------------------    
    HTMLNode bodyNode = document.getRoot().getChildrenNode().get(1);
    assertEquals(bodyNode.getName(),Name.BODY);
    /*
    URL url_ = new URL("http://www.exoplatform.com");    
    assertNotNull(url_);    
    hyperlinkUtil.createFullNormalLink(bodyNode, url_);
    */
    linkList.clear();
    linkList=hyperlinkUtil.getSiteLink(document.getRoot());
    System.out.println("\nSHOW ALL HYPERLINKs: ");
    for(int i=0;i<linkList.size();i++){
     System.out.println(linkList.get(i).toString());     
    }
    HTMLNode row2Node = tableNode.getChildren().get(0).getChildrenNode().get(1);
    assertNotNull(row2Node);
    assertEquals(row2Node.getName(),Name.TR);
    assertEquals("tr",new String(row2Node.getValue()));
    //System.out.println("ROW2: " + row2Node.getTextValue());
    HTMLNode row2Cell2 = row2Node.getChildrenNode().get(1);    
    
    assertNotNull(new URL("http://www.mysite.net"));
    URLCreator urlCreator = new URLCreator();
    hyperlinkUtil.createFullNormalLink(row2Cell2, new URL("http://www.mysite.net"),urlCreator);
    linkList.clear();
    linkList=hyperlinkUtil.getSiteLink(document.getRoot());
    System.out.println("\nSHOW ALL HYPERLINKs: ");
    for(int i=0;i<linkList.size();i++){
     System.out.println(linkList.get(i).toString());     
    }
    
    //CreateFullImageLink.-----------------------------------------------------------------------
    hyperlinkUtil.createFullImageLink(document.getRoot(),new URL("http://www.myImageLink.net"));
    System.out.println("\nNEW IMAGE_LINK1:" + hyperlinkUtil.getSingleImageLink(document.getRoot()));
    hyperlinkUtil.createFullImageLink(document.getRoot(),new URL("http://www.exo.com"),new URLCreator());
    System.out.println("\nNEW IMAGE_LINK2:" + hyperlinkUtil.getSingleImageLink(document.getRoot()));    
  }
  public void testURLCreator()throws Exception {
    String link ="";
    URLCreator urlCreator = new URLCreator();
    link = urlCreator.createURL("http://www.dantri.com.vn","default.aspx?sub_id=1");
    System.out.println("\n\nLINK: " + link);
  }
}
