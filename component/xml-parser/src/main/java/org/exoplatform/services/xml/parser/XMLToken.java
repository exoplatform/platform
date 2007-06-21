/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.xml.parser;

import org.exoplatform.services.chars.CharsUtil;
import org.exoplatform.services.chars.SpecChar;
import org.exoplatform.services.html.refs.RefsDecoder;
import org.exoplatform.services.token.TypeToken;
import org.exoplatform.services.token.TokenParser.Factory;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Sep 17, 2006
 */
@SuppressWarnings("serial")
public class XMLToken extends Factory<XMLNode> {
  
  private XMLNode xmlType;
  private RefsDecoder refs ;
  
  public XMLToken (){
    refs = new RefsDecoder();
  }
  
  public int create(char [] data, int start, int end, int type){         
    if(start >= end) return end;
    if(start > data.length) return data.length;    
    char[] value = CharsUtil.cutAndTrim(data, start, Math.min(end, data.length)); 
    if(value.length < 1) return end;    
    if(type != TypeToken.TAG){      
      value = refs.decode(value);
      push(new XMLNode(value, null, type));
      return end;
    }            
    if(value[0] == SpecChar.END_TAG){
      if(value.length <= 1) return end;
      value  = CharsUtil.cutAndTrim(value, 1, value.length);
      push(new XMLNode(value, new String(value), TypeToken.CLOSE));
      return end;
    }
    String nameValue = new String(CharsUtil.cutBySpace(value, 0));
    if(value[value.length-1] == SpecChar.QUESTION_MASK &&
        value[0] == SpecChar.QUESTION_MASK ) {
      xmlType = new XMLNode(value, nameValue, TypeToken.SINGLE);
    }else{
      push(new XMLNode(value, nameValue, isSingleTag(value)));
    }
    return end;
  }
  
  int isSingleTag(char[] value){
    if(value[value.length - 1] == SpecChar.END_TAG) return TypeToken.SINGLE;
    else if(value[0] == SpecChar.PUNCTUATION_MASK) return TypeToken.SINGLE;
    return TypeToken.TAG;
  }

  public XMLNode getXmlType() {
    return xmlType;
  }

}
