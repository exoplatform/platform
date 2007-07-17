/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.organization;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormTabPane;
/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 28, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIFormTabPane.gtmpl",
    initParams = {   
      @ParamConfig(
          name = "AccountTemplateConfigOption", 
          value = "app:/WEB-INF/conf/uiconf/account/webui/component/model/AccountTemplateConfigOption.groovy"
      ),
      @ParamConfig(
          name = "help.UIAccountFormQuickHelp",
          value = "app:/WEB-INF/conf/uiconf/account/webui/component/model/UIAccountFormQuickHelp.xhtml"
      )
    },
    events = {
      @EventConfig(listeners = UIAccountForm.SaveActionListener.class ),
      @EventConfig(listeners = UIAccountForm.ResetActionListener.class, phase = Phase.DECODE)
      //,@EventConfig(listeners = UIAccountForm.SelectItemOptionActionListener.class, phase = Phase.DECODE)
    }
)
public class UIAccountForm extends UIFormTabPane {
  
  @SuppressWarnings("unchecked")
  public UIAccountForm(InitParams initParams) throws Exception {
    super("UIAccountForm") ;
    
//    UIFormInputItemSelector templateInput = new  UIFormInputItemSelector("AccountTemplate", null);    
//    addUIFormInput(templateInput) ;
    UIFormInputSet accountInputSet = new UIAccountInputSet("AccountInputSet") ;
//    accountInputSet.setRendered(false) ;
    addUIFormInput(accountInputSet) ;

    UIFormInputSet userProfileSet = new UIUserProfileInputSet("UIUserProfileInputSet") ;
    userProfileSet.setRendered(false) ;
    addUIFormInput(userProfileSet) ;
    if(initParams == null) return ;  
    
//    UIUserMembershipSelector uiUserMembershipSelector = new UIUserMembershipSelector();
//    uiUserMembershipSelector.setRendered(false);
//    addUIFormInput(uiUserMembershipSelector);
//    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
//    boolean isRoleAdmin = context.isUserInRole("admin"); 
//    
//    Param param = initParams.getParam("AccountTemplateConfigOption");
//    List<SelectItemCategory> itemConfigs = (List<SelectItemCategory>)param.getMapGroovyObject(context);
//    for(SelectItemCategory itemCategory: itemConfigs){
//      if(!"AdminAccount".equalsIgnoreCase(itemCategory.getName()) || isRoleAdmin){
//        templateInput.getItemCategories().add(itemCategory);
//      }
//    }
//    uiUserMembershipSelector.setAdminRole(isRoleAdmin);
//    
//    if(templateInput.getSelectedItemOption() == null) {
//      templateInput.getItemCategories().get(0).setSelected(true);
//    }
    setActions(new String[]{"Save", "Reset"});
  }

  public String getSelectPortalTemplate(){  return "SelectPortalTemplate";  }
  
  //TODO: Tung.Pham added
  public void reset() {
    getChild(UIAccountInputSet.class).reset() ;  
    getChild(UIUserProfileInputSet.class).reset();
  }
  
//  public void processRender(WebuiRequestContext context) throws Exception {
//    super.processRender(context);
//    UIUserMembershipSelector uiUserMembershipSelector = getChild(UIUserMembershipSelector.class);    
//    if(uiUserMembershipSelector == null) return;
//    UIPopupWindow uiPopupWindow = uiUserMembershipSelector.getChild(UIPopupWindow.class);
//    if(uiPopupWindow == null) return;
//    uiPopupWindow.processRender(context);
//  }

  static  public class SaveActionListener extends EventListener<UIAccountForm> {
    public void execute(Event<UIAccountForm> event) throws Exception {
      UIAccountForm uiForm = event.getSource();
      OrganizationService service =  uiForm.getApplicationComponent(OrganizationService.class);
      UIAccountInputSet uiAccountInput = uiForm.getChild(UIAccountInputSet.class) ;  
      String userName = uiAccountInput.getUserName();
      boolean saveAccountInput = uiAccountInput.save(service, true);
      if(saveAccountInput == false) return;
      uiForm.getChild(UIUserProfileInputSet.class).save(service, userName, true);
      uiForm.reset() ;
//      UIUserMembershipSelector uiMembershipSelector = uiForm.getChild(UIUserMembershipSelector.class);
//      if(uiMembershipSelector == null) return ;
//      uiMembershipSelector.setUserName(userName);
//      uiMembershipSelector.save(service, true);     
    }
  } 
  
  static  public class ResetActionListener extends EventListener<UIAccountForm> {
    public void execute(Event<UIAccountForm> event) throws Exception {
      UIAccountForm uiForm = event.getSource();
      //TODO: Tung.Pham modified
      //--------------------------
//      uiForm.getChild(UIAccountInputSet.class).reset() ;  
//      uiForm.getChild(UIUserProfileInputSet.class).reset();
      uiForm.reset() ;
      //--------------------------
//      UIUserMembershipSelector uiMembershipSelector = uiForm.getChild(UIUserMembershipSelector.class);
//      if(uiMembershipSelector == null) return ;
//      uiMembershipSelector.reset();
    }
  }
  
//  static  public class SelectItemOptionActionListener extends EventListener<UIAccountForm> {
//    public void execute(Event<UIAccountForm> event) throws Exception {
//      UIAccountForm uiForm = event.getSource();
//      UIFormInputItemSelector templateInput = uiForm.getChild(UIFormInputItemSelector.class);
//      UIAccountTemplateConfigOption selectItem = 
//        (UIAccountTemplateConfigOption)templateInput.getSelectedCategory().getSelectItemOptions().get(0);      
//      List<Membership> memberships = selectItem.getMemberships();      
//      UIUserMembershipSelector uiMembershipSelector =  uiForm.getChild(UIUserMembershipSelector.class);
//      uiMembershipSelector.getMembership().clear();
//      for(Membership mem : memberships){
//        uiMembershipSelector.addMembership(mem);
//      }
//    }
//  }

}