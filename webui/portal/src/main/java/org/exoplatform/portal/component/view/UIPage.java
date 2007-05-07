/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.portal.component.view.lifecycle.UIPageLifecycle;
import org.exoplatform.portal.component.view.listener.UIPageActionListener.EditPageActionListener;
import org.exoplatform.portal.component.view.listener.UIPageActionListener.RemoveChildActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 19, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/component/view/UIPage.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class)
      }
  ),
  @ComponentConfig(
      id = "Desktop",
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/component/view/UIPageDesktop.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class),
        @EventConfig(listeners = RemoveChildActionListener.class),
        @EventConfig(listeners = ShowLoginFormActionListener.class)
      }
  )
})
public class UIPage extends UIContainer {
  
  private String ownerId ;
  private String ownerType ;  
  private String [] accessGroups;
  private boolean showMaxWindow = false ;
  private UIPortlet maximizedUIPortlet;
  
  public String getOwnerId() { return ownerId ; }
  public void   setOwnerId(String s) { ownerId = s ; } 
  
  public boolean isShowMaxWindow() { return showMaxWindow; }
  public void setShowMaxWindow(Boolean showMaxWindow) { this.showMaxWindow = showMaxWindow; }
  
  public String[] getAccessGroups() { return accessGroups; }
  public void setAccessGroups(String[] accessGroups) { this.accessGroups = accessGroups; }
  
  public String getPageId() { return ownerId + ":/" + name_ ; }
  
  public UIPortlet getMaximizedUIPortlet() { return maximizedUIPortlet;  }
  
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
  public void setMaximizedUIPortlet(UIPortlet maximizedUIPortlet) {
    this.maximizedUIPortlet = maximizedUIPortlet;
  }
  
  public String getPortletIcon(ResourceBundle res,  UIPortlet uiPortlet) {
    try {
      return res.getString("UIPageDesktop.img." + uiPortlet.getExoWindowID().getPortletName()) ;
    } catch (MissingResourceException ex) {
      return res.getString("UIPageDesktop.img.DefaultPortlet") ;
    }
  }
  
}