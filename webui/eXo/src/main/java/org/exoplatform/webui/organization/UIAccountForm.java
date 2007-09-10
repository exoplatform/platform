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
    }
)
public class UIAccountForm extends UIFormTabPane {
  
  @SuppressWarnings("unchecked")
  public UIAccountForm(InitParams initParams) throws Exception {
    super("UIAccountForm") ;
    UIFormInputSet accountInputSet = new UIAccountInputSet("AccountInputSet") ;
    addUIFormInput(accountInputSet) ;
    UIFormInputSet userProfileSet = new UIUserProfileInputSet("UIUserProfileInputSet") ;
    userProfileSet.setRendered(false) ;
    addUIFormInput(userProfileSet) ;
    if(initParams == null) return ;  
    
    setActions(new String[]{"Save", "Reset"});
  }

  public String getSelectPortalTemplate(){  return "SelectPortalTemplate";  }
  
  public void reset() {
    getChild(UIAccountInputSet.class).reset() ;  
    getChild(UIUserProfileInputSet.class).reset();
  }
  
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
    }
  } 
  
  static  public class ResetActionListener extends EventListener<UIAccountForm> {
    public void execute(Event<UIAccountForm> event) throws Exception {
      UIAccountForm uiForm = event.getSource();
      uiForm.reset() ;
    }
  }
}