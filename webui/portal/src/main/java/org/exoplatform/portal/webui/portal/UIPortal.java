/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.application.UIWidgets.ChangeOptionActionListener;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.page.UIPageBody;
import org.exoplatform.portal.webui.page.UIPageActionListener.ChangePageNodeActionListener;
import org.exoplatform.portal.webui.portal.UIPortalActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ChangeSettingActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.RemoveJSApplicationToDesktopActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;

@ComponentConfig(
    lifecycle = UIPortalLifecycle.class,
    template = "system:/groovy/portal/webui/portal/UIPortal.gtmpl",
    events = {
      @EventConfig(listeners = ChangePageNodeActionListener.class),
      @EventConfig(listeners = MoveChildActionListener.class),
      @EventConfig(listeners = RemoveJSApplicationToDesktopActionListener.class),
      @EventConfig(listeners = UIPortalActionListener.ChangeWindowStateActionListener.class),
      @EventConfig(listeners = ShowLoginFormActionListener.class),
      @EventConfig(listeners = UIPortalActionListener.LogoutActionListener.class),
      @EventConfig(listeners = ChangeOptionActionListener.class),
      @EventConfig(listeners = ChangeSettingActionListener.class)
    }
)
public class UIPortal extends UIContainer { 
  
  private String owner ;
  private String locale ;
  private String [] accessPermissions;
  private String editPermission;
  private String skin;
  
  private List<PageNavigation> navigations ;  
  private List<PageNode> selectedPaths_;
  private PageNode selectedNode_;
  private PageNavigation selectedNavigation_;

  private Map<String, String> publicParameters_ = new HashMap<String, String>();
  
  private UIComponent maximizedUIComponent ;

  public String getOwner() { return owner ; }
  public void   setOwner(String s) { owner = s  ; } 

  public String getLocale() { return locale ; }
  public void   setLocale(String s) { locale = s ; }

  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessGroups) { this.accessPermissions = accessGroups; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }

  public String getSkin() { return skin; }
  public void setSkin(String s ) { skin = s; }
  
  public Map<String, String> getPublicParameters() { return publicParameters_; } 
  public void setPublicParameters(Map<String, String> publicParams) {
	publicParameters_ = publicParams;
  }

  public List<PageNavigation> getNavigations() { return navigations ; }
  public void setNavigation(List<PageNavigation> navs) throws Exception {
    navigations = navs;
    selectedPaths_ = new ArrayList<PageNode>();
    if(navigations == null || navigations.size() < 1) return;
    PageNavigation pNav = navigations.get(0);
    if(pNav.getNodes() == null || pNav.getNodes().size() < 1) return;
    selectedNode_ = pNav.getNodes().get(0);
    selectedPaths_.add(selectedNode_);
    UIPageBody uiPageBody = findFirstComponentOfType(UIPageBody.class);    
    if(uiPageBody == null) return;
    uiPageBody.setPageBody(selectedNode_, this);
  }

  public void setSelectedNode(PageNode node) { selectedNode_ = node; }
  
  public PageNode getSelectedNode(){ 
    if(selectedNode_ != null)return selectedNode_;
    if(getSelectedNavigation() == null || selectedNavigation_.getNodes() == null ||
       selectedNavigation_.getNodes().size()< 1) return null;
    selectedNode_ = selectedNavigation_.getNodes().get(0);
    return selectedNode_;
  }

  public List<PageNode> getSelectedPaths() { return selectedPaths_ ; }
  public void setSelectedPaths(List<PageNode> nodes){  selectedPaths_ = nodes; }
  
  public PageNavigation getSelectedNavigation() {
    if(selectedNavigation_ != null) return selectedNavigation_;
    if(getNavigations().size() < 1) return null;
    setSelectedNavigation(getNavigations().get(0));
    return getNavigations().get(0);
  }
  
  public PageNavigation getPageNavigation(String id){
    for(PageNavigation nav: navigations){
      if(nav.getId().equals(id)) return nav;
    }
    return null;
  }
  
  public void setSelectedNavigation(PageNavigation selectedNavigation) { 
    selectedNavigation_ = selectedNavigation;
  }

  public UIComponent getMaximizedUIComponent() { return maximizedUIComponent; }
  public void setMaximizedUIComponent(UIComponent maximizedReferenceComponent) {
    this.maximizedUIComponent = maximizedReferenceComponent;
  }

}