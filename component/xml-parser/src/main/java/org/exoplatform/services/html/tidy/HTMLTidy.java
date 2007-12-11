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
package org.exoplatform.services.html.tidy;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.html.NodeConfig;
import org.exoplatform.services.html.Tag;
import org.exoplatform.services.html.parser.NodeImpl;
import org.exoplatform.services.token.TokenParser;
import org.exoplatform.services.token.TypeToken;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 22, 2007  
 */
public class HTMLTidy {
  
  private TokenParser tokenPaser ;
  
  public HTMLTidy() {
    tokenPaser = new TokenParser();
  }

  public synchronized List<String> check(char[] data)  {
    List<String> listMsg = new ArrayList<String>();
    TidyToken tokens = new TidyToken();
    try {
      tokenPaser.createBeans(tokens, data);
    }catch (Exception e) {
      listMsg.add(e.toString());
      return listMsg;
    }
    checkCloseElement(listMsg, tokens);
    
    return listMsg;
  }
  
  final private void checkCloseElement (List<String> listMsg, TidyToken tokens) {    
    if(!tokens.hasNext()) return;
    
    List<NodeImpl> openNodes = new ArrayList<NodeImpl>();
    
    while(tokens.hasNext()){ 
      NodeImpl temp = tokens.pop(); 
      NodeConfig config = temp.getConfig();
      if(config.end() == Tag.FORBIDDEN) continue;
//      System.out.println("==== > "+new String(temp.getValue()));

      if(temp.getType() == TypeToken.TAG) {
        openNodes.add(temp);
        continue;
      }

      if(openNodes.size() < 1) {
        listMsg.add("No open tag for close node </"+new String(temp.getValue())+">");
        continue;
      } 
      
      NodeImpl lastNode = openNodes.get(openNodes.size()-1);
      if(temp.getName() == lastNode.getName()) {
        openNodes.remove(lastNode);
        continue;
      } 
      listMsg.add("Expect end tag for <" + new String(lastNode.getValue())
                                         + ">, found </"+new String(temp.getValue())+">");
    }
    
    if(openNodes.size() > 0) {
      listMsg.add("No close tag node <"+new String(openNodes.get(openNodes.size() - 1).getValue())+">");
    }
  } 

}
