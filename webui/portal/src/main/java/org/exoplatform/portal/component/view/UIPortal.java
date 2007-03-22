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
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.ShowLoginFormActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.MaximizeActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.MinimizeActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalActionListener.RemoveJSApplicationToDesktopActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.component.widget.UILogged.LogoutActionListener;
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
      @EventConfig(listeners = MaximizeActionListener.class),
      @EventConfig(listeners = MinimizeActionListener.class),
      @EventConfig(listeners = ShowLoginFormActionListener.class),
      @EventConfig(listeners = LogoutActionListener.class),
      @EventConfig(listeners = AddPortletToDesktopActionListener.class),
      @EventConfig(listeners = AddJSApplicationToDesktopActionListener.class),
      @EventConfig(listeners = RemoveJSApplicationToDesktopActionListener.class)
    }
)
public class UIPortal extends UIContainer { 
  private String owner ;
  private String locale ;
  private String viewPermission ;
  private String editPermission  ; 
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

  public String getViewPermission() { return viewPermission ; }
  public void   setViewPermission(String s) { viewPermission = s ; }

  public String getEditPermission() { return editPermission ; }
  public void   setEditPermission(String s) { editPermission = s ; }

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
    uiPageBody.setPageBody(selectedNode_, this);
  }

  public void setSelectedNode(PageNode node) {  selectedNode_ = node; }  
  public PageNode getSelectedNode(){ return selectedNode_; }

  public List<PageNode> getSelectedPaths() { return selectedPaths_ ; }
  public void setSelectedPaths(List<PageNode> nodes){  selectedPaths_ = nodes; }

  public UIPortlet getFirstUIPortlet(){
    return (UIPortlet)Util.findUIComponent(this, UIPortlet.class, UIPage.class);
  }

  public List<UIComponent> getUIPortlets(){
    List<UIComponent> list  = new ArrayList<UIComponent>();
    Util.findUIComponents(this, list, UIPortlet.class, UIPage.class);
    return list;
  }

  public UIContainer getFirstUIContainer(){
    return (UIContainer)Util.findUIComponent(this, UIContainer.class, UIPage.class);
  }

  public List<UIComponent> getUIContainers(){
    List<UIComponent> list  = new ArrayList<UIComponent>();
    Util.findUIComponents(this, list, UIContainer.class, UIPage.class);
    return list;
  }

  public UserPortalConfig getUserPortalConfig() { return userPortalConfig_; }
  void setUserPortalConfig(UserPortalConfig userPortalConfig) {
    this.userPortalConfig_ = userPortalConfig; 
  }
  
  public UIComponent getMaximizedUIComponent() { return maximizedUIComponent; }
  public void setMaximizedUIComponent(UIComponent maximizedReferenceComponent) {
    this.maximizedUIComponent = maximizedReferenceComponent;
  }

}