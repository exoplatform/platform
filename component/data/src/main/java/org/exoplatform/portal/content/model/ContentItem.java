/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content.model;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 21, 2006  
 */
public interface ContentItem {
  
  public String getCreator();
  public void setCreator(String creator) ;

  public String getDesc() ;
  public void setDesc(String desciption) ;

  public String getTime() ;
  public void setTime(String time);
  
  public String getImage();
  public void setImage(String image);

  public String getTitle();
  public void setTitle(String title) ;

  public String getLink();
  public void setLink(String url) ;
  
}
