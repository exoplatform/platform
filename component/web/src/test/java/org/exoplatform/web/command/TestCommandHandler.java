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
