/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 */
@ComponentConfig(template = "system:/groovy/webui/component/UIFormTableInputSet.gtmpl" )
public class UIFormTableInputSet extends UIFormInputSet {

  private String name ;
  private String [] columns ;  

  public UIFormTableInputSet() throws Exception {}
  
  public String getName() { return name ; }
  public void setName(String name) { this.name = name; }

  public String [] getColumns() { return columns; }

  public void setColumns(String [] columns) { this.columns = columns; }
  
  public void processDecode(WebuiRequestContext context) throws Exception {
    for(UIComponent child : getChildren())  {
      child.processDecode(context) ;
    }
  }

}