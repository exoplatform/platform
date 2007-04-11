/*
 * Copyright 2001-2003 The eXo platform SARL All rights reserved.
 * Please look at license.txt in info directory for more license detail. 
 */

package org.exoplatform.services.portletregistery;

/**
 * Created y the eXo platform team
 * User: Benjamin Mestrallet
 * Date: 15 juin 2004
 */
public class PortletPermission {
 
  private String id;
  private String membership;
  private String groupId;
  private String description;
  private String portletId ;

  public String getId() {  return id; }
  public void setId(String id) { this.id = id; }

  public String getMembership() {  return membership; }
  public void setMembership(String roleName) { this.membership = roleName; }
  
  public String getGroupId() { return groupId ; }
  public void setGroupId(String roleName) { this.groupId = roleName; }
  
  public String getPortletId() {    return portletId; }
  public void setPortletId(String portletId) { this.portletId = portletId; }
  
  public String getDescription() { return description ; }
  public void setDescription(String s) { this.description = s; }
 
  public String getPermissionExpression() { 
    if(groupId == null || groupId.length() == 0) return null ;
    return membership + ":"  + groupId ;
  }
  
  public void   setPermissionExpression(String s) {
    if(s == null || s.length() == 0) {
      groupId = null ;
      membership = null ;
      return ;
    } 
    String[] tmp = s.split(":", 2) ;
    if(tmp.length != 2) throw new RuntimeException("expect the permission expression format membership:/groupId") ;
    membership = tmp[0] ;
    groupId = tmp[1] ;
  }
}