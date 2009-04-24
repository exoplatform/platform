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
package org.exoplatform.organization.webui.component;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfigs({
  @ComponentConfig(
     events = {
       @EventConfig(listeners = UIUserInGroup.DeleteUserActionListener.class, confirm = "UIUserInGroup.confirm.deleteUser"),
       @EventConfig(listeners = UIUserInGroup.EditActionListener.class)
     }
  ),
  @ComponentConfig(
     type = org.exoplatform.organization.webui.component.UIUserInGroup.UIGridUser.class,
     id = "UIGridUser",
     template = "system:/groovy/webui/core/UIGrid.gtmpl"
  )
})
public class UIUserInGroup extends UIContainer {

  private static String[] USER_BEAN_FIELD = {"userName", "firstName", "lastName", "membershipType", "email"};

  private static String[] USER_ACTION = {"Edit", "DeleteUser"} ;
  
  public UIUserInGroup() throws Exception {
    UIGrid uiGrid = addChild(UIGridUser.class, "UIGridUser", null) ;
    uiGrid.configure("id", USER_BEAN_FIELD, USER_ACTION) ;
    uiGrid.getUIPageIterator().setId("UIUserInGroupIterator") ;
    addChild(UIGroupMembershipForm.class, null, null);
    UIPopupWindow editMemberPopup = addChild(UIPopupWindow.class, null, "EditMembership");
    editMemberPopup.setWindowSize(400, 0);
  }  
  
  @Override
  protected String loadConfirmMesssage(org.exoplatform.webui.config.Event event, WebuiRequestContext context, String beanId) {
   
    String confirm = event.getConfirm();
    if(confirm.length() < 1) return confirm;
    UIGridUser uiGrid = getChild(UIGridUser.class);
    try {
      confirm = context.getApplicationResourceBundle().getString(confirm);
      MembershipUser membershipUser = uiGrid.searchMembershipUser(beanId);
      if(membershipUser == null) return confirm;
      Group selectGroup = getSelectedGroup();
      if(selectGroup == null) return confirm;
      confirm = confirm.replaceAll("\\{0\\}", membershipUser.getUserName());
      confirm = confirm.replaceAll("\\{1\\}", selectGroup.getId().substring(1));
    }catch (Exception e) {
      
    }
    return confirm;
  }

  public Group getSelectedGroup(){
    UIOrganizationPortlet uiOrganizationPortlet = getAncestorOfType(UIOrganizationPortlet.class);
    UIGroupManagement uiGroupManagement = uiOrganizationPortlet.findFirstComponentOfType(UIGroupManagement.class);
    UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class);
    return uiGroupExplorer.getCurrentGroup();
  }

  public String getName() { return "UIUserInGroup" ; }  

  public void refresh() throws Exception { setValues(getSelectedGroup()); }

  public void setValues(Group group) throws Exception {
    PageList pageList = null;
    if(group == null) {
      pageList = new ObjectPageList(new ArrayList<Object>(), 10) ;
    } else {
      OrganizationService service = getApplicationComponent(OrganizationService.class) ;
      MembershipHandler handler = service.getMembershipHandler();
      List<?> memberships = (List<?>)handler.findMembershipsByGroup(group) ;
      pageList = new ObjectPageList(memberships, 10) ;
    }
    UIGridUser uiGrid = getChild(UIGridUser.class) ;
    pageList.setPageSize(5) ;
    uiGrid.getUIPageIterator().setPageList(pageList);
    
    if (group != null) {
      String groupId = group.getId();
      // show action if is administrator
      boolean showAction = GroupManagement.isAdministrator(null);
      
      if (!showAction) {
        pageList.setPageSize(10) ;
        if (getChild(UIGroupMembershipForm.class) != null)
          removeChild(UIGroupMembershipForm.class);
        uiGrid.configure("id", USER_BEAN_FIELD, null);
      } else {
        pageList.setPageSize(5) ;
        uiGrid.configure("id", USER_BEAN_FIELD, USER_ACTION) ;
        if (getChild(UIGroupMembershipForm.class) == null)
          addChild(UIGroupMembershipForm.class, null, null);
      }
    } else {
      pageList.setPageSize(10) ;
      if (getChild(UIGroupMembershipForm.class) != null)
        removeChild(UIGroupMembershipForm.class);
    }
    
  }


  public void processRender(WebuiRequestContext context) throws Exception {
    //TODO: Tung.Pham edded
    //-------------------------
    refresh() ;
    //-------------------------
    Writer w =  context.getWriter() ;
    w.write("<div class=\"UIUserInGroup\">");
    renderChildren();
    w.write("</div>");
  }

  static  public class DeleteUserActionListener extends EventListener<UIUserInGroup> {
    public void execute(Event<UIUserInGroup> event) throws Exception {
      UIUserInGroup uiUserInGroup = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIPageIterator pageIterator = uiUserInGroup.getChild(UIGridUser.class).getUIPageIterator();
      int currentPage = pageIterator.getCurrentPage();
      OrganizationService service = uiUserInGroup.getApplicationComponent(OrganizationService.class);
      MembershipHandler handler = service.getMembershipHandler();
      handler.removeMembership(id, true) ;
      uiUserInGroup.refresh();
      while(currentPage > pageIterator.getAvailablePage()) currentPage--;
      pageIterator.setCurrentPage(currentPage);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiUserInGroup.getChild(UIGridUser.class));
    }
  }
  
  static  public class EditActionListener extends EventListener<UIUserInGroup> {
    public void execute(Event<UIUserInGroup> event) throws Exception {
      UIUserInGroup uiUserInGroup = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      OrganizationService service = uiUserInGroup.getApplicationComponent(OrganizationService.class);
      MembershipHandler handler = service.getMembershipHandler();
      UIPopupWindow uiPopup = uiUserInGroup.getChild(UIPopupWindow.class);
      UIGroupEditMembershipForm uiEditMemberShip = 
        uiUserInGroup.createUIComponent(UIGroupEditMembershipForm.class, null, null);
      uiEditMemberShip.setValue(handler.findMembership(id), uiUserInGroup.getSelectedGroup());
      uiPopup.setUIComponent(uiEditMemberShip);
      uiPopup.setShow(true);
    }
  }

  static public class UIGridUser extends UIGrid {
    
    private List<MembershipUser> membershipUsers;
    
    public UIGridUser() throws Exception {
      super();
      membershipUsers = new ArrayList<MembershipUser>() ;
    }
    
    public List<?> getBeans() throws Exception {
      membershipUsers.clear();
      List<?> list = super.getBeans() ;
      Iterator<?> itr = list.iterator() ;
      while(itr.hasNext()){
        Membership membership = (Membership)itr.next() ;
        MembershipUser mu = toMembershipUser(membership);
        if(mu != null) membershipUsers.add(mu) ;
      }
      return membershipUsers ;
    }
    
    private MembershipUser searchMembershipUser(String beanId) {
      for(MembershipUser ele : membershipUsers) {
        if(ele.id.equals(beanId)) return ele;
      }
      return null;
    }
    
    private MembershipUser toMembershipUser(Membership membership) throws Exception {
      OrganizationService service = getApplicationComponent(OrganizationService.class) ;
      String userName = membership.getUserName() ;
      UserHandler handler = service.getUserHandler();
      User user = handler.findUserByName(userName) ;
      if(user == null) return null;
      return new MembershipUser(user, membership.getMembershipType(), membership.getId()) ;
    }

  }

  static public class MembershipUser {    

    private User user;
    private String mtype;
    private String id;

    public MembershipUser(User user, String mtype, String id){
      this.mtype = mtype;
      this.user = user;
      this.id = id;
    }

    public String getMembershipType() { return mtype; }
    public void setMembershipType(String mtype) { this.mtype = mtype; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getUserName(){ return user.getUserName(); }
    public String getFirstName(){ return user.getFirstName(); }
    public String getLastName(){ return  user.getLastName(); }
    public String getEmail() { return user.getEmail(); }

    public String getId(){ return id; }
  }

}
