/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test.crawler;

import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Dec 1, 2006  
 */
public class TestCrawlBBC extends BasicTestCase {
  
  public void testCrawlBBC() throws Exception{
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://news.bbc.co.uk/","utf-8",
        "BODY[0].TABLE[2].TBODY[0].TR[0].TD[1]",
        "BODY[0].TABLE[2].TBODY[0].TR[0].TD[1].TABLE[1].TBODY[0].TR[1].TD[0].FONT[0]");
    while(!crawl.isComplete()){
      Thread.sleep(2000);
    }
      
  }
}
