/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 27, 2006
 */
public class UserACL {

  private OrganizationService orgService_ ;
  
  private UserACLPlugin userACLPlugin_;

  public UserACL(OrganizationService  orgService) throws Exception {
    this.orgService_ = orgService;
  }

  public void computeNavigation(String remoteUser, List<PageNavigation> navs) throws Exception {
    Iterator<PageNavigation> iterator = navs.iterator();
    while(iterator.hasNext()){
      PageNavigation nav = iterator.next();      
      if(hasPermission(nav.getOwner(), remoteUser, nav.getAccessPermission())){
        computeNode(remoteUser, nav.getNodes());
        continue;
      }      
      iterator.remove();
    }
  } 

  public void computeNode(String remoteUser, List<PageNode> nodes) throws Exception {
    if(nodes == null) return;
    Iterator<PageNode> iterator = nodes.iterator();
    while(iterator.hasNext()){
      PageNode node = iterator.next();
      if(hasPermission(node.getCreator(), remoteUser, node.getAccessPermission())){
        computeNode(remoteUser, node.getChildren()); 
        continue;      
      }
      iterator.remove();
    }
  }
  
  public boolean hasPermission(String owner, String remoteUser, String expPerm) throws Exception {
    if(owner != null && owner.equals(remoteUser)) return true;
    if(userACLPlugin_ != null && userACLPlugin_.hasRoleAdmin()) return true;
    if(expPerm == null) return false ;
    Permission permission = new Permission();
    permission.setPermissionExpression(expPerm);
    String groupId = permission.getGroupId();
    if("/guest".equals(groupId)) return true ;

    String membership = permission.getMembership() ;
    MembershipHandler handler = orgService_.getMembershipHandler();
    if(membership == null || "*".equals(membership)) {
      Collection c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, membership) != null;
  }
  
  public void addPlugin(ComponentPlugin plugin){
    if(plugin instanceof UserACLPlugin) userACLPlugin_ = (UserACLPlugin) plugin;
  }
  
  static public abstract class UserACLPlugin  extends BaseComponentPlugin {
    public abstract boolean hasRoleAdmin();
  }

  static public class Permission {

    private String name_ ;
    private String groupId_ = ""  ;  
    private String membership_ = "" ;
    private boolean selected_  = false;

    public void setPermissionExpression(String exp) {
      if(exp == null || exp.length() == 0) return;
      String[] temp = exp.split(":") ;
      if(temp.length < 2) return;
      membership_ = temp[0] ;
      groupId_ = temp[1] ;
    }

    public String getGroupId(){ return groupId_; }
    public void setGroupId(String groupId) { groupId_ = groupId; }

    public String getName(){ return name_; }
    public void setName(String name) { name_ = name; }

    public String getValue(){
      if(membership_ .length() == 0 || groupId_.length() == 0) return null;
      return membership_+":"+groupId_;
    }

    public String getMembership(){ return membership_; }
    public void setMembership(String membership) { membership_ = membership; }

    public boolean isSelected(){ return selected_; }
    public void setSelected(boolean selected){ selected_ = selected; }
  }

}
