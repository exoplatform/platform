/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class TestCommandHandler extends BasicTestCase {
  
  public TestCommandHandler(String name){
    super(name);
  }

  
  public void testCommandHandler() throws Exception {
    CommandHandler handler = new CommandHandler() ;
    Map<String,String> props = new HashMap<String,String>() ;
    props.put("intProp", "10") ;
    props.put("stringProp", "A String") ;
    handler.createCommand("org.exoplatform.web.command.TestCommandHandler$CommandTest", props) ;
  }
  
  static public class CommandTest extends Command {
    private int    intProp    ;
    private String stringProp ; 
    
    public void execute() throws Exception {
      System.out.println("int    prop : "  +  intProp)   ;
      System.out.println("String prop : "  +  stringProp)   ;
    }
    
  }
}
