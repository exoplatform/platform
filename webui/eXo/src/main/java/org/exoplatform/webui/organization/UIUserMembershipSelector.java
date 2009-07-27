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
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.MembershipTypeHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormPopupWindow;
/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 26, 2006
 * 
 */
@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIUserMembershipSelector.gtmpl",
  events = {
    @EventConfig(listeners = UIUserMembershipSelector.SelectMembershipActionListener.class),
    @EventConfig(listeners = UIUserMembershipSelector.DeleteMembershipActionListener.class, phase = Phase.DECODE, confirm = "UIUserMembershipSelector.deleteMembership")  
  }
)
public class UIUserMembershipSelector extends UISelector<String> {  
  
  private List<Membership>  membership_ = new ArrayList<Membership>();
  private String user_ = "site" ;  
  private boolean isAdminRole_ = false;
  public static String[] BEAN_FIELD = {"userName", "groupId", "membershipType"} ;
  public static String[] ACTIONS = {"DeleteMembership"} ;
  public UIUserMembershipSelector() throws Exception {
    super("UIUserMembershipSelector", null);
    setComponentConfig(UIUserMembershipSelector.class, null) ;        
    UIGrid uiGrid = addChild(UIGrid.class, null, "MembershipGrid") ;
    uiGrid.configure("id", BEAN_FIELD, ACTIONS) ;
    
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "UserPermissionSelector");
    uiPopup.setWindowSize(540, 0);
    UIGroupMembershipSelector uiMembershipSelector = createUIComponent(UIGroupMembershipSelector.class, null, null) ;
    uiPopup.setUIComponent(uiMembershipSelector);
  }
  
  public String getUser(){ return user_; }
  
  public void setUserName(String userName) throws Exception {
    user_ = userName;
  }
  
  public Class<String> getTypeValue() { return String.class; }
  
  public boolean isAdminRole() { return isAdminRole_; }
  public void setAdminRole(boolean b) { isAdminRole_ = b; }
  
  @SuppressWarnings("unchecked")
  public void setUser(User user) throws Exception { 
    user_ = user.getUserName();
    membership_.clear();
    OrganizationService service = getApplicationComponent(OrganizationService.class);    
    Collection<org.exoplatform.services.organization.Membership>  
                        collection = service.getMembershipHandler().findMembershipsByUser(user_);
    for(org.exoplatform.services.organization.Membership membership : collection){
      addMembership(new Membership(membership.getUserName(), membership.getId(),
                                   membership.getGroupId(), membership.getMembershipType() ));
    }
    
    UIGrid uiGrid = getChild(UIGrid.class) ;
    LazyPageList pageList = new LazyPageList(new MembershipListAccess(getMembership()), 10) ;
    uiGrid.getUIPageIterator().setPageList(pageList) ;
  }
  
  public List<Membership> getMembership(){ return membership_; }  
  
  public void createMembership(String groupId, String membershipType){
    Membership membership = new Membership(user_, "" , groupId, membershipType);
    addMembership(membership);
  }
  
  public void addMembership(Membership mem) {
    String groupId, membershipType = null ;
    for(Membership ele : membership_) {
      groupId = ele.getGroupId() ;
      membershipType = ele.getMembershipType() ;
      if(groupId.equals(mem.getGroupId())&& membershipType.equals(mem.getMembershipType())) return;
    }
    membership_.add(mem); 
  }
  
  public void  setUIInputValue(Object input) { user_ = (String) input ; }
  
  public void save(OrganizationService service, boolean broadcast) throws Exception {
    GroupHandler groupHandler = service.getGroupHandler();
    MembershipTypeHandler mtHandler = service.getMembershipTypeHandler(); 
    User user = service.getUserHandler().findUserByName(user_);
    for(Membership membership : membership_){
      if(user == null) user = service.getUserHandler().findUserByName(membership.getUserName());
      Group group = groupHandler.findGroupById(membership.getGroupId());
      MembershipType  mt = mtHandler.findMembershipType(membership.getMembershipType());
      if(service.getMembershipHandler() != null) {
        service.getMembershipHandler().linkMembership(user, group, mt, broadcast);
      } 
    }
  }
  
  void setMembership(String groupId, String membershipType){
    createMembership(groupId, membershipType);
  }
  
  public String event(String actionName, String beanId) throws Exception {
    UIForm uiForm = getAncestorOfType(UIForm.class) ;
    if(uiForm != null) return uiForm.event(actionName, getId(), beanId);
    return super.event(name, beanId);
  }
  
  static public class DeleteMembershipActionListener extends EventListener<UIUserMembershipSelector>{
    public void execute(Event<UIUserMembershipSelector> event) throws Exception{
      UIUserMembershipSelector uiUserMembershipSelector = event.getSource();
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageIterator pageIterator = uiUserMembershipSelector.getChild(UIGrid.class).getUIPageIterator();
      int currentPage = pageIterator.getCurrentPage();
      OrganizationService service = uiUserMembershipSelector.getApplicationComponent(OrganizationService.class);
      service.getMembershipHandler().removeMembership(id, true);
      User user = service.getUserHandler().findUserByName(uiUserMembershipSelector.getUser()) ;
      uiUserMembershipSelector.setUser(user) ;
      while(currentPage > pageIterator.getAvailablePage()) currentPage--;
      pageIterator.setCurrentPage(currentPage);
    }
  }
  
  static public class Membership {
    
    private String groupId_;
    private String membershipType_;
    private String userName_;
    private String id_;
    
    public Membership(String userName, String id, String groupId, String membershipType){
      userName_ = userName;
      id_ = id;
      groupId_ = groupId;
      membershipType_ = membershipType;
    }
    
    public Membership(String userName, String groupId, String membershipType){
      this(userName, "", groupId, membershipType);
    }
    
    public String getId(){return id_;}
    public void setId(String id){id_ = id; }
    
    public String getUserName(){ return userName_; }
    public void setUserName(String userName){ userName_ = userName; }
    
    public String getGroupId() { return groupId_; }
    public void setGroupId(String groupId){ groupId_ = groupId; }
    
    public String getMembershipType(){ return membershipType_; }
    public void setMembershipType(String membershipType){ membershipType_ = membershipType; }
  }
    
}
