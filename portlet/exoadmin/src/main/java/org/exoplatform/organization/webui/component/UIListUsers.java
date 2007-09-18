/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UISearch;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(
    lifecycle = UIContainerLifecycle.class,
    events = {
      @EventConfig(listeners = UIListUsers.ViewUserInfoActionListener.class),
      @EventConfig(listeners = UIListUsers.SelectUserActionListener.class),
      @EventConfig(listeners = UIListUsers.DeleteUserActionListener.class, confirm = "UIListUsers.deleteUser")
    }
)
public class UIListUsers extends UISearch {
  
  public static String USER_NAME = "userName";
  public static String LAST_NAME = "lastName";
  public static String FIRST_NAME = "firstName";
  public static String EMAIL = "email";
  
  private static String[] USER_BEAN_FIELD = {USER_NAME, LAST_NAME, FIRST_NAME, EMAIL} ;
  private static String[] USER_ACTION = {"ViewUserInfo", "DeleteUser"} ;  

  private static List<SelectItemOption<String>> OPTIONS_ = new ArrayList<SelectItemOption<String>>(4);
  static{
    OPTIONS_.add(new SelectItemOption<String>(USER_NAME, USER_NAME));
    OPTIONS_.add(new SelectItemOption<String>(LAST_NAME, LAST_NAME));
    OPTIONS_.add(new SelectItemOption<String>(FIRST_NAME, FIRST_NAME));
    OPTIONS_.add(new SelectItemOption<String>(EMAIL, EMAIL));
  }
  
  private Query lastQuery_ ;
  private String userSelected_;
  private UIGrid grid_;
  
	public UIListUsers() throws Exception {
		super(OPTIONS_) ;
		grid_ = addChild(UIGrid.class, null, "UIListUsersGird") ;
		grid_.configure(USER_NAME, USER_BEAN_FIELD, USER_ACTION) ;
    //TODO: Tung.Pham added
    //--------------------------------------------
		grid_.getUIPageIterator().setId("UIListUsersIterator") ;
		grid_.getUIPageIterator().setParent(this);
    //--------------------------------------------
		search(new Query()) ;
	}
  
  public void setUserSelected(String userName) {
    userSelected_ = userName;
  }

  public String getUserSelected() {
    
    return userSelected_;
  }
	public void search(Query query) throws Exception {
    lastQuery_ = query ;
//    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    //TODO: Tung.Pham modified
    //------------------------------------------------------------------
    //uiGrid.getUIPageIterator().setPageList(service.getUserHandler().findUsers(query)) ;
    PageList pageList = service.getUserHandler().findUsers(query) ;
    pageList.setPageSize(10) ;
    grid_.getUIPageIterator().setPageList(pageList) ;
    //------------------------------------------------------------------    
    UIPageIterator pageIterator = grid_.getUIPageIterator();
    if(pageIterator.getAvailable() == 0 ) {
      UIApplication uiApp = Util.getPortalRequestContext().getUIApplication() ;
      uiApp.addMessage(new ApplicationMessage("UISearchForm.msg.empty", null)) ;
      
      Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
    }
	}
  
  public void quickSearch(UIFormInputSet quickSearchInput) throws Exception {
    Query query = new Query();
    UIFormStringInput input = (UIFormStringInput) quickSearchInput.getChild(0);
    UIFormSelectBox select = (UIFormSelectBox) quickSearchInput.getChild(1);
    String name = input.getValue();
    if(name == null || name.equals("")) {
      search(new Query()) ;
      return ;
    }
    String selectBoxValue = select.getValue();
    if(selectBoxValue.equals(USER_NAME)) query.setUserName(name) ;
    if(selectBoxValue.equals(LAST_NAME)) query.setFirstName(name) ; 
    if(selectBoxValue.equals(FIRST_NAME)) query.setLastName(name) ;
    if(selectBoxValue.equals(EMAIL)) query.setEmail(name) ;
    search(query);
  }

  @SuppressWarnings("unused")
  public void advancedSearch(UIFormInputSet advancedSearchInput) throws Exception {
  }
  
	static  public class ViewUserInfoActionListener extends EventListener<UIListUsers> {
    public void execute(Event<UIListUsers> event) throws Exception {
    	String username = event.getRequestContext().getRequestParameter(OBJECTID) ;
      
    	UIListUsers uiListUsers = event.getSource();
      OrganizationService service = uiListUsers.getApplicationComponent(OrganizationService.class);
      if(service.getUserHandler().findUserByName(username) == null ) {
        uiListUsers.search(new Query()) ;
        return ;
      }
      
    	uiListUsers.setRendered(false);
    	UIUserManagement uiUserManager = uiListUsers.getParent();
    	UIUserInfo uiUserInfo = uiUserManager.getChild(UIUserInfo.class);
      uiUserInfo.setUser(username);
    	uiUserInfo.setRendered(true);
      
    	UIComponent uiToUpdateAjax = uiListUsers.getAncestorOfType(UIUserManagement.class) ;
    	event.getRequestContext().addUIComponentToUpdateByAjax(uiToUpdateAjax) ;
    }
  }
  
  static  public class DeleteUserActionListener extends EventListener<UIListUsers> {
    public void execute(Event<UIListUsers> event) throws Exception {
      UIListUsers uiListUser = event.getSource() ;
      String userName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      OrganizationService service = uiListUser.getApplicationComponent(OrganizationService.class) ;
      //TODO: Tung.Pham added
      //------------------------------------
      UserACL userACL = uiListUser.getApplicationComponent(UserACL.class) ;
      if(userACL.getSuperUser().equals(userName)) {
        UIApplication uiApp = event.getRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIListUsers.msg.DeleteSuperUser", new String[] {userName},ApplicationMessage.WARNING)) ;
        return ;
      }
      //------------------------------------
      service.getUserHandler().removeUser(userName, true) ;
      uiListUser.search(uiListUser.lastQuery_);
      
      UIComponent uiToUpdateAjax = uiListUser.getAncestorOfType(UIUserManagement.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiToUpdateAjax) ;
    }
  }
  
  static  public class SelectUserActionListener extends EventListener<UIListUsers> {
    public void execute(Event<UIListUsers> event) throws Exception {
      UIListUsers uiListUser = event.getSource() ;
      String userName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIPopupWindow popup = uiListUser.getAncestorOfType(UIPopupWindow.class);
      popup.setShow( false);
      UIGroupMembershipForm groupMembershipForm = popup.getParent();
      groupMembershipForm.setUserName(userName);
      event.getRequestContext().addUIComponentToUpdateByAjax(groupMembershipForm);
    }
  }
}
