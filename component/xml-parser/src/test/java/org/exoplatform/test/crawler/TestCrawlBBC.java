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
