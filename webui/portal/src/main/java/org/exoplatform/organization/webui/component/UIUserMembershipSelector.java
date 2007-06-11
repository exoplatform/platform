/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.MembershipTypeHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
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
  
  private List<Membership>  membership_ ;
  private String user_ = "site" ;  
  private boolean isAdminRole_ = false;
  
  public UIUserMembershipSelector() throws Exception {
    super("UIUserMembershipSelector", null);
    setComponentConfig(UIUserMembershipSelector.class, null) ;        
    membership_ = new ArrayList<Membership>();   
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
      System.out.println("\n\n\nUIUserMembershipSelector.java.Delete");
      UIUserMembershipSelector uiUserMembershipSelector = event.getSource();
      String index = event.getRequestContext().getRequestParameter(OBJECTID);  
      Membership membership = uiUserMembershipSelector.getMembership().get(Integer.parseInt(index));
      OrganizationService service = uiUserMembershipSelector.getApplicationComponent(OrganizationService.class);
      service.getMembershipHandler().removeMembership(membership.id_, true);
     
      uiUserMembershipSelector.getMembership().remove(Integer.parseInt(index));   
      
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
