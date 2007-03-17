/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.config;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 9, 2006
 */
public class InitParams {
  
  private ArrayList<Param> params ;
  
  public Param getParam(String name) {
    if(params == null)  return null;
    for(Param param : params) {
      if(name.equals(param.getName()))  return param ;
    }
    return null;
  }
  
  public ArrayList<Param> getParams() { return  params ; }
  public void setParams(ArrayList<Param> params) { this.params = params; }
  
}