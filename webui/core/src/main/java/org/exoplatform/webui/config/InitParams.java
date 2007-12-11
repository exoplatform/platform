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