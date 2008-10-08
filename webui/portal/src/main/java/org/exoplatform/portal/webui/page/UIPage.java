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
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.MoveChildActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
/**
 * May 19, 2006
 */
@ComponentConfig(
		lifecycle = UIPageLifecycle.class,
		template = "system:/groovy/portal/webui/page/UIPage.gtmpl",
		events = {
			@EventConfig(listeners = MoveChildActionListener.class)
		}
)
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