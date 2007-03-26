/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.component.UIWizard;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Oct 31, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "app:/groovy/portal/webui/component/customization/UIWizardPageSetInfo.gtmpl",
    events = @EventConfig(
      listeners = UIWizardPageSetInfo.ChangeNodeActionListener.class, phase=Phase.DECODE
    )
)
public class UIWizardPageSetInfo extends UIForm {   
  
  private boolean isEdit = false;
  
  public UIWizardPageSetInfo() throws Exception {
    UIPageNodeSelector  uiPageNodeSelector = addChild(UIPageNodeSelector.class, null, null);    
    addUIFormInput(new UIFormStringInput("pageName", "pageName", null).addValidator(EmptyFieldValidator.class));
    addUIFormInput(new UIFormTextAreaInput("description","description",null));
    
    UITree uiTree = uiPageNodeSelector.getChild(UITree.class);
    uiTree.setUIRightClickPopupMenu(null);
  } 
  
  public void setEditPageNode(boolean value){
    isEdit = value;
    if(!value) return;
    UIPageNodeSelector  uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    if(uiPageNodeSelector.getSelectedPageNode() != null) return;
    PageNode pageNode = Util.getUIPortal().getSelectedNode();
    uiPageNodeSelector.selectPageNodeByUri(pageNode.getUri()) ; 
    UIFormStringInput uiNameInput = getChild(UIFormStringInput.class);
    uiNameInput.setEditable(false);
    UIFormTextAreaInput uiDesInput = getChild(UIFormTextAreaInput.class);
    if(pageNode.getName() != null) uiNameInput.setValue(pageNode.getName());
    if(pageNode.getDescription() != null) uiDesInput.setValue(pageNode.getDescription());
  }
  
  public PageNode getPageNode() {
    if(isEdit) return getSelectedPageNode() ;
    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    String user = context.getRemoteUser();
    if(user == null) user = Util.getUIPortal().getOwner();
    String name = this.<UIFormStringInput>getUIInput("pageName").getValue();
    String des = this.<UIFormTextAreaInput>getUIInput("description").getValue();
    
    PageNode pageNode  = new PageNode();
    pageNode.setName(name);
    pageNode.setLabel(name);
    pageNode.setCreator(user);
    pageNode.setDescription(des);
    
    return pageNode;
 }
  
  public PageNode getSelectedPageNode() {    
    UIPageNodeSelector uip = getChild(UIPageNodeSelector.class);
    return uip.getSelectedPageNode(); 
  } 
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    String action = context.getRequestParameter(UIForm.ACTION);
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;   
    if(event != null) event.broadcast()  ;   
  }
  
  static public class ChangeNodeActionListener  extends EventListener<UIWizardPageSetInfo> {
    public void execute(Event<UIWizardPageSetInfo> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);        
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getChild(UIPageNodeSelector.class);      
      uiPageNodeSelector.selectPageNodeByUri(uri);
      
      UIPortalApplication uiPortalApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class);
      UIWizard uiWizard = uiPortalApp.findFirstComponentOfType(UIWizard.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWizard);
      
      if(!event.getSource().isEdit) return ;
      PageNode pageNode = uiPageNodeSelector.getSelectedPageNode();
      UIFormStringInput uiNameInput = event.getSource().getChild(UIFormStringInput.class);
      UIFormTextAreaInput uiDesInput = event.getSource().getChild(UIFormTextAreaInput.class);
      if(pageNode.getName() != null) uiNameInput.setValue(pageNode.getName());
      if(pageNode.getDescription() != null) uiDesInput.setValue(pageNode.getDescription());
    }
  }
 
}
