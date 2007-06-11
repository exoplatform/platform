/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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
 */
@ComponentConfig( lifecycle = UIComponentDecorator.UIComponentDecoratorLifecycle.class )
public class UIComponentDecorator extends UIComponent {
  
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