/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.web.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.test.BasicTestCase;
import org.exoplatform.web.WebAppController;

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
    Map<String, Object> props = new HashMap<String, Object>() ;
    props.put("intProp", "10") ;
    props.put("stringProp", "welcome to eXo") ;
    props.put("doubleValues", new String[] {"10.0", "-6.7", "7.0"}) ;
    props.put("booleanValue", "true") ;
    Command command = handler.createCommand("org.exoplatform.web.command.TestCommandHandler$CommandTest2", props) ;
    command.execute(null, null, null);
  }
  
   static public class CommandTest2 extends CommandTest {
    
     private boolean booleanValue = false;
     
     public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
       super.execute(controller, req, res);
       System.out.println(" \n\n\n === >"+booleanValue+"\n\n");
     }
  }
  
  static public class CommandTest extends Command {
    
    private double [] doubleValues;
    
    private Integer    intProp    ;
    private String stringProp ; 
    
    public void execute(WebAppController controller, HttpServletRequest req, HttpServletResponse res) throws Exception {
      System.out.println("\n\n");
      System.out.println("int    prop : "  +  intProp)   ;
      System.out.println("String prop : "  +  stringProp)   ;
      for(double ele : doubleValues) {
        System.out.println("===== > "+ele);
      }
      System.out.println("\n\n");
    }
    
    public void setStringProp(String value) {
      System.out.println("\n\n  invoke setter "+value +"\n\n");
      stringProp = value;
    }
  }
}
