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
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jun 27, 2006
 */
class UserACL {

  private OrganizationService orgService_ ;
  
  private String viewMembershipType_ ;
  private String editMembershipType_ ;
  
  private String superUser_;

  UserACL(InitParams params, OrganizationService  orgService) throws Exception {
    this.orgService_ = orgService;
    
    ValueParam valueParam = params.getValueParam("view.membership.type");
    if(valueParam != null) viewMembershipType_ = valueParam.getValue();
    if(viewMembershipType_ == null  || viewMembershipType_.trim().length() == 0) viewMembershipType_ = "*";
    
    valueParam = params.getValueParam("edit.membership.type");
    if(valueParam != null) editMembershipType_ = valueParam.getValue();
    if(editMembershipType_ == null || editMembershipType_.trim().length() == 0) editMembershipType_ = "owner";
    
    valueParam = params.getValueParam("super.user");
    if(valueParam != null) superUser_ = valueParam.getValue();
    if(superUser_ == null || superUser_.trim().length() == 0) superUser_= "exoadmin";
  }
  
  String getViewMembershipType() { return viewMembershipType_ ; }
  String getEditMembershipType() { return editMembershipType_ ; }

  void computeNavigation(List<PageNavigation> navs, String remoteUser) throws Exception {
    Iterator<PageNavigation> iterator = navs.iterator();
    while(iterator.hasNext()){
      PageNavigation nav = iterator.next();
      if(hasPermission(nav, remoteUser, viewMembershipType_)) {
        nav.setModifiable(hasPermission(nav, remoteUser, editMembershipType_));
        continue;
      }            
      iterator.remove();
    }
    
    Collections.sort(navs, new Comparator<PageNavigation>(){
      public int compare(PageNavigation nav1, PageNavigation nav2) {
        return nav1.getPriority() - nav2.getPriority();
      }
    });
    
    /*for(int i = 0; i < navs.size(); i++) {
      Iterator<PageNode> nodeIterator = navs.get(i).getNodes().iterator();
      while(nodeIterator.hasNext()){
        PageNode node = nodeIterator.next();
        for(int j = i+1; j < navs.size(); j++) {
          Iterator<PageNode> nextNodeIterator = navs.get(j).getNodes().iterator();
          while(nextNodeIterator.hasNext()){
            PageNode nextNode = nextNodeIterator.next();
            if(node.getUri().equals(nextNode.getUri())) {
              if(navs.get(i).getPriority() < navs.get(j).getPriority()) {
                nodeIterator.remove();
              } else {
                nextNodeIterator.remove();
              }
              break;
            }            
          }          
        }
      }
    }*/
    
  }  
  
  boolean hasPermission(PortalConfig config, String remoteUser, String mt) throws Exception {
    String [] groups = config.getAccessGroup();
    if(groups == null) groups = new String[]{"/user"}; 
    for(String group : groups) {
      if(hasPermission(null, remoteUser, group, mt)) return true;
    }
    return false;
  }
  
  boolean hasPermission(Page page, String remoteUser, String mt) throws Exception {
    String [] groups = page.getAccessGroup();
    if(groups == null) groups = new String[]{"/user"}; 
    String owner = null;
    if(page.getOwnerType().equals(DataStorage.USER_TYPE)) owner = page.getOwnerId(); 
    for(String group : groups) {
      if(hasPermission(owner, remoteUser, group, mt)) return true;
    }
    return false;
  }
  
  boolean hasPermission(PageNavigation nav, String remoteUser, String mt) throws Exception {
    String [] groups = nav.getAccessGroup();
    if(groups == null) groups = new String[]{"/user"}; 
    String owner = null;
    if(nav.getOwnerType().equals(DataStorage.USER_TYPE)) owner = nav.getOwnerId();
    for(String group : groups) {
      if(hasPermission(owner, remoteUser, group, mt)) return true;
    }
    return false;
  }
  
  boolean hasPermission(String owner, String remoteUser, String groupId, String mt) throws Exception {
    if(owner != null && owner.equals(remoteUser)) return true;
    if(superUser_.equals(remoteUser)) return true;
    groupId = groupId.trim();
    if("/guest".equals(groupId)) return true ;

    MembershipHandler handler = orgService_.getMembershipHandler();
    if(mt == null || "*".equals(mt)) {
      Collection c = handler.findMembershipsByUserAndGroup(remoteUser, groupId) ;
      if(c == null) return false ;
      return c.size() > 0 ;
    } 
    return handler.findMembershipByUserGroupAndType(remoteUser, groupId, mt) != null;
  }

}
