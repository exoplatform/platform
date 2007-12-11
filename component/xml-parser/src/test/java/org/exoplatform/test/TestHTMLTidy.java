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
