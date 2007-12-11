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
