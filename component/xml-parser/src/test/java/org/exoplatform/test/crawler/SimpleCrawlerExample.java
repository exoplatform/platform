/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.test.crawler;

/**
 * Created by The eXo Platform SARL
 * Author : Lai Van Khoi
 *          laivankhoi46pm1@yahoo.com
 * Dec 1, 2006  
 */
public class SimpleCrawlerExample {
  public static void main(String[] args) throws Exception {
    CrawlerService crawl = new CrawlerService();
    crawl.startCrawl("http://www.vnexpress.net/Vietnam/Home/", "utf-8",
        "BODY[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[1].TBODY[0].TR[0].TD[2]",
        "BODY[0].TABLE[0].TBODY[0].TR[0].TD[0].TABLE[1].TBODY[0].TR[0].TD[2].TABLE[0].TBODY[0].TR[1].TD[0]");

  }
}
