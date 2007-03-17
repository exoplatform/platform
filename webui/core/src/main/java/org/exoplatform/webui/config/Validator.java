/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 4, 2006
 */
public class Validator {
	
  private String type;
  private InitParams  initParams ;
 
  public InitParams getInitParams() {  return initParams; }
  public void setInitParams(InitParams initParams) { this.initParams = initParams; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
}
