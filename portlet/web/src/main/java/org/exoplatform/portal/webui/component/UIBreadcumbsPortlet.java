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
package org.exoplatform.portal.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.portal.webui.component.UIBreadcumbsPortlet.*;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 30, 2006
 * @version:: $Id$
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  events = @EventConfig(listeners = SelectPathActionListener.class)    
)
public class UIBreadcumbsPortlet extends UIPortletApplication {
  
  public UIBreadcumbsPortlet() throws Exception {
    addChild(UIBreadcumbs.class, null, null);
  }
  
  public void loadSelectedPath() {   
    List<PageNode> nodes = Util.getUIPortal().getSelectedPaths() ;
    List<LocalPath> paths = new ArrayList<LocalPath>();
    for(PageNode node : nodes){
      if (node == null) continue;
      if(node.getPageReference() == null){
        paths.add(new LocalPath(null, node.getResolvedLabel()));
      } else {
        paths.add(new LocalPath(node.getUri(), node.getResolvedLabel()));
      }
    }
    UIBreadcumbs uiBreadCumbs = getChild(UIBreadcumbs.class);
    uiBreadCumbs.setPath(paths);
  } 
  
  public void renderChildren() throws Exception {
    loadSelectedPath();  
    super.renderChildren();
  }
  
  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortal uiPortal = Util.getUIPortal() ;
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, uri) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
  }
  
}
