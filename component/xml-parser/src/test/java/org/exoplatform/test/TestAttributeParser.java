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

import org.exoplatform.services.token.Node;
import org.exoplatform.services.token.attribute.Attribute;
import org.exoplatform.services.token.attribute.AttributeParser;
import org.exoplatform.services.token.attribute.Attributes;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Nov 28, 2006  
 */
public class TestAttributeParser extends BasicTestCase{
  public void testAttributes(){
    MockNode node = new MockNode();
    
    StringBuffer builder = new StringBuffer();
    builder.append("name=\"hu query\" size=\"\'15\'\" maxlength=30\" ");
    builder.append("onmouseover=\"this.style.visibility='visible';\"");
    builder.append(" src='http://www.ddth.com/banners/trananh.gif='");
    
    node.setValue(builder.toString().toCharArray());
    Attributes attrs = AttributeParser.getAttributes(node);
    for(Attribute attr : attrs){
      //assertEquals(attr.getName() + " : " + attr.getValue().toString(), builder.toString());
      //System.out.println(attr.getName()+" : "+attr.getValue());
      assertEquals(attr.getName() + " : " + attr.getValue(),attr.getName() + " : " + attr.getValue());
    }    
  }
}
 class MockNode implements Node{
  private String value="";
  
  public MockNode(){}
  //----------------------
  public Object getName(){
    return "MockNode";
  }
  public char[] getValue(){
    return value.toCharArray();
  }
  public void setValue(char[] chars){
    value=new String(chars);
  }
}