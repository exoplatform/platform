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
//    addChild(UISharedPortalForm.class, null, null).setRendered(false) ;
//    addChild(UISharedNavigationForm.class, null, null).setRendered(false) ;
//    addChild(UIGroupSharedInfo.class, null, null).setRendered(false) ;    
  }
  
  public void setGroup(Group group) throws Exception {
    getChild(UIUserInGroup.class).setValues(group);
    setRenderedChild(UIUserInGroup.class);
//    UISharedPortalForm uiSharedPortalForm = getChild(UISharedPortalForm.class);
//    uiSharedPortalForm.setValues(group != null ? group.getId() : null);
//    
//    UISharedNavigationForm uiSharedNavigationForm = getChild(UISharedNavigationForm.class);
//    uiSharedNavigationForm.setValues(group != null ? group.getId() : null);
  }
  
}