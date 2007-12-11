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
package org.exoplatform.services.html.refs;

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
