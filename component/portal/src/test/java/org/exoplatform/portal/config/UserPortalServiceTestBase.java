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
package org.exoplatform.portal.config;

import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserProfile;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Dec 2, 2005
 */
abstract class UserPortalServiceTestBase extends BasicTestCase {
  
  protected static  OrganizationService orgService_ ;
  protected static HibernateService hservice_;  
  
  protected static String demo  = "demo" ;
  protected static String Group1 = "Group1" ;
  protected static String Group2 = "Group2" ;
  protected static String username1 = "userName_1";
  protected static String username2 = "userName_2" ;
  protected static String memtype1 = "MembershipType_1" ;
  protected static String memtype2 = "MembershipType_2" ;   
  
  protected Group group1, group2, groupDefault;  
  protected MembershipType mType1,mType2, mTypeDefault ;
  protected User user1, user2 ,userDefault;     
  
  public UserPortalServiceTestBase(String name) {
    super(name);    
  }
  
  
  public void setUp() throws Exception {    
  }
  
  public void tearDown() throws Exception {    
  }
  
  protected void prepareOrganizationData() throws Exception{
    groupDefault = orgService_.getGroupHandler().findGroupById("/users") ;         
    if(group1 ==null) { group1 = createGroup( Group1); }    
    if(group2 ==null) { group2 = createGroup( Group2) ; }
    
    mTypeDefault = orgService_.getMembershipTypeHandler().findMembershipType("member") ;
    if(mType1 ==null) { mType1 = createMembershipType(memtype1); }    
    if(mType2 ==null) {mType2 = createMembershipType(memtype2); 
    }
    
    if(user1 ==null) {
      user1 =  createUser(username1);
      //createDataUser(user1);            
    }    
    if(user2 ==null) {
      user2= createUser(username2) ;    
      //createDataUser(user2) ;            
    }
    
    userDefault = orgService_.getUserHandler().findUserByName(demo) ;
  }            
  protected void removeOrganizationData() throws Exception {    
    if(orgService_.getGroupHandler().findGroupById("/"+Group1) !=null) {      
      orgService_.getGroupHandler().removeGroup(group1,true) ;
      group1 = null ;
    }        
    if(orgService_.getGroupHandler().findGroupById("/"+Group2) !=null) {      
      orgService_.getGroupHandler().removeGroup(group2,true) ;
      group2 = null ;
    }
    
    if(orgService_.getMembershipTypeHandler().findMembershipType(memtype1)!= null) {      
      orgService_.getMembershipTypeHandler().removeMembershipType(memtype1,true) ;
      mType1 = null ;
    }    
    if(orgService_.getMembershipTypeHandler().findMembershipType(memtype2)!= null) {      
      orgService_.getMembershipTypeHandler().removeMembershipType(memtype2,true) ;
      mType2 = null ;
    }
    
    if(orgService_.getUserHandler().findUserByName(username1) != null) {      
      orgService_.getUserHandler().removeUser(username1,true) ;
      user1 = null ;
    }    
    if(orgService_.getUserHandler().findUserByName(username2) != null) {      
      orgService_.getUserHandler().removeUser(username2,true) ;
      user2 = null ;
    }                        
  }
  
  protected Group createGroup(String groupName) throws Exception {   
    Group savedGroup = orgService_.getGroupHandler().findGroupById("/"+groupName);
    if(savedGroup != null) return savedGroup;
    Group groupParent = orgService_.getGroupHandler().createGroupInstance() ;
    groupParent.setGroupName( groupName);
    groupParent.setDescription("This is description");    
    orgService_.getGroupHandler().addChild(null, groupParent, true);   
    return groupParent;
  }
  
  protected MembershipType createMembershipType(String name) throws Exception {
    MembershipType savedMt = orgService_.getMembershipTypeHandler().findMembershipType(name);
    if(savedMt != null) return savedMt;
    MembershipType mt = orgService_.getMembershipTypeHandler().createMembershipTypeInstance();
    mt.setName( name) ;
    mt.setDescription("This is a test") ;
    mt.setOwner("exo") ;     
    orgService_.getMembershipTypeHandler().createMembershipType(mt, true);
    return mt;
  }
  
  @SuppressWarnings("deprecation")
  protected User createUser(String userName) throws Exception {   
    User savedUser = orgService_.getUserHandler().findUserByName(userName);
    if(savedUser != null) return savedUser;
    User user = orgService_.getUserHandler().createUserInstance(userName) ;
    user.setPassword("default") ;
    user.setFirstName("default") ;
    user.setLastName("default") ;
    user.setEmail("exo@exoportal.org") ;
    orgService_.getUserHandler().createUser(user, true);
    return user ;
  }
  
  protected User createDataUser(User u) throws Exception {
    UserProfile up = orgService_.getUserProfileHandler().findUserProfileByName(u.getUserName());
    up.getUserInfoMap().put("user.gender", "male");
    orgService_.getUserProfileHandler().saveUserProfile(up, true);    
    return u;
  }
}
