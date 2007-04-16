/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.portal.config.model.Component;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIFormInputIconSelector;
import org.exoplatform.webui.component.UIFormInputInfo;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
/**
 * Author : Dang Van Minh, Pham Tuan
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
@ComponentConfig(  
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIFormTabPane.gtmpl" ,    
    events = {
      @EventConfig(listeners = UIPageNodeForm.SaveActionListener.class ),
      @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class )
    }
)
public class UIPageNodeForm extends UIFormTabPane {

  private  PageNode  pageNode_ ; 
  private  Object selectedParent ;

  public UIPageNodeForm() throws Exception {
    super("UIPageNodeForm") ;
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PageNodeSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("uri", "uri", null).setEditable(false)).                            
    addUIFormInput(new UIFormStringInput("name","name", null).
                   addValidator(EmptyFieldValidator.class)).
                   addUIFormInput(new UIFormStringInput("label", "label", null).
                   addValidator(EmptyFieldValidator.class)).
    addUIFormInput(new UIFormStringInput("type", "type", null)).
    addUIFormInput(new UIFormTextAreaInput("description", "description",null)).
    addUIFormInput(new UIFormInputInfo("creator", "creator", null)).
    addUIFormInput(new UIFormInputInfo("modifier", "modifier", null));
    
    addUIFormInput(uiSettingSet);

    UIPermissionSelector uiPermissionSelector = createUIComponent(UIPermissionSelector.class, null, null);
    uiPermissionSelector.configure("Permission", null, null);
    uiPermissionSelector.createPermission("AccessPermission", null);
    uiPermissionSelector.setRendered(false) ;
    addUIComponentInput(uiPermissionSelector) ;

    UIPageSelector uiPageSelector = createUIComponent(UIPageSelector.class, null, null) ;
    uiPageSelector.configure("UIPageSelector", "pageReference") ;
    uiPageSelector.setRendered(false) ;
    addUIFormInput(uiPageSelector) ;

    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon") ;
    uiIconSelector.setRendered(false)  ;
    addUIFormInput(uiIconSelector) ;   
  }

  public PageNode getPageNode(){ return pageNode_ ;   }

  public void setValues(PageNode pageNode) throws Exception {  
    pageNode_ = pageNode;
    getUIStringInput("name").setEditable(UIFormStringInput.DISABLE);    
    if(pageNode == null){      
      getUIStringInput("name").setEditable(UIFormStringInput.ENABLE);
      return;
    }   
    invokeGetBindingBean(pageNode_) ;    
    UIPermissionSelector uiPermissionSelector = getChild(UIPermissionSelector.class);
    Permission permission = uiPermissionSelector.getPermission("AccessPermission");
    permission.setPermissionExpression(pageNode_.getAccessPermission());
    
    UIPageSelector uiPageSelector = getChild(UIPageSelector.class);
    if(uiPageSelector != null) uiPageSelector.setUIInputValue(pageNode_.getPageReference());
  }

  public Object getSelectedParent(){ return selectedParent; }  
  public void setSelectedParent(Object obj) { this.selectedParent = obj; }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIPageSelector uiPageSelector = getChild(UIPageSelector.class);    
    if(uiPageSelector != null ) {  
      UIPopupWindow uiPopupWindowPage = uiPageSelector.getChild(UIPopupWindow.class);
      uiPopupWindowPage.processRender(context);
    }
    
    UIPermissionSelector uiPermissionSelector = getChild(UIPermissionSelector.class);    
    UIPopupWindow uiPopupPermission = uiPermissionSelector.getChild(UIPopupWindow.class);
    uiPopupPermission.processRender(context);
  }
  
  static public class SaveActionListener extends EventListener<UIPageNodeForm> {
    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiPageNodeForm = event.getSource();
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIPageSelector uiPageSelector = uiPageNodeForm.getChild(UIPageSelector.class);
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      UIPageManagement uiManagement = uiPortalApp.findFirstComponentOfType(UIPageManagement.class);
      UIPageNodeSelector uiNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);
      pcontext.addUIComponentToUpdateByAjax(uiManagement);
      
      PageNode pageNode = uiPageNodeForm.getPageNode();
      if(pageNode == null) pageNode  = new PageNode();
      uiPageNodeForm.invokeSetBindingBean(pageNode) ;
      if(pageNode.getCreator() == null) pageNode.setCreator(pcontext.getRemoteUser());      
      pageNode.setModifier(pcontext.getRemoteUser());
      
      UIPermissionSelector uiPermissionSelector = uiPageNodeForm.getChild(UIPermissionSelector.class);
      if(uiPermissionSelector != null){
        Permission permission = uiPermissionSelector.getPermission("AccessPermission");
        if(permission != null) pageNode.setAccessPermission(permission.getValue());
        else pageNode.setAccessPermission(null);
      }
      
      UIFormInputIconSelector uiIconSelector = uiPageNodeForm.getChild(UIFormInputIconSelector.class);
      pageNode.setIcon(uiIconSelector.getSelectedIcon());
      
      if(uiPageSelector != null) {
        Object pageReference = uiPageSelector.getUIInputValue();
        if(pageReference != null){
          pageNode.setPageReference(String.valueOf(pageReference));
        }else{
          String id = pageNode.getCreator()+":/" +pageNode.getName();
          PortalDAO dataService = uiPageNodeForm.getApplicationComponent(PortalDAO.class);
          Page page = dataService.getPage(id);
          if(page == null){
            page = new Page();
            page.setId(id);
            page.setName(pageNode.getName());
            page.setChildren(new ArrayList<Component>(0));
            page.setOwner(pageNode.getCreator());
            dataService.savePage(page);
          }
          pageNode.setPageReference(page.getId());
        }        
      }
      
      Object selectedParent = uiPageNodeForm.getSelectedParent();
      PageNavigation pageNav = null;
      if(selectedParent == null) {
        pageNav = new PageNavigation();
        pageNav.getNodes().add(pageNode);
        pageNav.setOwner(Util.getPortalRequestContext().getRemoteUser());
        uiNodeSelector.getNavigations().add(pageNav);
        selectedParent = pageNav;
      }
      
      if(selectedParent instanceof PageNavigation){
        pageNav = (PageNavigation)selectedParent;         
        pageNode.setUri(pageNode.getName());
        if(!pageNav.getNodes().contains(pageNode)) pageNav.addNode(pageNode);
      }else if(selectedParent instanceof PageNode){
        PageNode parentNode = (PageNode)selectedParent; 
        List<PageNode> children = parentNode.getChildren();
        if(children == null){ 
          children = new ArrayList<PageNode>();
          parentNode.setChildren((ArrayList<PageNode>)children);
        }
        if(!children.contains(pageNode)) children.add(pageNode);
        pageNode.setUri(parentNode.getUri()+"/"+pageNode.getName());       
      }
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      UIPageNodeSelector uiPageNodeSelector = uiControl.findFirstComponentOfType(UIPageNodeSelector.class);   
      uiPageNodeSelector.selectPageNodeByUri(pageNode.getUri());
    }
  }

}
