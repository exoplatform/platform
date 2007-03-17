/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.io.Writer;

import org.exoplatform.services.organization.Group;
import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 4, 2006  
 */
@ComponentConfig()
//TODO remove this component
public class UIGroupSharedInfo extends UIContainer {
  
  public UIGroupSharedInfo() throws Exception {
    addChild(UISharedPortalForm.class, null, null);
    addChild(UISharedNavigationForm.class, null, null);
  }
  
  public void processRender(RequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.write("<div class=\"UIGroupSharedInfo\" id=\"UIGroupSharedInfo\">");
    renderChildren();
    w.write("</div>");
  }
  
  public void setGroup(Group group) throws Exception {
    UISharedPortalForm uiSharedPortalForm = getChild(UISharedPortalForm.class);
    uiSharedPortalForm.setValues(group != null ? group.getId() : null);
    
    UISharedNavigationForm uiSharedNavigationForm = getChild(UISharedNavigationForm.class);
    uiSharedNavigationForm.setValues(group != null ? group.getId() : null);
  }
  

}
