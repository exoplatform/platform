/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.page.UIPageActionListener.AddApplicationActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.DeleteWidgetActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.EditPageActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.RemoveChildActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.SaveWidgetPropertiesActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.SaveWindowPropertiesActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
/**
 * May 19, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/page/UIPage.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class)
      }
  ), //save desktop setting
  @ComponentConfig(
      id = "Desktop",
      lifecycle = UIPageLifecycle.class,
      template = "system:/groovy/portal/webui/page/UIPageDesktop.gtmpl",
      events = {
        @EventConfig(listeners = EditPageActionListener.class ),
        @EventConfig(listeners = MoveChildActionListener.class),
        @EventConfig(listeners = RemoveChildActionListener.class),
        @EventConfig(listeners = ShowLoginFormActionListener.class),
        @EventConfig(listeners = DeleteWidgetActionListener.class),
        @EventConfig(listeners = AddApplicationActionListener.class),
        @EventConfig(listeners = SaveWidgetPropertiesActionListener.class),
        @EventConfig(listeners = SaveWindowPropertiesActionListener.class)
      }
  )
})
public class UIPage extends UIContainer {
  
  private String pageId;
  private String ownerId ;
  private String ownerType ;  
  
  private String [] accessPermissions;
  private String editPermission;
  
  private boolean showMaxWindow = false ;
  private UIPortlet maximizedUIPortlet;
  
  public String getOwnerId() { return ownerId ; }
  public void   setOwnerId(String s) { ownerId = s ; } 
  
  public boolean isShowMaxWindow() { return showMaxWindow; }
  public void setShowMaxWindow(Boolean showMaxWindow) { this.showMaxWindow = showMaxWindow; }
  
  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessGroups) { this.accessPermissions = accessGroups; }
  
  public String getEditPermission() { return editPermission; }
  public void setEditPermission(String editPermission) { this.editPermission = editPermission; }
  
  public String getPageId() { return pageId; }
  public void setPageId(String id) { pageId = id; }
  
  public UIPortlet getMaximizedUIPortlet() { return maximizedUIPortlet;  }
  
  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
  public void setMaximizedUIPortlet(UIPortlet maximizedUIPortlet) {
    this.maximizedUIPortlet = maximizedUIPortlet;
  }
}