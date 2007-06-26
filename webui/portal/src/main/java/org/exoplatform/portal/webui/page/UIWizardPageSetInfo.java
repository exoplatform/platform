/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
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

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Thi Hoa
 *          hoa.nguyen@exoplatform.com
 * Oct 31, 2006  
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "app:/groovy/portal/webui/page/UIWizardPageSetInfo.gtmpl",
    events = @EventConfig(listeners = UIWizardPageSetInfo.ChangeNodeActionListener.class, phase=Phase.DECODE)
)
public class UIWizardPageSetInfo extends UIForm {   
  final private static String PAGE_NAME = "pageName" ;
  final private static String PAGE_DISPLAY_NAME = "pageDisplayName" ;
  private boolean isEdit = false;
  
  public UIWizardPageSetInfo() throws Exception {
    UIPageNodeSelector  uiPageNodeSelector = addChild(UIPageNodeSelector.class, null, null);    
    addUIFormInput(new UIFormStringInput(PAGE_NAME, "name", null).addValidator(EmptyFieldValidator.class).addValidator(IdentifierValidator.class));
    addUIFormInput(new UIFormStringInput(PAGE_DISPLAY_NAME, "label", null));
    
    UITree uiTree = uiPageNodeSelector.getChild(UITree.class);
    uiTree.setUIRightClickPopupMenu(null);
    uiPageNodeSelector.removeChild(UIRightClickPopupMenu.class);    
  } 
  
  public void setEditPageNode(boolean value) throws Exception {
    isEdit = value;
    if(!value) return;
    UIPageNodeSelector  uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    uiPageNodeSelector.loadSelectedNavigation();
    UIFormStringInput uiNameInput = getChildById(PAGE_NAME) ;
    UIFormStringInput uiDisplayNameInput = getChildById(PAGE_DISPLAY_NAME) ;
    //TODO: Tung.Pham modified
    //----------------------------------------------------------------
    //if(uiPageNodeSelector.getSelectedPageNode() == null) return;
    //PageNode pageNode = uiPageNodeSelector.getSelectedPageNode();
    //uiPageNodeSelector.selectPageNodeByUri(pageNode.getUri()) ;
    uiNameInput.setEditable(false) ;
    PageNode pageNode = uiPageNodeSelector.getSelectedPageNode() ;
    if (pageNode == null) return ;
    //invokeGetBindingBean(pageNode);
    String pageName = pageNode.getPageReference().split("::")[2] ;
    if(pageNode.getName() != null) uiNameInput.setValue(pageName);
    if(pageNode.getLabel() != null) uiDisplayNameInput.setValue(pageNode.getLabel());
    //---------------------------------------------------------------- 
  }
  
  public PageNode getPageNode() throws Exception {
    if(isEdit) {
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
    //PageNavigation pageNav =  uiNodeSelector.getSelectedNavigation();    
    if(selectedNode != null) {
      pageNode.setUri(selectedNode.getUri()+"/"+pageNode.getName());
    } else {       
      //TODO: Tung.Pham modified
      //--------------------------------------------------------------------
      //pageNode.setUri(pageNav.getOwnerId() + "::" + pageNode.getName());
      pageNode.setUri(pageNode.getName()) ;
      //--------------------------------------------------------------------
    }
    return pageNode;
 }
  
  public PageNode getSelectedPageNode() {    
    UIPageNodeSelector uiPageNodeSelector = getChild(UIPageNodeSelector.class);
    return uiPageNodeSelector.getSelectedPageNode(); 
  } 
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    String action = context.getRequestParameter(UIForm.ACTION);
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;   
    if(event != null) event.broadcast() ;   
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
      UIFormStringInput uiNameInput = event.getSource().getChildById(PAGE_NAME) ;
      UIFormStringInput uiDisplayNameInput = event.getSource().getChildById(PAGE_DISPLAY_NAME) ;
      PageNode pageNode = uiPageNodeSelector.getSelectedPageNode();
      if (pageNode == null){
        uiNameInput.setValue(null) ;
        uiDisplayNameInput.setValue(null) ;
        return ;
      }
      String pageName = pageNode.getPageReference().split("::")[2] ;
      //if(pageNode.getName() != null) uiNameInput.setValue(pageNode.getName());
      if(pageNode.getName() != null) uiNameInput.setValue(pageName);
      if(pageNode.getLabel() != null) uiDisplayNameInput.setValue(pageNode.getLabel());
    }
  }
 
}
