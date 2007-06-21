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
