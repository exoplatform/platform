/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rss.parser;

import java.util.List;

/**
 * Created by The eXo Platform SARL        .
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 17, 2006
 */
public class RSSDocument <T extends IRSSChannel, E extends IRSSItem> {
  
  private T channel;
  
  private List<E> items;
  
  public RSSDocument(T channel, List<E> items){
    this.channel = channel;
    this.items = items;
  }
  
  public T getChannel(){   return channel;  }  
  public void setChannel(T channel) { this.channel = channel; }
  
  public List<E> getItems(){ return items; }  
  public void setItems(List<E> list){ items = list; }
  
  public E getItem(int idx){ return items.get(idx); }  
  public void removeItem(int idx){ items.remove(idx); }
  
}
