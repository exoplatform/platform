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
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.organization.UIUserMembershipSelector;
import org.exoplatform.webui.organization.UIUserProfileInputSet;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",
  events = {
    @EventConfig(listeners = UIUserInfo.SaveActionListener.class),
    @EventConfig(listeners = UIUserInfo.BackActionListener.class, phase = Phase.DECODE),
    @EventConfig(listeners = UIUserInfo.ToggleChangePasswordActionListener.class, phase = Phase.DECODE)
  }
)
public class UIUserInfo extends UIFormTabPane { 
  
  private String username_ = null ;
  
	public UIUserInfo() throws Exception {
    super("UIUserInfo");
    
    UIFormInputSet accountInputSet = new UIAccountEditInputSet("UIAccountEditInputSet") ;
    addChild(accountInputSet) ;
    setSelectedTab(accountInputSet.getId()) ;
    
    UIFormInputSet userProfileSet = new UIUserProfileInputSet("UIUserProfileInputSet") ;
    addChild(userProfileSet) ;    
    
    UIFormInputContainer<?> uiUserMembershipSelectorSet = new UIUserMembershipSelector();
    addChild(uiUserMembershipSelectorSet);
    
    setActions(new String[]{"Save", "Back"}) ;
  }
		
	public void setUser(String userName) throws Exception {
    username_ = userName ;
    OrganizationService service =  getApplicationComponent(OrganizationService.class);
    User user = service.getUserHandler().findUserByName(userName) ;
    
    getChild(UIAccountEditInputSet.class).setValue(user) ;
    getChild(UIUserProfileInputSet.class).setUserProfile(userName);
    
    UIUserMembershipSelector uiMembershipSelector = getChild(UIUserMembershipSelector.class);
    uiMembershipSelector.setUser(user);
  }
  
  public String getUserName() { return username_ ; } 
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    UIUserMembershipSelector uiUserMembershipSelector = getChild(UIUserMembershipSelector.class);    
    if(uiUserMembershipSelector == null) return;
    UIPopupWindow uiPopupWindow = uiUserMembershipSelector.getChild(UIPopupWindow.class);
    if(uiPopupWindow == null) return;
    uiPopupWindow.processRender(context);
  }
  
  static  public class SaveActionListener extends EventListener<UIUserInfo> {
    public void execute(Event<UIUserInfo> event) throws Exception {
      UIUserInfo uiUserInfo = event.getSource() ;
      OrganizationService service =  uiUserInfo.getApplicationComponent(OrganizationService.class);      
      boolean save = uiUserInfo.getChild(UIAccountEditInputSet.class).save(service) ; 
      if(!save) return;
      uiUserInfo.getChild(UIUserProfileInputSet.class).save(service, uiUserInfo.getUserName(), false) ;            
    }
  }
  
  static  public class BackActionListener extends EventListener<UIUserInfo> {
    public void execute(Event<UIUserInfo> event) throws Exception {
      UIUserInfo userInfo = event.getSource() ;
      UIUserManagement userManagement = userInfo.getParent() ;
      UIListUsers listUser = userManagement.getChild(UIListUsers.class) ;
      UIAccountEditInputSet accountInput = userInfo.getChild(UIAccountEditInputSet.class) ;
      UIUserProfileInputSet userProfile = userInfo.getChild(UIUserProfileInputSet.class) ;
      userInfo.setRenderSibbling(UIListUsers.class) ;
      listUser.search(new Query()) ;
      accountInput.reset() ;
      userProfile.reset() ;
      event.getRequestContext().setProcessRender(true) ;           
    }
  }
  static  public class ToggleChangePasswordActionListener extends EventListener<UIUserInfo> {
    public void execute(Event<UIUserInfo> event) throws Exception {
      UIUserInfo userInfo = event.getSource() ;
      UIAccountEditInputSet uiAccountInput = userInfo.getChild(UIAccountEditInputSet.class) ;
      uiAccountInput.checkChangePassword() ;
    }
  }
}
