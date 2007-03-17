/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.attribute;

import java.util.List;

import org.exoplatform.services.parser.common.Node;
import org.exoplatform.services.parser.text.SpecChar;
import org.exoplatform.services.parser.text.StringTokenizer;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 28, 2006  
 */
final class AttrParser {
  
  final synchronized Attributes getAttributes(Node node) {
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
              if( value.charAt(value.length()-1) == '\''){
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
