/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Nov 27, 2006  
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.exoplatform.services.chars.chardet.Detector;
import org.exoplatform.services.chars.chardet.ICharsetDetectionObserver;
import org.exoplatform.services.chars.chardet.PSMDetector;

public class TestFile extends TestCase{
  public static boolean found=false;
  
  public void testFile() throws  Exception {
    File file = new File("D:\\java\\projects\\v2.x\\experiments\\exo-groupware\\services\\parser\\src\\resources\\normal.html");
    FileInputStream input= new FileInputStream(file);
    BufferedInputStream buffer = new BufferedInputStream(input);
    byte[] data = new byte[buffer.available()];
    int available = -1;
    
    Detector det = new Detector(PSMDetector.ALL);
    
    //Set an observer...
    //The Notify() will be called when a matching charset is found.
    det.init(new ICharsetDetectionObserver(){
      public void notify(String charset){
        System.out.println("CHARSET === " + charset);       
      }
    });
    
    boolean done = false;
    boolean isAscii = true;
    
    while((available = buffer.read(data))>-1){
      //Khoilv'code.
      //System.out.print(data);
      if(isAscii)
        isAscii = det.isAscii(data,available);
      
      //DoIt if non-ascii and not done yet.
      if(!isAscii && !done)
        done = det.doIt(data,available,false);
    }
    
    det.dataEnd();
    
    if(isAscii){
      System.out.println("CHARSET = ASCII");
      found = true;      
    }
    
    if(!found){
      String prob[] = det.getProbableCharsets();
      for(int i=0; i<prob.length;i++){
        System.out.println("Probable Charset = " + prob[i]);
      }
    }
  }
}
