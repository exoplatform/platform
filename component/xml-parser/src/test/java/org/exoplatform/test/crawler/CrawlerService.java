/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test.crawler;

import java.net.URL;
import java.util.List;

import org.exoplatform.services.parser.html.HTMLDocument;
import org.exoplatform.services.parser.html.parser.HTMLParser;
import org.exoplatform.services.parser.html.path.NodePath;
import org.exoplatform.services.parser.html.path.NodePathParser;
import org.exoplatform.services.parser.html.path.NodePathUtil;
import org.exoplatform.services.parser.html.util.HyperLinkUtil;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Dec 1, 2006  
 */
public class CrawlerService extends Thread{
  private SingleCrawlThread[] childrenThread;
  private boolean complete=false;
  
  private String url_;
  private String charset_="utf-8";
  private NodePath homePath_, childPath_; //Node.
  
  private List<String> links; //holds all links for downloading.
  private int idx=0;          //the order of the Thread (of URL-Link) is being downloaded.
  
  private HyperLinkUtil linkUtil = new HyperLinkUtil();
  
  //-------------------------
  public CrawlerService(){
    this.childrenThread = new SingleCrawlThread[3];
    for(int i=0;i<this.childrenThread.length;i++){
      this.childrenThread[i] = new SingleCrawlThread();
    }
    new Thread(this).start(); //Note.
  }
  //--------------------------
  public void startCrawl(String url, String charset, 
                         String homePath, String childPath)throws Exception {
    this.url_=url;
    this.charset_=charset;
    this.homePath_= NodePathParser.toPath(homePath);
    this.childPath_=NodePathParser.toPath(childPath);
    this.childrenThread[0].startDownload(this.url_);
  }
  //--------------------------
  public void run(){
    while(true){        
      try{
        this.processHome();
        this.processLink();
        Thread.sleep(2000);
      }
      catch(Exception exp){exp.printStackTrace();}
    }
  }
  //-------------------------
  private void processHome()throws Exception {
    if(this.links!=null && this.links.size()>0) return;  //don't hold any link for downloading.
    
    if(!this.childrenThread[0].isComplete())return;      //Downloading hasn't still completed.  
    if(this.childrenThread==null)return;
    if(this.childrenThread[0]==null)return;
    if(this.childrenThread[0].getData()==null)return;
    
    byte[] data = this.childrenThread[0].getData().toByteArray();
    //The whole HTMLdocument.
    HTMLDocument document = HTMLParser.createDocument(data,this.charset_);
    //The(an) only part of HTMLDocument with 'homePath' Node (NodePath).
    document = NodePathUtil.create(document.getRoot(),new NodePath[]{this.homePath_});
    
    //Create the full url-link for all links to be downloaded.
    this.linkUtil.createFullNormalLink(document.getRoot(),new URL(this.url_));
    //And get all these url-links.
    this.links=this.linkUtil.getSiteLink(document.getRoot());
    this.idx=0;
  }
  //--------------------------
  private void processLink() throws Exception {
    if(this.links==null||this.links.size()<1)return; //Not download.
    if(this.idx>=this.links.size())return;           //Downloading is already completed.
    
    //Check each Thread in the childrenThread.
    for(int i=0;i<this.childrenThread.length;i++){
      if(!this.childrenThread[i].isComplete())continue; //Continue when the Thread is still not completed.
      childrenThread[i].saveData();//Save data has downloaded when the Thread is completed.
      if(this.idx>=this.links.size()){
        System.out.println("Download completed!");
        this.complete=true;
        return;
      }
      this.childrenThread[i].startDownload(this.links.get(idx),this.idx,this.childPath_,this.charset_);
      this.idx++;
    }
  }
  //----------------------------
  public boolean isComplete(){
    return this.complete;
  }
}
