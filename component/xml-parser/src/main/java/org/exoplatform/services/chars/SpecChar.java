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

package org.exoplatform.services.chars;

/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public class SpecChar {
  
  public static char s = ' ';
  public static char t = '\t';
  public static char n = '\n';
  public static char b = '\b';
  public static char f = '\f';
  public static char r = '\r';
  
  public static char END_TAG = '/', OPEN_TAG = '<', CLOSE_TAG = '>' ; 
  
  public static char  HYPHEN = '-', QUESTION_MASK = '?',  PUNCTUATION_MASK='!';
    
  public static int findWhiteSpace(String value, int start){    
    for(int i=start; i<value.length(); i++){  
      if(Character.isWhitespace(value.charAt(i))) return i;      
    }       
    return value.length();
  }    
}
