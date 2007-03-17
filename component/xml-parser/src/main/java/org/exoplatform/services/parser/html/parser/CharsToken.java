/*
 * Copyright 2004-2006 The  eXo Platform SARL        All rights reserved.
 *
 * Created on January 24, 2006, 7:50 PM
 */

package org.exoplatform.services.parser.html.parser;

import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.common.TokenParser.Factory;
import org.exoplatform.services.parser.html.HTMLDocument;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.html.NodeConfig;
import org.exoplatform.services.parser.text.CharsUtil;
import org.exoplatform.services.parser.text.SpecChar;

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
@SuppressWarnings("serial")
public class CharsToken extends Factory<NodeImpl> { 

  private char [] script = {'s','c','r','i','p','t'};
  private char [] style = {'s','t','y','l','e'};
  
  private HTMLDocument document;
  
  public void setDocument(HTMLDocument document) { this.document = document; }

  public int create(char [] data, int start, int end, TypeToken type){ 
    if(start >= end) return end;
    if(start > data.length) return data.length;   
    char [] value = CharsUtil.cutAndTrim(data, start, Math.min(end, data.length)); 
   
    NodeConfig config = null;
    if(value.length < 1) return end;
    
    if(type != TypeToken.TAG){     
      if(type == TypeToken.COMMENT) config = HTML.getConfig(Name.COMMENT);
      else config = HTML.getConfig(Name.CONTENT);
      push(new NodeImpl(value, config));
      return end;
    }
    
    if(value[0] == SpecChar.END_TAG){
      if(value.length <= 1) return end;     
      value  = CharsUtil.cutAndTrim(value, 1, value.length);
      config = HTML.getConfig(new String(value).toUpperCase());
      if(config != null){
        push(new NodeImpl(value, config, TypeToken.CLOSE));
      }else{
        char [] newValue = new char[value.length+1];
        newValue[0] = SpecChar.END_TAG; 
        System.arraycopy(value, 0, newValue, 1, value.length);
        push(new NodeImpl(newValue, HTML.getConfig(Name.UNKNOWN)));
        return end;
      }
      return end;
    }
    String nameValue = new String(CharsUtil.cutBySpace(value, 0)).toUpperCase();
    if(nameValue.charAt(nameValue.length()-1) == SpecChar.END_TAG){      
      nameValue = nameValue.substring(0, nameValue.length()-1).trim();      
    }   
    if(nameValue.equals("!DOCTYPE")){
      if(document != null) 
        document.setDoctype(new NodeImpl(value, HTML.getConfig(Name.DOCTYPE)));
      return end;
    }
    config = HTML.getConfig(nameValue); 
    if(config != null){
      push(new NodeImpl(value, config, TypeToken.TAG));    
    }else{
      push(new NodeImpl(value, HTML.getConfig(Name.UNKNOWN)));
      return end;
    }
    if(config.name() == Name.SCRIPT){
      return findEndScript(data, script, end);
    }else if(config.name() == Name.STYLE){
      return findEndScript(data, style, end);
    }
    return end;
  }  

  private int findEndScript(char [] value, char [] c, int start){    
    int [] idx = indexEndNode(value, c, start);   
    if(idx.length < 1) return start;   
    create(value, start+1, idx[0], TypeToken.CONTENT);    
    return create(value, idx[1], idx[2], TypeToken.TAG);
  }  
 
  private int[] indexEndNode(char [] value, char [] c, int start){
    boolean is = false;
    int [] idx = new int[3];
    for(int i = start; i < value.length; i++){
      if(value[i] != SpecChar.OPEN_TAG) continue;
      is = true;
      idx[0] = i;
      int k = i+1;
      if(value[k] == SpecChar.PUNCTUATION_MASK 
          && Services.TOKEN_PARSER.getRef().isComment(value, k)){
        int startComment = k;          
        int endComment  = Services.TOKEN_PARSER.getRef().findEndComment(value, k); 
        startComment = create(value, startComment-1, endComment, TypeToken.COMMENT); 
        if(startComment < value.length  && value[startComment] == SpecChar.OPEN_TAG) {
          i = startComment + 1;
          continue;
        }
        break;
      }
      while(k < value.length){
        if(value[k] == SpecChar.END_TAG)  idx[1] = k;
        if(value[k] != SpecChar.END_TAG && !Character.isWhitespace(value[k])) break;
        k++;
      }           
      for(int j = 0; j< c.length; j++){  
        if(c[j] == Character.toLowerCase(value[k+j])) continue;
        if(k+j == value.length - 1){
          is = false;
          break;
        }
        is = false;
        break;
      }    
      if(!is) continue;                
      k += c.length;      
      while(k < value.length){
        if(value[k] != SpecChar.END_TAG && !Character.isWhitespace(value[k])) break;
        k++;
      }  
      if(k >= value.length) return new int[0];
      idx[2] = k;
      if(value[k] == SpecChar.CLOSE_TAG) return idx;
    }
    return new int[0];
  }

}
