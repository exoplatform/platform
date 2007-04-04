/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.lang.reflect.Method;
import java.util.List;

import org.exoplatform.util.ReflectionUtil;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 7, 2006
 */
@ComponentConfig(
  template = "system:/groovy/webui/component/UITree.gtmpl" , 
  events = @EventConfig(listeners = UITree.ChangeNodeActionListener.class)
)
public class UITree extends UIComponent {
  
  private String expandIcon = "ExpandIcon";
  private String colapseIcon = "ColapseIcon";
  
  private String selectedIcon ;
  private String icon;
  
  private String beanIdField_ ;
  private String beanLabelField_ ;
  
  private List<?> sibbling;
  private List<?> children;
  private Object selected;
  private Object parentSelected ;
  
  private UIRightClickPopupMenu uiPopupMenu_;
  
  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS) ;
  }
  
  public void setBeanIdField(String beanIdField_) {  this.beanIdField_ = beanIdField_; }
  public void setBeanLabelField(String beanLabelField_) { this.beanLabelField_ = beanLabelField_; }


  public Object getId(Object object) throws Exception {    
    return getFieldValue(object, beanIdField_);
  } 
  
  public String getActionLink() throws Exception {
    if(selected == null) return "#";
    if(parentSelected == null) return event("ChangeNode");
    return event("ChangeNode", (String)getId(parentSelected));
  }
  
  private boolean isSelected(Object obj){
    if(selected == null) return false;    
    return obj == selected;
  }

  public String getColapseIcon() { return colapseIcon; }
  public void setCollapseIcon(String colapseIcon) { this.colapseIcon = colapseIcon; }

  public String getExpandIcon() { return expandIcon;  }
  public void setExpandIcon(String expandIcon) { this.expandIcon = expandIcon;}

  public String getIcon() { return icon;}
  public void setIcon(String icon) { this.icon = icon; }

  public String getSelectedIcon() { return selectedIcon; }
  public void setSelectedIcon(String selectedIcon) { this.selectedIcon = selectedIcon; }

  public List<?> getChildren() { return children; }
  public void setChildren(List<?> children) { this.children = children; }

  @SuppressWarnings("unchecked")
  public <T> T getSelected() { return (T)selected; }
  public void setSelected(Object selectedObject) { this.selected = selectedObject; }
  
  @SuppressWarnings("unchecked")
  public <T> T getParentSelected() { return (T)parentSelected; }
  public void setParentSelected(Object parentSelected) { this.parentSelected = parentSelected; }

  public List<?> getSibbling() { return sibbling; }
  public void setSibbling(List<?> sibbling) { this.sibbling = sibbling; }
  
  public UIRightClickPopupMenu getUIRightClickPopupMenu() { return uiPopupMenu_; }
  public void setUIRightClickPopupMenu(UIRightClickPopupMenu uiPopupMenu) {
    uiPopupMenu_ = uiPopupMenu;
    if(uiPopupMenu_  != null) uiPopupMenu_.setParent(this);
  }
  
  public String event(String name, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(name, beanId);
    return super.event(name, beanId);
  }
  
  public String renderNode(Object obj) throws Exception {
    String nodeIcon = expandIcon;
    String iconGroup = icon;
    String note = "" ; 
    if(isSelected(obj)) {
      nodeIcon = colapseIcon;
      iconGroup = selectedIcon;
      note = "NodeSelected" ;             
    }
    String objId = String.valueOf(getId(obj)) ;
    String actionLink = event("ChangeNode", objId);
    StringBuilder builder = new StringBuilder();
    builder.append(" <a class=\"").append(nodeIcon).append("\" href=\"").append(actionLink).append("\"><span></span></a>") ;      
    builder.append(" <div class=\"").append(iconGroup).append("\"><span></span></div> ");
    if(uiPopupMenu_ == null){
      builder.append(" <div class=\"NodeLabel\"> ");
    }else{
      builder.append(" <div class=\"NodeLabel\" ").append(uiPopupMenu_.getJSOnclickShowPopup(objId, null)).append("> ");
    }
    builder.append("   <a class =\"").append(note).append("\" href=\"").append(actionLink).append("\">");
    builder.append(     getFieldValue(obj, beanLabelField_));
    builder.append("   </a> ");
    builder.append(" </div> ");
    return builder.toString();
  }
  
  public void renderUIComponent(UIComponent uicomponent) throws Exception {
    uicomponent.processRender((WebuiRequestContext)WebuiRequestContext.getCurrentInstance()) ;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T findComponentById(String id) {
    if(getId().equals(id)) return (T)this ;
    if(uiPopupMenu_ == null) return null;
    return (T)uiPopupMenu_.findComponentById(id);
  }
  
  public <T extends UIComponent> T findFirstComponentOfType(Class<T> type) {
    if (type.isInstance(this)) return type.cast(this);  
    if(uiPopupMenu_ == null) return null;
    return uiPopupMenu_.findFirstComponentOfType(type);
  }
  
  public <T> void findComponentOfType(List<T>list, Class<T> type) {
    if (type.isInstance(this)) list.add(type.cast(this));
    if(uiPopupMenu_ == null) return ;
    uiPopupMenu_.findComponentOfType(list, type);
  }
  
  static  public class ChangeNodeActionListener extends EventListener<UITree> {    
    public void execute(Event<UITree> event) throws Exception {   
      event.getSource().<UIComponent>getParent().broadcast(event, Event.Phase.PROCESS) ;      
    }
  }
  
}
