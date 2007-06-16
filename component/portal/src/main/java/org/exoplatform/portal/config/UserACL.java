/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 27, 2006
 */
public class UserACL {

  private OrganizationService orgService_ ;
  
  private String superUser_;

  public UserACL(InitParams params, OrganizationService orgService) throws Exception {
    this.orgService_ = orgService;
    
    ValueParam valueParam = params.getValueParam("super.user");
    if(valueParam != null) superUser_ = valueParam.getValue();
    if(superUser_ == null || superUser_.trim().length() == 0) superUser_= "exoadmin";
  }
  
  void computeNavigation(List<PageNavigation> navs, String remoteUser) throws Exception {
    Iterator<PageNavigation> iterator = navs.iterator();
    while(iterator.hasNext()){
      PageNavigation nav = iterator.next();
      if(hasPermission(nav, remoteUser)) continue;
      iterator.remove();
    }
    
    Collections.sort(navs, new Comparator<PageNavigation>(){
      public int compare(PageNavigation nav1, PageNavigation nav2) {
        return nav1.getPriority() - nav2.getPriority();
      }
    });
    
  }  
  
  boolean hasPermission(PortalConfig pconfig, String accessUser) throws Exception {
    if(hasEditPermission(pconfig.getCreator(), accessUser, pconfig.getEditPermission())) {
      pconfig.setModifiable(true);
      return true;
    }
    pconfig.setModifiable(false);
    
    return hasViewPermission(pconfig.getCreator(), accessUser, pconfig.getAccessPermissions()) ;
  }
  
  boolean hasPermission(Page page, String accessUser) throws Exception {
    String owner = page.getCreator();
    if(page.getOwnerType().equals(PortalConfig.USER_TYPE)) owner = page.getOwnerId();
    if(hasEditPermission(owner, accessUser, page.getEditPermission())) {
      page.setModifiable(true);
      return true;
    }
    page.setModifiable(false);
    
    return hasViewPermission(owner, accessUser, page.getAccessPermissions()) ;
  }
  
  boolean hasPermission(PageNavigation nav, String accessUser) throws Exception {
    String owner = nav.getCreator();
    if(nav.getOwnerType().equals(PortalConfig.USER_TYPE)) owner = nav.getOwnerId();
    
    if(hasEditPermission(owner, accessUser, nav.getEditPermission())) {
      nav.setModifiable(true);
      return true;
    }
    nav.setModifiable(false);
    return hasViewPermission(owner, accessUser, nav.getAccessPermissions()) ;
  }
  
  public boolean hasViewPermission(String owner, String remoteUser, String[] expPerms) throws Exception {
    if(owner != null && owner.equals(remoteUser)) return true;
    if(superUser_.equals(remoteUser)) return true;
    if(expPerms == null || expPerms.length < 1) expPerms = new String[] {"*:/guest"};
    for(String expPerm : expPerms) {
      if(hasViewPermission(remoteUser, expPerm)) return true;
    }
    return false;
  }
  
  public boolean hasViewPermission(String remoteUser, String expPerm) throws Exception {
    if(expPerm == null) return false ;
    Permission permission = new Permission();
    permission.setPermissionExpression(expPerm);
    String groupId = permission.getGroupId();
    if("/guest".equals(groupId)) return true ;

    String membership = permission.getMembership() ;
    MembershipHandler handler = orgService_.getMembershipHandler();
    if(membership == null || "*".equals(membership)) {
      Collection<?> c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, membership) != null;
  }
  
  public boolean hasEditPermission(String owner, String remoteUser, String expPerm) throws Exception {
    if(owner != null && owner.equals(remoteUser)) return true;
    if(superUser_.equals(remoteUser)) return true;
    if(expPerm == null) return false ;
    Permission permission = new Permission();    
    permission.setPermissionExpression(expPerm);
    String groupId = permission.getGroupId();

    String membership = permission.getMembership() ;
    MembershipHandler handler = orgService_.getMembershipHandler();
    if(membership == null || "*".equals(membership)) {
      Collection<?> c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, membership) != null;
  }
  
  static public class Permission {

    private String name_ ;

    private String groupId_ = ""  ;  
    private String membership_ = "" ;
    private String expression;
    
    private boolean selected_  = false;

    public void setPermissionExpression(String exp) {
      if(exp == null || exp.length() == 0) return;
      String[] temp = exp.split(":") ;
      if(temp.length < 2) return;
      expression = exp;
      membership_ = temp[0].trim() ;
      groupId_ = temp[1].trim() ;
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

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }
  }

}
