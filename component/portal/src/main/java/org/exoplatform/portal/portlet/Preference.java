/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.portlet;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by The eXo Platform SARL
 * Author : Mestrallet Benjamin
 *          benjmestrallet@users.sourceforge.net
 * Date: Jul 27, 2003
 * Time: 9:21:41 PM
 */
public class Preference {
  
  private String name;
  private ArrayList<String> values = new ArrayList<String>(3);
  private boolean readOnly = false;  
  
  public Preference () {
  }
    
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  
  public List getValues() { return values; }
  public void setValues(ArrayList<String> values) { this.values = values; }
  public void addValue(String value) { values.add(value) ;}

  public boolean isReadOnly() { return readOnly; }
  public void    setReadOnly(boolean b) { readOnly = b ; }
  
}