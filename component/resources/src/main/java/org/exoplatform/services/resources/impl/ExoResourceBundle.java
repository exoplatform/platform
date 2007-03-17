/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.resources.impl;

import java.io.Serializable;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

/**
 * May 7, 2004
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: ExoResourceBundle.java 9439 2006-10-12 03:28:53Z thuannd $
 **/
@SuppressWarnings("serial")
public class ExoResourceBundle extends ListResourceBundle implements Serializable {

  private Object[][] contents ;

  public ExoResourceBundle(String data) {
    String [] tokens = data.split("\\n");
    int i = 0;
    String [][] properties = new String[tokens.length][2];
    for(String token : tokens){
      int idx = token.indexOf('=');
      if(idx < 0 || idx >= token.length() - 1) continue;
      properties[i][0] = token.substring(0, idx);
      properties[i][1] = token.substring(idx+1, token.length());;
      i++;
    }
    contents = new Object[i][2];
    System.arraycopy(properties, 0, contents, 0, i);
  }
  
  public ExoResourceBundle(String data, ResourceBundle parent) {
    this(data) ; 
    setParent(parent);
  }
  
  public Object[][] getContents() {
    return contents;
  }
}