/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 14, 2007  
 */

@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = @EventConfig(listeners = UISharedPortalResources.SaveActionListener.class)
)

public class UISharedPortalResources extends UIForm {
  List<SelectItemOption<String>> listOption = new ArrayList<SelectItemOption<String>>();
  
  @SuppressWarnings("unchecked")
  public UISharedPortalResources() throws Exception {
    for(int i = 1; i <= 10; i++ ) {
      listOption.add(new SelectItemOption(new Integer(i).toString()));
    }
    
    addUIFormInput(new UIFormStringInput("portalResourceName", "portalResourceName", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormSelectBox("priority","priority", listOption));
  }
  
  public String getPortalResourceName() { return getUIStringInput("portalResourceName").getValue(); }
  public String getPriority() { return getUIFormSelectBox("priority").getValue(); }
  
  static  public class SaveActionListener extends EventListener<UISharedPortalResources> {
    public void execute(Event<UISharedPortalResources> event) throws Exception {
      UISharedPortalResources uiForm = event.getSource() ;
      
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UserPortalConfigService userPortalConfigService = uiForm.getApplicationComponent(UserPortalConfigService.class);
      String portalResourceName = uiForm.getPortalResourceName();
      UIGroupInfo uiGroupInfo = uiForm.getParent();
      UIUserInGroup uiUserInGroup = uiGroupInfo.getChild(UIUserInGroup.class);
      Group selectedGroup = uiUserInGroup.getSelectedGroup();
      
      String selectedGroupId ;
      if(selectedGroup == null) {
        UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UISharedPortalResources.msg.notSelected", null)) ;
        Util.getPortalRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
        return ;
      } 
      selectedGroupId = selectedGroup.getId() ;
      
      Query<Page> query = new Query<Page>(null, null, null, Page.class) ;
      
      query.setOwnerType(PortalConfig.GROUP_TYPE) ;
      query.setOwnerId(portalResourceName) ;
      
      DataStorage dataStorage = uiForm.getApplicationComponent(DataStorage.class) ;
      PageList pagelist = dataStorage.find(query) ;
      
      int i = 1;
      while(i < pagelist.getAvailablePage()) {
        List<?>  list = pagelist.getPage(i);
        for(Object ele : list) {
          Page page  = (Page)ele;
          page.setOwnerType(PortalConfig.GROUP_TYPE);
          page.setOwnerId(selectedGroupId);
          if(dataStorage.getPage(page.getId()) == null) dataStorage.create(page);
        }
        i++;
      }
      
      PageNavigation navigation = dataStorage.getPageNavigation(PortalConfig.USER_TYPE + "::" + portalResourceName) ;
     
      navigation.setPriority(new Integer(uiForm.getPriority()));
      navigation.setOwnerType(PortalConfig.GROUP_TYPE);
      navigation.setOwnerId(selectedGroupId);
      if(dataStorage.getPageNavigation(navigation.getId()) != null) dataStorage.create(navigation);
    }
  }
}
