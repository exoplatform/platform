/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
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
  @ComponentConfig(events = @EventConfig(listeners = UIUserInGroup.DeleteUserActionListener.class)),
  @ComponentConfig(
     type = org.exoplatform.organization.webui.component.UIUserInGroup.UIGridUser.class,
     id = "UIGridUser",
     template = "system:/groovy/webui/component/UIGrid.gtmpl"
  )
})
public class UIUserInGroup extends UIContainer {

  private static String[] USER_BEAN_FIELD = {"userName", "firstName", "lastName", "membershipType", "email"};

  private static String[] USER_ACTION = {"DeleteUser"} ;
  
  public UIUserInGroup() throws Exception {
    UIGrid uiGrid = addChild(UIGridUser.class, "UIGridUser", null) ;
    uiGrid.configure("id", USER_BEAN_FIELD, USER_ACTION) ;
    addChild(UIGroupMembershipForm.class, null, null);
  }  

  public Group getSelectedGroup(){
    UIOrganizationPortlet uiOrganizationPortlet = getAncestorOfType(UIOrganizationPortlet.class);
    UIGroupManagement uiGroupManagement = uiOrganizationPortlet.findFirstComponentOfType(UIGroupManagement.class);
    UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class);
    return uiGroupExplorer.getCurrentGroup();
  }

  public String getName() { return "UIUserInGroup" ; }  

  public void setValues() throws Exception {    
    setValues(getSelectedGroup());
  }

  public void setValues(Group group) throws Exception {
    String groupId = null;
    if(group != null) groupId = group.getId();
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    PageList pagelist = service.getUserHandler().findUsersByGroup(groupId);
    UIGridUser uiGrid = getChild(UIGridUser.class) ;
    uiGrid.setGroupId(groupId);
    uiGrid.getUIPageIterator().setPageList(pagelist);
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.write("<div class=\"UIUserInGroup\">");
    renderChildren();
    w.write("</div>");
  }

  static  public class DeleteUserActionListener extends EventListener<UIUserInGroup> {
    public void execute(Event<UIUserInGroup> event) throws Exception {
      UIUserInGroup uiUserInGroup = event.getSource() ;
      String id = event.getRequestContext().getRequestParameter(OBJECTID) ;
      OrganizationService service = uiUserInGroup.getApplicationComponent(OrganizationService.class);
      MembershipHandler handler = service.getMembershipHandler();
      handler.removeMembership(id, true) ;
      uiUserInGroup.setValues();
    }
  }

  static public class UIGridUser extends UIGrid {
    
    private String groupId_;

    public UIGridUser() throws Exception {
      super();
    }

    public List getBeans() throws Exception { 
      List list = super.getBeans();      
      Iterator it = list.iterator() ;
      boolean add = true;
      List<MembershipUser> memberships = new ArrayList<MembershipUser>() ;
      while(it.hasNext()){
        User user = (User)it.next() ;
        add = true;
        for(MembershipUser ele : memberships ){
          if(ele.getUser() != user) continue;
          add = false;
          break;
        }
        if(add) loadMemberships(user, memberships) ;
      } 
      return  memberships;
    }
    
    private void loadMemberships(User user, List<MembershipUser> memberships) throws Exception{
      OrganizationService service = getApplicationComponent(OrganizationService.class) ;
      MembershipHandler handler = service.getMembershipHandler();
      Collection mt = handler.findMembershipsByUserAndGroup(user.getUserName(), groupId_) ;
      Iterator it = mt.iterator() ;
      while(it.hasNext()){
        Membership type = (Membership)it.next() ;
        memberships.add(new MembershipUser(user, type.getMembershipType(), type.getId()));
      }
    }

    public void setGroupId(String groupId) { this.groupId_ = groupId; }
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
