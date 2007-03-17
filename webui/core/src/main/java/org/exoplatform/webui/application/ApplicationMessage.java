/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 */
public class ApplicationMessage {
  final public static int ERROR = 0, WARNING = 1, INFO = 2;
  
  private int type_ = INFO;
  private String messageKey_ ;
  private Object[] messageArgs_ ;
  
  public ApplicationMessage(String key, Object[]  args) {
    messageKey_ = key ;
    messageArgs_ =  args ;
  }
  
  public ApplicationMessage(String key, Object[]  args, int type) {
    this(key, args) ;
    type_ = type ;
  }
  
  public String getMessageKey() { return messageKey_ ; }
  
  public Object[] getMessageAruments() { return messageArgs_ ; }

  public int getType() { return type_;  }

  public void setType(int type) {  this.type_ = type; }
  
}
