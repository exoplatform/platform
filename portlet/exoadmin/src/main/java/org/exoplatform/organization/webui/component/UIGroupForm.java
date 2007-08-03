/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;


import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 27, 2006
 * 8:48:47 AM 
 */
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = UIGroupForm.SaveActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UIGroupForm.BackActionListener.class)      
  }
)
public class UIGroupForm extends UIForm {
  
  private Group group_;
  private String componentName_ = "AddGroup";
  private static String GROUP_NAME = "groupName",
                        GROUP_LABEL = "label",
                        GROUP_DESCRIPSION = "description" ;
  
  public UIGroupForm() throws Exception {
    addUIFormInput(new UIFormStringInput(GROUP_NAME, GROUP_NAME, null).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(IdentifierValidator.class));
    addUIFormInput(new UIFormStringInput(GROUP_LABEL, GROUP_LABEL, null)) ;
    addUIFormInput(new UIFormTextAreaInput(GROUP_DESCRIPSION,GROUP_DESCRIPSION,null));    
  }
  
  public Group getGroup() { return group_; }
  
  public void setGroup(Group group) throws Exception { 
    this.group_ = group;
    if(group_ == null){
      getUIStringInput(GROUP_NAME).setEditable(UIFormStringInput.ENABLE) ;
      reset() ;
      return ;
    }
    getUIStringInput(GROUP_NAME).setEditable(UIFormStringInput.DISABLE) ;
    invokeGetBindingBean(group_);
  }
  
  public String getName() { return componentName_; }
  
  public void setName(String componentName) { componentName_ = componentName; }
  
  static  public class SaveActionListener extends EventListener<UIGroupForm> {
    public void execute(Event<UIGroupForm> event) throws Exception {
      UIGroupForm uiGroupForm = event.getSource() ;
      UIGroupDetail uiGroupDetail = uiGroupForm.getParent() ;
      UIGroupManagement uiGroupManagement = uiGroupDetail.getParent() ;
      OrganizationService service = uiGroupForm.getApplicationComponent(OrganizationService.class) ;
      UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class) ;
      
      Group currentGroup =  uiGroupForm.getGroup();
      if(currentGroup != null) {
        uiGroupForm.invokeSetBindingBean(currentGroup);
        if(currentGroup.getLabel() == null || currentGroup.getLabel().trim().length() == 0) {
          currentGroup.setLabel(currentGroup.getGroupName()) ;
        }
        service.getGroupHandler().saveGroup(currentGroup, false);
        uiGroupForm.reset();
        uiGroupForm.setGroup(null);
        uiGroupExplorer.changeGroup(currentGroup.getId()) ;
        uiGroupForm.setRenderSibbling(UIGroupInfo.class) ;
        return ;
      }    
      
      //UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class) ;      
      String currentGroupId = null ;
      currentGroup =  uiGroupExplorer.getCurrentGroup() ;
      if(currentGroup != null) currentGroupId = currentGroup.getId() ;
      String groupName = "/"+ uiGroupForm.getUIStringInput(GROUP_NAME).getValue() ;
      
      GroupHandler groupHandler = service.getGroupHandler() ;
      
      if(currentGroupId != null) groupName = currentGroupId + groupName ;
      
      Group newGroup = groupHandler.findGroupById(groupName);
      if(newGroup != null) {
        Object[]  args = {"GroupName", groupName } ;
        UIApplication uiApp = event.getRequestContext().getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIGroupForm.msg.group-exist", args)) ;
        return ;
      }
      newGroup = groupHandler.createGroupInstance();
      uiGroupForm.invokeSetBindingBean(newGroup) ;
      if(newGroup.getLabel() == null || newGroup.getLabel().trim().length() == 0) {
        newGroup.setLabel(newGroup.getGroupName()) ;
      }
      if(currentGroupId == null) {
        groupHandler.addChild(null, newGroup, true) ;
        uiGroupExplorer.changeGroup(groupName) ;
      } else {
        Group parrentGroup = groupHandler.findGroupById(currentGroupId) ;
        groupHandler.addChild(parrentGroup, newGroup, true) ;
        uiGroupExplorer.changeGroup(currentGroupId) ;
      }      
      uiGroupForm.reset();
      uiGroupForm.setGroup(null);
      uiGroupForm.setRenderSibbling(UIGroupInfo.class) ;
    }    
  }
  
  static  public class BackActionListener extends EventListener<UIGroupForm> {
    public void execute(Event<UIGroupForm> event) throws Exception {
      UIGroupForm uiGroupForm = event.getSource() ;
      uiGroupForm.reset();
      uiGroupForm.setGroup(null);
      uiGroupForm.setRenderSibbling(UIGroupInfo.class) ;
      event.getRequestContext().setProcessRender(true) ;
    }
  }
  
}
