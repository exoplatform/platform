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
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.DeleteNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.EditNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNavigationActionListener.SaveNavigationActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.AddNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.CopyNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.CutNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.DeleteNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditPageNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.EditSelectedNodeActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.MoveDownActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.MoveUpActionListener;
import org.exoplatform.portal.webui.navigation.UIPageNodeActionListener.PasteNodeActionListener;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDropDownControl;
import org.exoplatform.webui.core.UIRightClickPopupMenu;
import org.exoplatform.webui.core.UITree;
import org.exoplatform.webui.core.UIWizard;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.form.validator.PageNodeNameValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Oct 31, 2006  
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template = "app:/groovy/portal/webui/page/UIWizardPageSetInfo.gtmpl",
      events = @EventConfig(listeners = UIWizardPageSetInfo.ChangeNodeActionListener.class, phase=Phase.DECODE)
  ),
  @ComponentConfig(
      id = "WizardPageNodeSelector",
      type = UIPageNodeSelector.class,
      template = "app:/groovy/portal/webui/navigation/UIPageNodeSelector.gtmpl" ,
      events = {
         @EventConfig(listeners = UIWizardPageSetInfo.SelectNavigationActionListener.class, phase=Phase.DECODE) 
      }
  ),
  @ComponentConfig(
      id = "PageNodePopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = EditPageNodeActionListener.class),
        @EventConfig(listeners = EditSelectedNodeActionListener.class),
        @EventConfig(listeners = CopyNodeActionListener.class),
        @EventConfig(listeners = CutNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class),
        @EventConfig(listeners = MoveUpActionListener.class),
        @EventConfig(listeners = MoveDownActionListener.class),
        @EventConfig(listeners = DeleteNodeActionListener.class, confirm = "UIPageNodeSelector.deleteNavigation")
      }
  ),
  @ComponentConfig(
      id = "UIPageNodeSelectorPopupMenu",
      type = UIRightClickPopupMenu.class,
      template = "system:/groovy/webui/core/UIRightClickPopupMenu.gtmpl",
      events = {
        @EventConfig(listeners = AddNodeActionListener.class),
        @EventConfig(listeners = PasteNodeActionListener.class),
        @EventConfig(listeners = SaveNavigationActionListener.class),
        @EventConfig(listeners = EditNavigationActionListener.class),
        @EventConfig(listeners = DeleteNavigationActionListener.class, confirm = "UIPageNodeSelector.deleteNode")
      }
  ),  
  @ComponentConfig (
      type = UIDropDownControl.class ,
      id = "UIDropDown",
      template = "system:/groovy/webui/core/UIDropDownControl.gtmpl",
      events = {
        @EventConfig(listeners = UIPageNodeSelector.SelectNavigationActionListener.class)
      }
    )
})
public class UIWizardPageSetInfo extends UIForm {   

  final private static String PAGE_NAME = "pageName" ;
  final private static String PAGE_DISPLAY_NAME = "pageDisplayName" ;
  private boolean isEditMode = false;
  private boolean firstTime = true;
  
  public UIWizardPageSetInfo() throws Exception {
    UIPageNodeSelector  uiPageNodeSelector = addChild(UIPageNodeSelector.class, "WizardPageNodeSelector", null);    
    addUIFormInput(new UIFormStringInput(PAGE_NAME, "name", null).
                       addValidator(EmptyFieldValidator.class).
                       addValidator(IdentifierValidator.class).
                       addValidator(StringLengthValidator.class, 3, 30)
                       .addValidator(PageNodeNameValidator.class));
    addUIFormInput(new UIFormStringInput(PAGE_DISPLAY_NAME, "label", null));
    
    UITree uiTree = uiPageNodeSelector.getChild(UITree.class);
    uiTree.setUIRightClickPopupMenu(null);
    uiPageNodeSelector.removeChild(UIRightClickPopupMenu.class);    
  } 

  public void setEditMode() throws Exception {
    isEditMode = true ;
    UIFormStringInput uiNameInput = getChildById(PAGE_NAME) ;
    uiNameInput.setEditable(false) ;   
  }
  
  public boolean isEditMode() { return isEditMode; }
  
  public PageNode getPageNode() throws Exception {
    if(isEditMode) {
      PageNode pageNode = getSelectedPageNode() ;  
      invokeSetBindingBean(pageNode);
      if(pageNode.getLabel() == null || pageNode.getLabel().trim().length() == 0) {
        pageNode.setLabel(pageNode.getName());
      }
      return pageNode;
    }
    
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
    String user = context.getRemoteUser();
    if(user == null) user = Util.getUIPortal().getOwner();
    PageNode pageNode  = new PageNode();
    invokeSetBindingBean(pageNode);
    if(pageNode.getLabel() == null || pageNode.getLabel().trim().length() == 0) {
      pageNode.setLabel(pageNode.getName());   
    }
    
    UIPageNodeSelector uiNodeSelector = getChild(UIPageNodeSelector.class);
    PageNode selectedNode = uiNodeSelector.getSelectedPageNode();
    if(selectedNode != null) {
      pageNode.setUri(selectedNode.getUri()+"/"+pageNode.getName());
    } else pageNode.setUri(pageNode.getName()) ;
    return pageNode;
 }
  
  public PageNode getSelectedPageNode() {    
    UIPageNodeSelector uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    return uiPageNodeSelector.getSelectedPageNode(); 
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    if(isEditMode && getChild(UIPageNodeSelector.class).getSelectedPageNode() == null) reset() ;
    super.processRender(context) ;
  }
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    String action = context.getRequestParameter(UIForm.ACTION);
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;   
    if(event != null) event.broadcast() ;   
  }
  
  public boolean isFirstTime() {
    return firstTime;
  }
  
  public void setFirstTime(boolean firstTime){
    this.firstTime = firstTime;
  }
  
  static public class SelectNavigationActionListener  extends EventListener<UIPageNodeSelector> {
    public void execute(Event<UIPageNodeSelector> event) throws Exception {
      String id = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPageNodeSelector uiPageNodeSelector = event.getSource();
      UIWizard uiWizard = uiPageNodeSelector.getAncestorOfType(UIWizard.class);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWizard);
      if(id != null) uiPageNodeSelector.selectNavigation(id);
    }
  }
  
  static public class ChangeNodeActionListener  extends EventListener<UIWizardPageSetInfo> {
    public void execute(Event<UIWizardPageSetInfo> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIWizardPageSetInfo uiForm = event.getSource() ;
      
      UIPageNodeSelector uiPageNodeSelector = event.getSource().getChild(UIPageNodeSelector.class) ; 
      UITree tree = uiPageNodeSelector.getChild(UITree.class) ;
    
      if(tree.getParentSelected() == null && (uri == null || uri.length() < 1)){
        uiPageNodeSelector.selectNavigation(uiPageNodeSelector.getSelectedNavigation().getId()) ;
      } else {
        uiPageNodeSelector.selectPageNodeByUri(uri) ;
      }
     
      UIPortalApplication uiPortalApp = uiPageNodeSelector.getAncestorOfType(UIPortalApplication.class) ;
      UIWizard uiWizard = uiPortalApp.findFirstComponentOfType(UIWizard.class) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWizard) ;
      
      
      if(!event.getSource().isEditMode()) {
        return  ;
      }
      PageNode pageNode = uiPageNodeSelector.getSelectedPageNode() ;

      if(pageNode == null && uiForm.isFirstTime()) {
        uiForm.setFirstTime(false) ;
        UIPortal uiPortal = Util.getUIPortal() ;
        uiPageNodeSelector.selectNavigation(uiPortal.getSelectedNavigation().getId()) ;
        uiPageNodeSelector.selectPageNodeByUri(uiPortal.getSelectedNode().getUri()) ;
        pageNode = uiPageNodeSelector.getSelectedPageNode() ;
      }
      
      if(pageNode == null) return ;
      UserPortalConfigService configService = uiWizard.getApplicationComponent(UserPortalConfigService.class) ;
      String accessUser = event.getRequestContext().getRemoteUser() ;
      Page page = configService.getPage(pageNode.getPageReference(), accessUser) ;
      if(page == null){
        uiPortalApp.addMessage(new ApplicationMessage("UIWizardPageSetInfo.msg.null", null)) ;
        event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
        uiForm.reset() ;
        return ;
      }
      String pageName = pageNode.getPageReference().split("::")[2] ;
      UIFormStringInput uiNameInput = uiForm.getChildById(PAGE_NAME) ;
      if(pageNode.getName() != null) uiNameInput.setValue(pageName) ;
      UIFormStringInput uiDisplayNameInput = uiForm.getChildById(PAGE_DISPLAY_NAME) ;
      if(pageNode.getLabel() != null) uiDisplayNameInput.setValue(pageNode.getResolvedLabel()) ;
    }
  }
}
