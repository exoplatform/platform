/***************************************************************************
 * Copyright 2001-2003 The  eXo Platform SARL        All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.common;

import java.io.File;
import java.io.FileFilter;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 11, 2006
 */
public class FileFilterByExt implements FileFilter {
  
  private String [] exts = new String[0];
  private boolean isFile  = false;
  
  public FileFilterByExt(){}
  
  public FileFilterByExt(boolean isFile_){
    isFile = isFile_;
  }
  
  public FileFilterByExt(String[] exts_){
    exts = new String[exts_.length];
    for(int i=0; i<exts_.length ; i++){
      if(exts_[i] != null)
       exts[i] = exts_[i].trim().toLowerCase();
    }
  }
  
  public  FileFilterByExt(String ext){
    exts = new String[]{ext.trim().toLowerCase()};
  }
  
  public boolean accept(File f) {
    if(exts.length == 0 && !isFile) return f.isDirectory();
    if(exts.length == 0 && isFile && f.isFile()) return f.getName().indexOf(".") < 0;
    if(f.isDirectory()) return false;    
    return isEndWith(f.getName());
  }
  
  private boolean isEndWith(String name){
    name  = name.trim().toLowerCase();
    for(String ele : exts)
      if(ele != null && name.endsWith(ele)) return true;
    return false;
  }
}
