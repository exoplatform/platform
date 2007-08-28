/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
  
  //TODO: Tung.Pham modified
  //transient private  UIPortal uiPortal ; 
  
  public UIBreadcumbsPortlet() throws Exception {
    addChild(UIBreadcumbs.class, null, null);
  }
  
  public void loadSelectedPath() {   
    //List<PageNode> nodes = getPortal().getSelectedPaths() ;
    List<PageNode> nodes = Util.getUIPortal().getSelectedPaths() ;
    List<LocalPath> paths = new ArrayList<LocalPath>();
    for(PageNode node : nodes){
      paths.add(new LocalPath(node.getUri(), node.getLabel()));
    }
    UIBreadcumbs uiBreadCumbs = getChild(UIBreadcumbs.class);
    uiBreadCumbs.setPath(paths);
  } 
  
  public void renderChildren() throws Exception {
    loadSelectedPath();  
    super.renderChildren();
  }
 
//  public UIPortal getPortal(){
//    if(uiPortal == null) uiPortal = Util.getUIPortal() ;
//    return uiPortal;
//  }
  
  static  public class SelectPathActionListener extends EventListener<UIBreadcumbs> {
    public void execute(Event<UIBreadcumbs> event) throws Exception {
      //UIBreadcumbs breadcumbs = event.getSource() ;
      //UIBreadcumbsPortlet breadcumbsPortlet = (UIBreadcumbsPortlet) breadcumbs.getParent() ;
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      //UIPortal uiPortal = breadcumbsPortlet.getPortal() ;
      UIPortal uiPortal = Util.getUIPortal() ;
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, null, uri) ;
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;
    }
  }
  
}
