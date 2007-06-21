/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rss.parser;


/**
 * Created by The eXo Platform SARL        .
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Mar 13, 2006
 */
public class DefaultRSSChannel extends DefaultRSSItem implements IRSSChannel {
  
  private String  generator = "";    
  
  public DefaultRSSChannel(){}
  
  public void setGenerator(String generator){ this.generator = generator; }
  
  public String getGenerator(){ return generator; }

}
