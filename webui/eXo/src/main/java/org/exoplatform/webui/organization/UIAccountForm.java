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
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormInputWithActions;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormInputWithActions.ActionData;
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
          @EventConfig(listeners = UIAccountForm.ResetActionListener.class, phase = Phase.DECODE),
          @EventConfig(listeners = UIAccountForm.SearchUserActionListener.class, phase = Phase.DECODE)
        }
    )
public class UIAccountForm extends UIFormTabPane {
  
  public UIAccountForm(InitParams initParams) throws Exception {
    super("UIAccountForm") ;
    UIFormInputWithActions accountInputSet = new UIAccountInputSet("AccountInputSet") ;
    List<ActionData> actions = new ArrayList<ActionData>() ;
    ActionData addCategory = new ActionData() ;
    addCategory.setActionListener("SearchUser") ;
    addCategory.setActionType(ActionData.TYPE_ICON) ;
    addCategory.setActionName("SearchUser") ;
    addCategory.setCssIconClass("SearchIcon") ;
    actions.add(addCategory) ;
    accountInputSet.setActionField("username", actions) ;
    setSelectedTab(accountInputSet.getId()) ;
    addChild(accountInputSet) ;
    UIFormInputSet userProfileSet = new UIUserProfileInputSet("UIUserProfileInputSet") ;
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
      ActionResponse actResponse = event.getRequestContext().getResponse() ;
      actResponse.setEvent(new QName("NewAccountAdded"), null) ;
    }
  } 
  
  static  public class ResetActionListener extends EventListener<UIAccountForm> {
    public void execute(Event<UIAccountForm> event) throws Exception {
      UIAccountForm uiForm = event.getSource();
      uiForm.reset() ;
    }
  }
  
  static  public class SearchUserActionListener extends EventListener<UIAccountForm> {
    public void execute(Event<UIAccountForm> event) throws Exception {
      UIAccountForm uiForm = event.getSource();
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
      UIApplication uiApp = context.getUIApplication() ;
      OrganizationService service =  uiForm.getApplicationComponent(OrganizationService.class);
      String userName = uiForm.getChild(UIAccountInputSet.class).getUserName() ;
      if(userName==null) {
        uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.empty-input", null, ApplicationMessage.WARNING)) ;
        return;
      }
      if(service.getUserHandler().findUserByName(userName) != null) {
        uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.user-exist", null, ApplicationMessage.WARNING)) ;
        return;
      }
      uiApp.addMessage(new ApplicationMessage("UIAccountInputSet.msg.user-not-exist", null, ApplicationMessage.WARNING)) ;
    }
  }
}