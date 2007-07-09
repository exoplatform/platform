/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormGrid;
import org.exoplatform.webui.form.UIFormPopupWindow;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 7, 2007
 */
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIListPermissionSelector.gtmpl",
  events = {
      @EventConfig(phase = Phase.DECODE, listeners = UIListPermissionSelector.CloseActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIListPermissionSelector.DeleteActionListener.class, confirm = "UIAccessGroup.deleteAccessGroup"),
    @EventConfig(phase = Phase.DECODE, listeners = UIPermissionSelector.SelectMembershipActionListener.class)
  }
)
public class UIListPermissionSelector extends UISelector<String[]> { 

  public UIListPermissionSelector() throws Exception {
    UIFormGrid uiGrid = addChild(UIFormGrid.class, null, "PermissionGrid") ;
    uiGrid.configure("expression", new String[]{"groupId", "membership"}, new String[]{"Delete"});
    uiGrid.getUIPageIterator().setPageList(new ObjectPageList(new ArrayList<Permission>(), 10));
    //TODO: Tung.Pham added
    //-------------------------------------------
    addChild(uiGrid.getUIPageIterator()) ;
    uiGrid.getUIPageIterator().setRendered(false) ;
    //-------------------------------------------
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "UIGroupMembershipSelector");
    uiPopup.setWindowSize(540, 0);
    
    UIGroupMembershipSelector uiMembershipSelector = createUIComponent(UIGroupMembershipSelector.class, null, null) ;
    uiMembershipSelector.setId("ListPermissionSelector");
    uiMembershipSelector.getChild(UITree.class).setId("TreeListPermissionSelector");
    uiMembershipSelector.getChild(UIBreadcumbs.class).setId("BreadcumbsListPermissionSelector");
    uiPopup.setUIComponent(uiMembershipSelector);
  }
  
  public void configure(String iname, String bfield) {
    setName(iname) ;
    setBindingField(bfield) ; 
  }
  
  @SuppressWarnings("unchecked")
  private boolean existsPermission(List<?> list, Permission permission) throws Exception {
    for(Object ele: list) {
      Permission per = (Permission) ele;
      if(per.getExpression().equals(permission.getExpression())) return true;
    }
    return false;
  }
  
  public void clearGroups() throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  @SuppressWarnings("unchecked")
  public String [] getValue() throws Exception {
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    List<Object> values = uiIterator.getPageList().getAll();
    String [] expPermissions = new String[values.size()];
    for(int i = 0; i < values.size(); i++) {
      Permission permission = (Permission) values.get(i);
      expPermissions[i] = permission.getExpression(); 
    }
    return expPermissions;
  }
  
  public UIListPermissionSelector setValue(String [] permissions) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    for(String exp : permissions) {
      if(exp.trim().length() < 1) continue;
      Permission permission  = new Permission();
      permission.setPermissionExpression(exp);
      if(existsPermission(list, permission)) continue;
      list.add(permission);
    }
    uiIterator.setPageList(new ObjectPageList(list, 10));
    return this;
  }
  
  @SuppressWarnings("unchecked")
  public void removePermission(String exp) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    list.addAll(uiIterator.getPageList().getAll());
    for(Object ele : list) {
      Permission permission = (Permission) ele;
      if(permission.getExpression().equals(exp)) {
        list.remove(ele);
        break;
      }
    }
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  @SuppressWarnings("unchecked")
  public void setMembership(String groupId, String membershipType) throws Exception {
    if(groupId.trim().length() < 1 || membershipType.trim().length() < 1) return ;
    Permission permission = new Permission();
    permission.setExpression(membershipType+":"+groupId);
    permission.setGroupId(groupId);
    permission.setMembership(membershipType);
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    list.addAll(uiIterator.getPageList().getAll());
    if(existsPermission(list, permission))  return;
    list.add(permission);
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  public Class<String[]> getTypeValue() { return String[].class; }
    
  static  public class DeleteActionListener extends EventListener<UIListPermissionSelector> {   
    public void execute(Event<UIListPermissionSelector> event) throws Exception {
      String permission  = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIListPermissionSelector uiPermissions = event.getSource() ;
      uiPermissions.removePermission(permission);
      UIContainer uiParent = uiPermissions.getParent() ;
      uiParent.setRenderedChild(UIListPermissionSelector.class) ;
      UIForm uiForm = uiPermissions.getAncestorOfType(UIForm.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }
  
  static  public class CloseActionListener extends EventListener<UIPopupWindow> {
    public void execute(Event<UIPopupWindow> event) throws Exception {
      UIPopupWindow uiPopupWindow = event.getSource();
      System.out.println("\n\n\n+++++++++++++++++++>>>>>>>>>>>>>>>>>> HUN");
    }
  }

}