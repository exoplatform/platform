/***************************************************************************
 * Copyright 2001-2003 The  eXo Platform SARL        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.html.refs;

/**
 * Created by  eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 8, 2006
 */
public class RefsEncoder {
  
  private boolean hexadecimal = false;
  
  public RefsEncoder(boolean hexadecimalV){    
    hexadecimal = hexadecimalV;
  }
  
  public char[] encode (char[] chars) {
    CharRefs charRefs = EncodeService.ENCODE_CHARS_REF.getRef();
    if(!charRefs.isSorted()) charRefs.sort(EncodeService.comparator);
    CharsSequence refValue = new CharsSequence(chars.length * 6);
    char c;
    CharRef ref;
    int i = 0;
    while (i < chars.length){
      c = chars[i];
      ref = charRefs.searchByValue(c, EncodeService.comparator);
      if (ref != null){
        refValue.append ('&');
        refValue.append (ref.getName());
        refValue.append (';');
      }else if (!(c < 0x007F)){
        refValue.append ("&#");
        if (hexadecimal) {
          refValue.append ('x');
          refValue.append (Integer.toHexString (c));
        }else
          refValue.append(String.valueOf((int)c));
        refValue.append (';');
      }else
        refValue.append (String.valueOf((int)c));
      i++;
    }
    return refValue.getValues();
  }

}
