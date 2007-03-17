/*
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 16 juin 2004
 */
@SuppressWarnings("serial")
public class PortletRegisteryException extends Exception {

  final static public int UNKNOWN_ERROR = 0 ;
  final static public int PORTLET_CATEGORY_NOT_FOUND = 1 ;
  final static public int PORTLET_NOT_FOUND = 2 ;
  final static public int PORTLET_ROLE_NOT_FOUND = 3 ;
  
  private int errorCode_ ; 

  public PortletRegisteryException(String s, int errorCode) {
    super(s) ;
    errorCode_ = errorCode ;
  }

  public int getErrorCode() { return errorCode_ ; }

}
