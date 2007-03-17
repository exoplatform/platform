/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.exception;

import org.exoplatform.webui.application.ApplicationMessage;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 7, 2006
 */

@SuppressWarnings("serial")
public class MessageException extends Exception {
  
  private ApplicationMessage message ;
  
  public MessageException(ApplicationMessage message) {
    this.message = message ;
  }
  
  public ApplicationMessage getDetailMessage() { return message ; }
  
}
