/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
/**
 * Jun 27, 2006
 */
public class UserACL {

  protected static Log log = ExoLogger.getLogger("organization:UserACL");
  
  private OrganizationService orgService_ ;
  
  private String superUser_;
  private List<String> portalCreatorGroups_;
  private String navigationCreatorMembershipType_;

  public UserACL(InitParams params, OrganizationService orgService) throws Exception {
    this.orgService_ = orgService;
    
    ValueParam superUserParam = params.getValueParam("super.user");
    if(superUserParam != null) superUser_ = superUserParam.getValue();
    if(superUser_ == null || superUser_.trim().length() == 0) superUser_= "exoadmin";
    
    ValueParam navCretorParam = params.getValueParam("navigation.cretor.membership.type");
    if(navCretorParam != null) navigationCreatorMembershipType_ = navCretorParam.getValue();
    if(navigationCreatorMembershipType_ == null || 
       navigationCreatorMembershipType_.trim().length() == 0) navigationCreatorMembershipType_= "owner";
    
    String allGroups = "";
    ValueParam portalCretorGroupsParam = params.getValueParam("portal.cretor.groups");
    if(portalCretorGroupsParam != null) allGroups = portalCretorGroupsParam.getValue();
    portalCreatorGroups_ = defragmentPermission(allGroups);
  }
  
  private List<String> defragmentPermission(String permission) {
    List<String> result = new ArrayList<String>();
    if(permission !=null ) {
      if(permission.contains(",")){
        String[] groups = permission.split(",");
        for(String group: groups) {  result.add(group.trim());  }
      } else {
        result.add(permission);
      }
    }
    return result;
  }
  
  public String getNavigationCreatorMembershipType() { return navigationCreatorMembershipType_; }
  public List getPortalCreatorGroups() { return portalCreatorGroups_;  }
  public String getSuperUser() { return superUser_ ; }
  
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
    if(!hasViewPermission(pconfig.getCreator(), accessUser, pconfig.getAccessPermissions())) return false;
    if(hasEditPermission(pconfig.getCreator(), accessUser, pconfig.getEditPermission())) {
      pconfig.setModifiable(true);
      return true;
    }
    pconfig.setModifiable(false);
    return true;
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
    if(log.isDebugEnabled())
	  log.debug("------HasViewPermission(3) of owner and User: "  + owner + ":" + remoteUser);
    if(owner != null && owner.equals(remoteUser)) return true;
    if(expPerms == null || expPerms.length < 1) expPerms = new String[]{"*:/user"};
    if(superUser_.equals(remoteUser)) return true;
    for(String expPerm : expPerms) {
      if(hasViewPermission(remoteUser, expPerm)) return true;
    }
    return false;
  }
  
  public boolean hasViewPermission(String remoteUser, String expPerm) throws Exception {
    if(log.isDebugEnabled())
	  log.debug("------HasVeiwPermission(2) of User "  + remoteUser );
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
    if(log.isDebugEnabled())
	  log.debug("------HasEditPermission(3) of owner and user "  + owner + ":" + remoteUser);
    if(owner != null && owner.equals(remoteUser)) return true;
    if(superUser_.equals(remoteUser)) return true;
    if(expPerm == null) return false;
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
  
  private boolean hasPermission(String remoteUser, String expPerm) throws Exception {
    if(log.isDebugEnabled())
    log.debug("------CheckPermission of User "  + remoteUser + " with membership " + expPerm);
    if(superUser_.equals(remoteUser)) return true;
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
  
  public boolean hasCreatePortalPermission(String remoteUser) throws Exception {
    if(superUser_.equals(remoteUser)) return true;
    if( portalCreatorGroups_ == null || portalCreatorGroups_.size() < 1) return false;
    for(String ele: portalCreatorGroups_){
      if(hasPermission(remoteUser, ele)) return true;
    }
    return false;
  }
  
  public boolean hasEditPermission(PortalConfig pconfig, String remoteUser) throws Exception {
    if(superUser_.equals(remoteUser)) return true;
    return hasPermission(remoteUser, pconfig.getEditPermission());
  }
  
  public boolean hasViewPermission(PortalConfig pconfig, String remoteUser) throws Exception{
    if( hasEditPermission(pconfig, remoteUser) == true) return true;
    String[] accessPerms = (pconfig.getAccessPermissions());
    for(String per: accessPerms){
      if(hasPermission(remoteUser, per)) return true;
    }
    return false;
  }
  
  public boolean hasEditPermission(PageNavigation pconfig, String remoteUser) throws Exception {
    if(superUser_.equals(remoteUser)) return true;
    String ownerType= pconfig.getOwnerType();
    if(PortalConfig.PORTAL_TYPE.equals(ownerType)){
      
    } else if( PortalConfig.GROUP_TYPE.equals(ownerType)) {
      String expPerm = navigationCreatorMembershipType_+ ":" + pconfig.getOwnerId();
      return hasPermission(remoteUser, expPerm);
    } else{
      return remoteUser.equals(pconfig.getOwnerId());
    }
    return false;
  }
 
  public boolean hasEditPermission(Page page, String remoteUser)  throws Exception {
    if(superUser_.equals(remoteUser)) return true;
    if(PortalConfig.USER_TYPE.equals(page.getOwnerType())){
      return remoteUser.equals(page.getOwnerId());
    }
    return hasPermission(remoteUser, page.getEditPermission());
  }
  
  public boolean hasViewPermission(Page page, String remoteUser)throws Exception {
    if(superUser_.equals(remoteUser)) return true;
    if(PortalConfig.USER_TYPE.equals(page.getOwnerType())){
      return remoteUser.equals(page.getOwnerId());
    }
    if(hasEditPermission(page, remoteUser)) return true;
    String[] accessPerms = page.getAccessPermissions();    
    for(String per: accessPerms){
      if(hasPermission(remoteUser, per)) return true;
    }
    return false;
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
