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

public class TextVerifier {
  
  public boolean startIn (String line, String[] pattern){
    for( String ele : pattern)
      if( line.startsWith( ele)) return true;
    return false;
  }
  
  public boolean endIn (String line, String[] pattern){
    for( String ele : pattern)
      if(line.endsWith( ele)) return true;
    return false;
  }
  
  public boolean existIn (String line, String[] pattern){
    for( String ele : pattern)
      if(line.indexOf( ele) > -1) return true;
    return false;
  }
  
  public boolean existAll(String line, String[] pattern){
    for( String ele : pattern)
      if( !(line.indexOf( ele) > -1)) return false;
    return true;
  }
  
  public boolean equalsIn (String line, String[] pattern){
    for( String ele : pattern)
      if(line.equals( ele)) return true;
    return false;
  }
  
  public  boolean startOrEnd(String line , String[] start, String[] end){
    return startIn(line, start)|| endIn(line, end);
  }
  
  public boolean startAndEnd(String line , String[] start, String[] end){
    return startIn(line, start) && endIn(line, end);
  }	
  
  public boolean startOrEndOrExist(String line , String[] start, String[] end, String[] exist){
    return startIn(line, start)|| endIn(line, end) || existIn(line, exist);
  }
  
  public boolean startAndEndAndExist(String line , String[] start, String[] end, String[] exist){
    return startIn(line, start)&& endIn(line, end) && existIn(line, exist);
  }
  
  public boolean startAndEndAndExistAll(String line , String[] start, String[] end, String[] exist){
    return startIn(line, start)&& endIn(line, end) && existAll(line, exist);
  }

}
