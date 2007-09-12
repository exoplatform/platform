/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
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
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormGrid;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormPageIterator;
import org.exoplatform.webui.form.UIFormPopupWindow;
import org.exoplatform.webui.form.validator.Validator;

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
    @EventConfig(phase = Phase.DECODE, listeners = UIPermissionSelector.SelectMembershipActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIListPermissionSelector.ChangePublicModeActionListener.class)
  }
)
public class UIListPermissionSelector extends UISelector<String[]> { 
  
  private boolean publicMode_ = false ;

  public UIListPermissionSelector() throws Exception {
    UIFormCheckBoxInput<Boolean> uiPublicMode = new UIFormCheckBoxInput<Boolean>("publicMode", null, false) ;
    uiPublicMode.setOnChange("ChangePublicMode", "UIListPermissionSelector") ;
    addChild(uiPublicMode) ;
    UIFormGrid uiGrid = addChild(UIFormGrid.class, null, "PermissionGrid") ;
    uiGrid.configure("expression", new String[]{"groupId", "membership"}, new String[]{"Delete"});
    UIFormPageIterator uiIterator = (UIFormPageIterator)uiGrid.getUIPageIterator() ;
    uiIterator.setPageList(new ObjectPageList(new ArrayList<Permission>(), 10));
    addChild(uiIterator) ;
    uiIterator.setRendered(false) ;
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
    setPublicMode(false);
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    for(String exp : permissions) {
      if(exp.trim().length() < 1) continue;
      Permission permission  = new Permission();
      permission.setPermissionExpression(exp);
      if(existsPermission(list, permission)) continue;
      list.add(permission);
      if(exp.equals("*:/guest")) {
        UIFormGrid uiGrid = getChild(UIFormGrid.class) ;
        uiGrid.setRendered(false) ;
        setPublicMode(true);
        UIFormCheckBoxInput<Boolean> uiPublicMode = getChildById("publicMode") ;
        uiPublicMode.setChecked(true) ;
      }
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
  
  public String getLabel(String id) throws Exception {
    String label = null ;
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    ResourceBundle res = context.getApplicationResourceBundle() ;
    String key = getId() + ".label." + id ;
    try{
      label = res.getString(key) ; 
    } catch(MissingResourceException e) {
      System.err.println("\nkey: " + key);
    }
    return label ;
  }
  
  public boolean isPublicMode() {
    return publicMode_ ;
  }

  public void setPublicMode(boolean mode) throws Exception {
    publicMode_ = mode ;
    UIFormGrid uiGrid = getChild(UIFormGrid.class) ;
    uiGrid.setRendered(!publicMode_) ;
    if(publicMode_) {
      setMembership("/guest", "*") ;
    }else {
      removePermission("*:/guest") ;
    }
    
  }
      
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
    //  UIPopupWindow uiPopupWindow = event.getSource();
      System.out.println("\n\n\n+++++++++++++++++++>>>>>>>>>>>>>>>>>> HUN");
    }
  }
  
  static public class ChangePublicModeActionListener extends EventListener<UIListPermissionSelector> {
    public void execute(Event<UIListPermissionSelector> event) throws Exception {
      UIListPermissionSelector uicom = event.getSource();
      UIFormCheckBoxInput<Boolean> uiPublicModeInput = uicom.getChildById("publicMode") ;
      uicom.setPublicMode(uiPublicModeInput.isChecked()) ;
      UIForm uiForm = uicom.getAncestorOfType(UIForm.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm.getParent()) ;
    }
    
  }
  static public class EmptyIteratorValidator implements Validator {

    public void validate(UIFormInput uiInput) throws Exception {
      UIFormInputContainer uiInputContainer = (UIFormInputContainer) uiInput ;
      UIFormPageIterator uiInputIterator = uiInputContainer.findFirstComponentOfType(UIFormPageIterator.class) ;
      if(uiInputIterator.getAvailable() < 1) {
        String[] args =  {uiInputContainer.getBindingField()} ;
        throw new MessageException(new ApplicationMessage("EmptyIteratorValidator.msg.empty", args, ApplicationMessage.INFO)) ;
      }
    }
    
  }

}