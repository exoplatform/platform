/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormTabPane.gtmpl",
  events = {
    @EventConfig(listeners = UIUserInfo.SaveActionListener.class),
    @EventConfig(listeners = UIUserInfo.BackActionListener.class, phase = Phase.DECODE) 
  }
)
public class UIUserInfo extends UIFormTabPane { 
  
  private String username_ = null ;
  
	public UIUserInfo() throws Exception {
    super("UIUserInfo");
    
    UIFormInputSet accountInputSet = new UIAccountInputSet("AccountInputSet") ;
    addChild(accountInputSet) ;
    
    UIFormInputSet userProfileSet = new UIUserProfileInputSet("UIUserProfileInputSet") ;
    userProfileSet.setRendered(false) ;
    addChild(userProfileSet) ;    
    
    UIFormInputContainer userMembershipSelectorSet = new UIUserMembershipSelector();
    userMembershipSelectorSet.setRendered(false);
    addChild(userMembershipSelectorSet);
  }
		
	public void setUser(String userName) throws Exception {
    username_ = userName ;
    OrganizationService service =  getApplicationComponent(OrganizationService.class);
    User user = service.getUserHandler().findUserByName(userName) ;
    
    getChild(UIAccountInputSet.class).setValue(user) ;
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
      boolean save = uiUserInfo.getChild(UIAccountInputSet.class).save(service, false) ; 
      if(!save) return;
      uiUserInfo.getChild(UIUserProfileInputSet.class).save(service, uiUserInfo.getUserName(), false) ;      
      uiUserInfo.getChild(UIUserMembershipSelector.class).save(service, true);      
    }
  }
  
  static  public class BackActionListener extends EventListener<UIUserInfo> {
    public void execute(Event<UIUserInfo> event) throws Exception {
      UIUserInfo userInfo = event.getSource() ;
      UIUserManagement userManagement = userInfo.getParent() ;
      UIListUsers listUser = userManagement.getChild(UIListUsers.class) ;
      UIAccountInputSet accountInput = userInfo.getChild(UIAccountInputSet.class) ;
      UIUserProfileInputSet userProfile = userInfo.getChild(UIUserProfileInputSet.class) ;
      userInfo.setRenderSibbling(UIListUsers.class) ;
      listUser.search(new Query()) ;
      accountInput.reset() ;
      userProfile.reset() ;
      event.getRequestContext().setProcessRender(true) ;           
    }
  }
  
}
