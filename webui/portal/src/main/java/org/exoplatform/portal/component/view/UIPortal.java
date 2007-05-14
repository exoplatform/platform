/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.view.lifecycle.UIPortalLifecycle;
import org.exoplatform.portal.component.view.listener.UIPageActionListener.ChangePageNodeActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.AddJSApplicationToDesktopActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.AddPortletToDesktopActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.ChangeWindowStateActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.RemoveJSApplicationToDesktopActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.portal.component.widget.UILogged.LogoutActionListener;
import org.exoplatform.portal.component.widget.UIUserWidgets.ChangeOptionActionListener;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;

/**
 * Date: Aug 11, 2003
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: UIBasicComponent.java,v 1.10 2004/09/26 02:25:46 tuan08 Exp $
 */
@ComponentConfig(
    lifecycle = UIPortalLifecycle.class,
    template = "system:/groovy/portal/webui/component/view/UIPortal.gtmpl",
    events = {
      @EventConfig(listeners = ChangePageNodeActionListener.class ),
      @EventConfig(listeners = MoveChildActionListener.class),
      @EventConfig(listeners = ChangeWindowStateActionListener.class),
      @EventConfig(listeners = ShowLoginFormActionListener.class),
      @EventConfig(listeners = LogoutActionListener.class),
      @EventConfig(listeners = ChangeOptionActionListener.class),
//      @EventConfig(listeners = AddPortletToDesktopActionListener.class),
      @EventConfig(listeners = RemoveJSApplicationToDesktopActionListener.class)
    }
)
public class UIPortal extends UIContainer { 
  
  private String owner ;
  private String locale ;
  private String [] accessGroups;
  private String skin;
  
  private List<PageNavigation> navigations ;  
  private List<PageNode> selectedPaths_;
  private PageNode selectedNode_;
  private UserPortalConfig userPortalConfig_;

  private UIComponent maximizedUIComponent ;

  public String getOwner() { return owner ; }
  public void   setOwner(String s) { owner = s  ; } 

  public String getLocale() { return locale ; }
  public void   setLocale(String s) { locale = s ; }

  public String[] getAccessGroups() { return accessGroups; }
  public void setAccessGroups(String[] accessGroups) { this.accessGroups = accessGroups; }

  public String getSkin() { return skin; }
  public void setSkin(String s ) { skin = s; }

  public List<PageNavigation> getNavigations() { return navigations ; }
  public void setNavigation(List<PageNavigation> navs) throws Exception {
    navigations = navs;
    selectedPaths_ = new ArrayList<PageNode>();
    if(navigations == null || navigations.size() < 1) return;
    PageNavigation pNav = navigations.get(0);
    if(pNav.getNodes() == null || pNav.getNodes().size() < 1) return;
    selectedNode_ = pNav.getNode(0);
    selectedPaths_.add(selectedNode_);
    UIPageBody uiPageBody = findFirstComponentOfType(UIPageBody.class);    
    if(uiPageBody == null) return;
    uiPageBody.setPageBody(selectedNode_, this);
  }

  public void setSelectedNode(PageNode node) {  selectedNode_ = node; }  
  public PageNode getSelectedNode(){ return selectedNode_; }

  public List<PageNode> getSelectedPaths() { return selectedPaths_ ; }
  public void setSelectedPaths(List<PageNode> nodes){  selectedPaths_ = nodes; }

  public UserPortalConfig getUserPortalConfig() { return userPortalConfig_; }
  void setUserPortalConfig(UserPortalConfig userPortalConfig) {
    this.userPortalConfig_ = userPortalConfig; 
  }
  
  public UIComponent getMaximizedUIComponent() { return maximizedUIComponent; }
  public void setMaximizedUIComponent(UIComponent maximizedReferenceComponent) {
    this.maximizedUIComponent = maximizedReferenceComponent;
  }

}