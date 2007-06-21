/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Nov 28, 2006  
 */
import java.io.File;
import java.util.List;

import org.exoplatform.services.html.HTMLNode;
import org.exoplatform.services.html.parser.HTMLParser;

public class TestParser extends BasicTestCase{
  
  public static void print(String text, HTMLNode element){
    List<HTMLNode> children = element.getChildren();
    for(HTMLNode node : children) System.out.print(text + " " + node);
  }
  
  public void testParser()throws Exception{
    HTMLNode node = HTMLParser.createDocument(new File("C:\\Documents and Settings\\exo\\Desktop\\130033.htm"),"utf-8").getRoot();
    //System.out.println(node.getTextValue());
    //print("", node);
    assertEquals(node.getTextValue(),node.getTextValue());
  }
}
