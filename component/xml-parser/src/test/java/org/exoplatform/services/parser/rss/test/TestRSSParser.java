/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.rss.test;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.parser.rss.DefaultRSSChannel;
import org.exoplatform.services.parser.rss.DefaultRSSItem;
import org.exoplatform.services.parser.rss.RSSDocument;
import org.exoplatform.services.parser.rss.RSSParser;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Jul 20, 2006  
 */

public class TestRSSParser extends BasicTestCase {
  
  private RSSParser parser_;
  
  public TestRSSParser(String name){
    super(name);
  }
  
  public void setUp() throws Exception{
    PortalContainer manager  = PortalContainer.getInstance();      
    parser_ = (RSSParser) manager.getComponentInstanceOfType(RSSParser.class) ;
  }
  
  public void tearDown() throws Exception{
    
  }
  
  public void testAtom30() throws Exception{
    RSSDocument<DefaultRSSChannel, DefaultRSSItem> document = createRSSDocument("atom03.xml");   
    
    List<DefaultRSSItem> items = document.getItems();   
    assertEquals(2, items.size());   
    assertEquals(items.get(0).getTitle(),"Wanted: Used Acme Bio Truck");
    assertEquals(items.get(0).getLink(),"http://provider-website.com/item1-info-page.html");
    assertEquals(items.get(0).getTime(),"2003-12-13T08:29:29-04:00");    
  }  
  
  public void testURLRss() throws Exception {
    URL url = new URL("http://itredux.com/blog/feed/atom/");
    RSSDocument<DefaultRSSChannel, DefaultRSSItem> document =  parser_.createDocument(url.toURI(), "utf-8");
    List<DefaultRSSItem> items = document.getItems();   
    for(DefaultRSSItem item  : items){
      System.out.println(item.getTitle());
    }
  }

  public RSSDocument<DefaultRSSChannel, DefaultRSSItem> createRSSDocument(String path) throws Exception {      
    File uri = new File("src"+File.separatorChar+"resources"+File.separatorChar+path); 
    return parser_.createDocument(uri, "utf-8");    
  }
  
}