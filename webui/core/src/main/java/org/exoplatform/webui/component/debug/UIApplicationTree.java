/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.debug;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.application.RequestContext;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen My Ngoc
 *          ngoc.nguyen@exoplatform.com
 * Aug 25, 2006
 */
@ComponentConfig(
    template = "war:/groovy/webui/component/debug/UIApplicationTree.gtmpl",
    events = {
        @EventConfig(listeners = UIApplicationTree.ChangeNodeActionListener.class),
        @EventConfig(listeners = UIApplicationTree.ShowEntireActionListener.class) 
    }
)
public class UIApplicationTree extends UIContainer {
  private UIComponent selectedUIComponent_ ;
  boolean showEntireTree_ =  false ;

  public UIApplicationTree() throws Exception {
    addChild(UIComponentInfo.class, null, null);
  }


  public List<UIComponent> getFirstLevel() {
    if(selectedUIComponent_ == null) {
      RequestContext context  = RequestContext.getCurrentInstance() ;
      selectedUIComponent_ = context.getUIApplication() ;
    }
    UIComponent parent = selectedUIComponent_.getParent() ;
    if(parent == null || parent instanceof UIComponentDecorator) {
      List<UIComponent> list = new ArrayList<UIComponent>(2) ;
      list.add(selectedUIComponent_);
      return list ;
    }
    UIContainer uiContainer =  selectedUIComponent_.getParent() ;
    return uiContainer.getChildren() ;
  }

  public List<UIComponent> getSecondLevel() {
    if(selectedUIComponent_ instanceof UIContainer) {
      return ((UIContainer)selectedUIComponent_).getChildren() ;
    } else if(selectedUIComponent_ instanceof UIComponentDecorator) {
      UIComponent uichild = ((UIComponentDecorator)selectedUIComponent_).getUIComponent() ;
      if(uichild != null) {
        List<UIComponent> list = new ArrayList<UIComponent>(2) ;
        list.add(uichild);
        return list ;
      }
    } 
    return new ArrayList<UIComponent>(0) ;
  }

  public UIComponent getSelectedUIComponent() { 
    if(selectedUIComponent_ == null) {
      RequestContext context  = RequestContext.getCurrentInstance() ;
      selectedUIComponent_ = context.getUIApplication() ;
    }
    return selectedUIComponent_; 
  }
  
  public void setSelectedUIComponent(UIComponent uicomponent) throws Exception {
    selectedUIComponent_ = uicomponent;  
  }

  boolean  isShowEntireTree() { return showEntireTree_ ; }
  
  public String toXML(Object obj) throws Exception {
    if(obj == null) return "No configuration" ;
    
    StringWriter w = new StringWriter(3000) ;
    IBindingFactory bfact = BindingDirectory.getFactory( obj.getClass());
    IMarshallingContext mctx = bfact.createMarshallingContext();
    mctx.setIndent(2);   
    mctx.marshalDocument(obj, "UTF-8", null,  w) ;
    char[] buf = w.getBuffer().toString().toCharArray() ;
    StringBuilder b = new StringBuilder(3000) ;
    for(int i = 0; i < buf.length; i++) {
      switch(buf[i]) {
        case '>' : b.append("&gt;") ; break ;
        case '<' : b.append("&lt;") ; break ;
        default : b.append(buf[i]) ; break ;
      }
    }
    return b.toString();
  }
  
  static  public class ChangeNodeActionListener extends EventListener<UIApplicationTree> {
    public void execute(Event<UIApplicationTree> event) throws Exception {
      UIApplicationTree uicomp = event.getSource() ;
      String clickUIComponentId  = event.getRequestContext().getRequestParameter(OBJECTID) ;
      if(clickUIComponentId.length() > 0) {
        UIApplication uiApp = event.getRequestContext().getUIApplication() ;
        uicomp.selectedUIComponent_ = uiApp.findComponentById(clickUIComponentId) ;
      }
      UIPopupWindow uiWindow = uicomp.getAncestorOfType(UIPopupWindow.class) ;
      if(uiWindow != null) uiWindow.setShow(true) ;
      uicomp.showEntireTree_ = false ;
    }
  }
  
  static  public class ShowEntireActionListener extends EventListener<UIApplicationTree> {
    public void execute(Event<UIApplicationTree> event) throws Exception {
      UIApplicationTree uicomp = event.getSource() ; 
      uicomp.showEntireTree_ = true ;
      UIPopupWindow uiWindow = uicomp.getAncestorOfType(UIPopupWindow.class) ;
      if(uiWindow != null) uiWindow.setShow(true) ;
    }
  }
}
