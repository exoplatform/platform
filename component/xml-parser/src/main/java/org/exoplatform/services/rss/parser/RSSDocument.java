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
