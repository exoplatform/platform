/***************************************************************************
 * Copyright 2003-2007 by eXoPlatform - All rights reserved.                *    
 **************************************************************************/
package org.exoplatform.services.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 4, 2007
 */
public class DataReader {
  
  public synchronized byte[] load(String path) throws Exception {
    File file = new File(path);       
    return load( file );
  }
  
  public synchronized byte[] load(File file) throws Exception {
    if ( !file.exists() || !file.isFile()) return new byte[0];
    FileInputStream input = new FileInputStream(file);
    return load(input, file.length());    
  }
  
  public synchronized byte[] load(FileInputStream input, long fsize) throws Exception {
    FileChannel fchan = input.getChannel();
    ByteBuffer buff = ByteBuffer.allocate((int)fsize);      
    fchan.read(buff);
    buff.rewind();      
    byte[] data = buff.array();      
    buff.clear();      
    fchan.close();        
    input.close();       
    return data;
  }
  
  public ByteArrayOutputStream loadInputStream(InputStream input) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] data  = new byte[1024];      
    int available = -1;
    while( (available = input.read(data)) > -1){
      output.write(data, 0, available);
    }   
    return output;
  }
 
}
