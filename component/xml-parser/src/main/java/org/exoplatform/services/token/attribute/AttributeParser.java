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

package org.exoplatform.services.token.attribute;

import java.util.List;

import org.exoplatform.services.chars.SpecChar;
import org.exoplatform.services.chars.StringTokenizer;
import org.exoplatform.services.common.ThreadSoftRef;
import org.exoplatform.services.token.Node;
/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public final class AttributeParser {
  
  static ThreadSoftRef<AttributeParser> PARSER = new ThreadSoftRef<AttributeParser>(AttributeParser.class);

  public static Attributes getAttributes(Node<?> node) { return PARSER.getRef().parseAttributes(node); }
  
  final synchronized private Attributes parseAttributes(Node<?> node) {
    String text = String.valueOf(node.getValue());    
    text = text.substring(text.toUpperCase().indexOf(node.getName().toString().toUpperCase())+1);   
    StringTokenizer split = new StringTokenizer(new char[]{'\"'}, 
                            new char[]{SpecChar.s,SpecChar.t, SpecChar.b, SpecChar.f, SpecChar.r});
    List<String> elements = split.split(text);
    Attributes list  = new Attributes(node);
    for(int i = 0; i<elements.size(); i++){     
      if(elements.get(i).indexOf("=") > 0){     
        String name = elements.get(i);
        int idx = name.indexOf("=");
        if(idx != name.length()-1){
          String value = name.substring(idx+1);
          name = name.substring(0, idx);
          if(value.charAt(0) == '\'' && value.length() > 1){
            if(value.charAt(value.length()-1) == '\''){              
              value = value.substring(1, value.length()-1);
            }else{
              value = value.substring(1, value.length()); 
            }
          }           
          Attribute attr = new Attribute(name, value);
          list.add(attr);
        }else{
          name = name.substring(0, idx);
          Attribute attr = new Attribute(name, "");
          int j = i+1;
          StringBuilder value = new StringBuilder();
          int start = -1; 
          while(j < elements.size()){ 
            String txt = elements.get(j).trim();
            if(txt.length() == 0 || txt.equals("\"")){
              j++;
              start++;
              continue;
            }
            if(start == 1) break;
            if(start == -1 && txt.indexOf("=") > -1) break;            
            if(value.length() > 0) value.append(' ');
            value.append(txt);
            j++;
          }
          if(value.length() > 0){
            if(value.charAt(0) == '\'') {
              if( value.charAt(value.length()-1) == '\'' && value.length() > 1){
                value = new StringBuilder(value.substring(1, value.length()-1));              
              }else{
                value = new StringBuilder(value.substring(1, value.length()));
              }
            }
            attr.setValue(value.toString());
            list.add(attr);
          }
          i = j-1;
        }
      }
    }
    list.trimToSize();
    return list;
  }  
}
