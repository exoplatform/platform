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
package org.exoplatform.webui.core;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.Lifecycle;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * May 7, 2006
 * 
 * The decorator of a component 
 */
@ComponentConfig( lifecycle = UIComponentDecorator.UIComponentDecoratorLifecycle.class )
public class UIComponentDecorator extends UIComponent {
  /**
   * The component being decorated
   */
  protected  UIComponent uicomponent_ ;
  
  public UIComponent getUIComponent()  { return uicomponent_ ; }
  
  public void setUIComponent(UIComponent uicomponent) { 
    if(uicomponent_ != null) uicomponent_.setRendered(false);
    uicomponent_ =  uicomponent ;
    if(uicomponent_ == null)  return ;
    uicomponent_.setParent(this);
  }  
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T findComponentById(String id) {
    if(getId().equals(id)) return (T)this ;
    if(uicomponent_ == null) return null;
    return (T)uicomponent_.findComponentById(id);
  }
  
  public <T extends UIComponent> T findFirstComponentOfType(Class<T> type) {
    if (type.isInstance(this)) return type.cast(this);  
    if(uicomponent_ == null) return null;
    return uicomponent_.findFirstComponentOfType(type);
  }
  
  public <T> void findComponentOfType(List<T>list, Class<T> type) {
    if (type.isInstance(this)) list.add(type.cast(this));
    if(uicomponent_ == null) return ;
    uicomponent_.findComponentOfType(list, type);
  }
  
  public void renderChildren() throws Exception {
    if(uicomponent_ == null)  return ;
    uicomponent_.processRender((WebuiRequestContext)WebuiRequestContext.getCurrentInstance()) ;
  }
  
  static public class UIComponentDecoratorLifecycle extends Lifecycle {
    @SuppressWarnings("unused")
    public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
      UIComponentDecorator uiContainer = (UIComponentDecorator) uicomponent;      
      if(uiContainer.uicomponent_ != null) {
        uiContainer.uicomponent_.processRender(context) ;
      }
    }
  }
} 