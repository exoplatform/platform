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
package org.exoplatform.portal.webui.portal;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.webui.core.UIContainer;
/**
 * May 19, 2006
 */
public class UIPortalComponent extends UIContainer {
  
  protected String template_ ;
  protected String name_;
  protected String factoryId;
//  protected String decorator_ ;
  protected String width_ ;
  protected String height_ ;
  private String title_; 
  
  private transient boolean modifiable_ ;
  
  private String[] accessPermissions = { UserACL.EVERYONE } ;
  
  private boolean showEditControl_ = false;
  protected int mode_   = COMPONENT_VIEW_MODE  ;
  
  final static public  int COMPONENT_VIEW_MODE = 1 ;
  final static public int COMPONENT_EDIT_MODE = 2 ;
  
//  public String getDecorator() { return decorator_; }
//  public void   setDecorator(String decorator) { decorator_ = decorator ; }
  
  public String getTemplate() {
    if(template_ == null || template_.length() == 0)  return getComponentConfig().getTemplate() ;
    return template_ ; 
  }
  public void   setTemplate(String s) {  template_ = s ; }
  
  public String[] getAccessPermissions() { return accessPermissions; }
  public void setAccessPermissions(String[] accessPermissions) {
    this.accessPermissions = accessPermissions;
  }

  public boolean hasPermission() {
    ExoContainer exoContainer = ExoContainerContext.getCurrentContainer() ;
    UserACL acl = (UserACL) exoContainer.getComponentInstanceOfType(UserACL.class) ;
    for(String per : accessPermissions) {
      if(acl.hasPermission(per)) {
        return true ;
      }
    }
    return false ;
  }
  
  public String getWidth() { return width_ ; }
  public void   setWidth(String s) { width_ = s ;}
  
  public String getHeight() { return height_ ; }
  public void   setHeight(String s) { height_ = s ;}
  
  public boolean isModifiable() { return modifiable_ ; }
  public void    setModifiable(boolean b) { modifiable_ = b ; }
  
  public String getTitle() { return title_ ; }
  public void   setTitle(String s) { title_ = s ; } 
  
  public int getMode() { return mode_ ; }
  public void setMode(int mode) {  mode_ = mode ; }
  public boolean isEditMode(){ return mode_ == COMPONENT_EDIT_MODE; }
  
  public boolean isShowEditControl(){ return showEditControl_; }
  public void setShowEditControl(boolean show) { showEditControl_ = show; }
  
  public String getName() { return name_; }
  public void setName(String name) { this.name_ = name; }
  
  public String getFactoryId() { return factoryId; }
  public void setFactoryId(String factoryId) { this.factoryId = factoryId; }

}