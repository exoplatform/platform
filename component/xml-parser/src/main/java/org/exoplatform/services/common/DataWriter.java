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
package org.exoplatform.services.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Jun 4, 2007
 */
public class DataWriter {
  
  public synchronized void save(File file, byte[] d) throws Exception {
    if( file.isDirectory()) return;  
    if (!file.exists()) file.createNewFile();          
    FileOutputStream output = new FileOutputStream(file);
    save(output, d);    
  }
  
  public synchronized void save(FileOutputStream output, byte[] data) throws Exception {
    FileChannel fchan = output.getChannel();      
    ByteBuffer buff = ByteBuffer.allocateDirect(data.length);
    buff.put(data);
    buff.rewind();
    if(fchan.isOpen()) fchan.write(buff);        
    buff.clear();        
    fchan.close();
    output.close();    
  } 
  
  public synchronized void save(File file, InputStream input) throws Exception {
    if( file.isDirectory()) return;  
    if (!file.exists()) file.createNewFile();          
    FileOutputStream output = new FileOutputStream(file);
    save(output, input);    
  }
  
  public synchronized void save(FileOutputStream output, InputStream input) throws Exception {
    ByteBuffer buff = ByteBuffer.allocateDirect(1024);
    byte [] bytes = new byte[1024];
    FileChannel fchan = output.getChannel(); 
    int read = 0;
    while ((read = input.read(bytes)) > -1){
      buff.put(bytes, 0, read);
      buff.rewind();
      if(fchan.isOpen()) fchan.write(buff);
      buff.clear(); 
    }   
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
}
