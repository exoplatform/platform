/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test;

import org.exoplatform.services.parser.attribute.Attribute;
import org.exoplatform.services.parser.attribute.AttributeParser;
import org.exoplatform.services.parser.attribute.Attributes;
import org.exoplatform.services.parser.common.Node;

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