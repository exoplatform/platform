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
