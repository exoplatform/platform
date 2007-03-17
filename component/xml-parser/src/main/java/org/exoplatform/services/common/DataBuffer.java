package org.exoplatform.services.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author thuannd
 *
 * TODO To change the template for this generated type comment go to
 */
public class DataBuffer {
  
  public synchronized byte[] load( String path) throws Exception {
    File file = new File( path);       
    return load( file );
  }
  
  public synchronized byte[] load(File file) throws Exception {
    if ( !file.exists() || !file.isFile()) return new byte[0];
    FileInputStream input = new FileInputStream(file);
    return load(input);    
  }
  
  public synchronized byte[] load(FileInputStream input) throws Exception {
    FileChannel fchan = input.getChannel();
    long fsize = fchan.size();       
    ByteBuffer buff = ByteBuffer.allocate((int)fsize);        
    fchan.read(buff);
    buff.rewind();      
    byte[] data = buff.array();      
    buff.clear();      
    fchan.close();        
    input.close();       
    return data;
  }
  
  public synchronized File save(String path, byte[] d ) throws Exception {
    File file = new File( path);
    save(file, d);
    return file;
  }
  
  public synchronized void save(File file, byte[] d ) throws Exception {
    if( file.isDirectory()) return;  
    if (!file.exists()) file.createNewFile();          
    FileOutputStream output = new FileOutputStream(file);
    save(output, d);    
  }
  
  public synchronized void save(FileOutputStream output, byte[] d) throws Exception {
    FileChannel fchan = output.getChannel();      
    ByteBuffer buff = ByteBuffer.allocateDirect(d.length);        
    for(int i=0; i<d.length; i++) buff.put(d[i]);    
    buff.rewind();
    fchan.write(buff);        
    buff.clear();        
    fchan.close();
    output.close();    
  } 
  
  public void copy(String from, String to) throws Exception {
    File src = new File(from);
    File des = new File(to);
    copy(src, des);
  }
  
  public void copy(File from, File to) throws Exception {
    FileChannel srcChannel = new FileInputStream(from).getChannel();
    FileChannel desChannel = new FileOutputStream(to).getChannel();
    srcChannel.transferTo(0, srcChannel.size(), desChannel);
    srcChannel.close();
    desChannel.close(); 
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