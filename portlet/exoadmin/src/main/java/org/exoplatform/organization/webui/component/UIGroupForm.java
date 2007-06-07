/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;


import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.component.UIApplication;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputBase;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.IdentifierValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.organization.webui.component.UIGroupForm.*;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 27, 2006
 * 8:48:47 AM 
 */
@ComponentConfig(
  lifecycle = UIFormLifecycle.class,
  template = "system:/groovy/webui/component/UIFormWithTitle.gtmpl",
  events = {
    @EventConfig(listeners = SaveActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = BackActionListener.class)      
  }
)
public class UIGroupForm extends UIForm {
  
  private Group group_;
  private String componentName_ = "AddGroup";
  
  public UIGroupForm() throws Exception {
    addUIFormInput(new UIFormStringInput("groupName", "groupName", null).
                   addValidator(EmptyFieldValidator.class).
                   addValidator(IdentifierValidator.class));
    addUIFormInput(new UIFormStringInput("label", "label", null).
                   addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormTextAreaInput("description","description",null));    
  }
  
  public Group getGroup() { return group_; }
  
  public void setGroup(Group group) throws Exception { 
    this.group_ = group;    
    this.<UIFormStringInput>getUIInput("groupName").setValue("");
    this.<UIFormStringInput>getUIInput("label").setValue("");
    this.<UIFormTextAreaInput>getUIInput("description").setValue("");    
    if(group != null) invokeGetBindingBean(group);
  }
  
  public String getName() { return componentName_; }
  
  public void setName(String componentName) { componentName_ = componentName; }
  
  //TODO: Tung.Pham added
  public void setEditableAll() {
    for (UIComponent component : getChildren()) {
      if (component  instanceof UIFormInputBase<?>)  {
        ((UIFormInputBase<?>)component).setEditable(true) ;
      }
    }
  }
  
  static  public class SaveActionListener extends EventListener<UIGroupForm> {
    public void execute(Event<UIGroupForm> event) throws Exception {
      UIGroupForm uiGroupForm = event.getSource() ;
      UIGroupDetail uiGroupDetail = uiGroupForm.getParent() ;
      UIGroupManagement uiGroupManagement = uiGroupDetail.getParent() ;
      OrganizationService service = uiGroupForm.getApplicationComponent(OrganizationService.class) ;     
      
      Group currentGroup =  uiGroupForm.getGroup();
      if(currentGroup != null) {
        uiGroupForm.invokeSetBindingBean(currentGroup);
        service.getGroupHandler().saveGroup(currentGroup, false);
        return ;
      }    
      
      UIGroupExplorer uiGroupExplorer = uiGroupManagement.getChild(UIGroupExplorer.class) ;      
      String currentGroupId = null ;
      currentGroup =  uiGroupExplorer.getCurrentGroup() ;
      if(currentGroup != null) currentGroupId = currentGroup.getId() ;
      String groupName = "/"+ uiGroupForm.getUIStringInput("groupName").getValue() ;
      
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
      if(currentGroupId == null) {
        groupHandler.addChild(null, newGroup, true) ;
        uiGroupExplorer.changeGroup(groupName) ;
      }else {
        Group parrentGroup = groupHandler.findGroupById(currentGroupId) ;
        groupHandler.addChild(parrentGroup, newGroup, true) ;
        uiGroupExplorer.changeGroup(currentGroupId) ;
      }      
      uiGroupForm.reset();
    }    
  }
  
  static  public class BackActionListener extends EventListener<UIGroupForm> {
    public void execute(Event<UIGroupForm> event) throws Exception {
      UIGroupForm uiGroupForm = event.getSource() ;
      uiGroupForm.reset();
      uiGroupForm.setRenderSibbling(UIGroupInfo.class) ;
      event.getRequestContext().setProcessRender(true) ;
    }
  }
  
}
