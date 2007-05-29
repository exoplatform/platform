/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.Group;
import org.exoplatform.webui.component.UITabPane;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:08:51 AM 
 */
@ComponentConfig( template = "system:/groovy/webui/component/UITabPane.gtmpl" )
public class UIGroupInfo extends UITabPane {
  
  public UIGroupInfo() throws Exception {
    addChild(UIUserInGroup.class, null, null) ;
  }
  
  public void setGroup(Group group) throws Exception {
    getChild(UIUserInGroup.class).setValues(group);
    setRenderedChild(UIUserInGroup.class);
  }
  
}