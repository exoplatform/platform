/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.form.validator;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Apr 12, 2007
 * 
 * Validates whether the current user is allowed to perform the current operation
 */
public class PermissionValidaror {
  
  public void validate(UIComponent uicomponent, String permission) throws Exception {
    OrganizationService service = (OrganizationService) PortalContainer.getComponent(OrganizationService.class);
    if(permission == null || permission.length() < 1 || permission.equals("*")) return; 
    Object[] args = {uicomponent.getName()} ;
    String[] tmp = permission.split(":", 2) ;
    if(tmp.length != 2) {
      throw new MessageException(new ApplicationMessage("PermissionValidator.msg.invalid-permission-input", args));
    }
    String membership = tmp[0] ;
    String groupId = tmp[1] ;
    Group group = null;
    MembershipType membershipType = null;
    try{
      membershipType = service.getMembershipTypeHandler().findMembershipType(membership) ;
      group = service.getGroupHandler().findGroupById(groupId);
    }catch(Exception e){
      e.printStackTrace();
    }
    if(membership.equals("*")){
      if(membershipType != null && group != null) return;
      throw new MessageException(new ApplicationMessage("PermissionValidator.msg.membership-group-not-found", args));
    }
    if(group != null) return;
    throw new MessageException(new ApplicationMessage("PermissionValidator.msg.membership-group-not-found", args));
  }
}
