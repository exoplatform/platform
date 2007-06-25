/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Nov 27, 2006  
 */
import java.util.List;

import junit.framework.TestCase;

import org.exoplatform.services.html.tidy.HTMLTidy;

public class TestHTMLTidy extends TestCase{
  
  
  public void testText() throws  Exception {
    String text = "<html>" +
                  "  <body>"+
                  "    <div>"+
                  "       hello" +
                  "    </div>"+
                  "    <p>"+
                  "    <div>" +
                  "      <% ta co %>asadsd </font> </b>"+
                  "  </div>"  +
                  "  </body>"+
                  "</html>";
    
    HTMLTidy tidy = new HTMLTidy();
    List<String> messages = tidy.check(text.toCharArray());
    System.out.println("\n\n\n");
    for(String msg : messages) {
      System.out.println(msg);
    }
    System.out.println("\n\n\n");  
  }
}
