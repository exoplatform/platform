/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.net.URL;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.content.model.ContentItem;
import org.exoplatform.portal.content.model.ContentNode;
import org.exoplatform.services.rss.parser.DefaultRSSChannel;
import org.exoplatform.services.rss.parser.DefaultRSSItem;
import org.exoplatform.services.rss.parser.RSSDocument;
import org.exoplatform.services.rss.parser.RSSParser;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 21, 2006  
 */
public class RSSContentPlugin extends ContentPlugin {

  private RSSParser service_;

  public RSSContentPlugin(RSSParser service){
    super();
    type ="rss";
    service_ = service;
  }

  @SuppressWarnings("unchecked")
  public PageList loadContentMeta(ContentNode node) throws Exception {
    URL uri = new URL(node.getUrl()); 
    RSSDocument<DefaultRSSChannel, RSSItem> document = 
      service_.createDocument(uri, "utf-8", DefaultRSSChannel.class, RSSItem.class);
    List<RSSItem> list = document.getItems();     
    return new ContentPageList(list);
  } 

  static public class RSSItem extends DefaultRSSItem implements ContentItem {

    public RSSItem(){
    }

    @SuppressWarnings("unused")
    public void setCreator(String creator){ }
    public String getCreator(){ return null; }

  }
  
}
