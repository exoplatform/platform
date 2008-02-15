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
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.page.UIPageSelector;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputIconSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
/**
 * Author : Dang Van Minh, Pham Tuan
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
@ComponentConfig(  
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormTabPane.gtmpl" ,    
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
                   addValidator(EmptyFieldValidator.class).addValidator(IdentifierValidator.class)).
    addUIFormInput(new UIFormStringInput("label", "label", null).addValidator(IdentifierValidator.class));
    
    addUIFormInput(uiSettingSet);
    setSelectedTab(uiSettingSet.getId()) ;

    UIPageSelector uiPageSelector = createUIComponent(UIPageSelector.class, null, null) ;
//    uiPageSelector.addValidator(NullFieldValidator.class);
    uiPageSelector.configure("UIPageSelector", "pageReference") ;
    addUIFormInput(uiPageSelector) ;

    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon") ;
    addUIFormInput(uiIconSelector) ;   
  }

  public PageNode getPageNode(){ return pageNode_ ;   }

  public void setValues(PageNode pageNode) throws Exception {
    pageNode_ = pageNode;
    if(pageNode == null) {      
      getUIStringInput("name").setEditable(UIFormStringInput.ENABLE);
      getChild(UIFormInputIconSelector.class).setSelectedIcon("Default");
      return;
    } 
    getUIStringInput("name").setEditable(UIFormStringInput.DISABLE);    
    String icon = pageNode_.getIcon();
    if( icon == null || icon.length() < 0) icon = "Default" ;
    getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    invokeGetBindingBean(pageNode_) ;
    getUIStringInput("label").setValue(pageNode_.getResolvedLabel()) ;
  }

  public Object getSelectedParent(){ return selectedParent; }  
  public void setSelectedParent(Object obj) { this.selectedParent = obj; }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIPageSelector uiPageSelector = getChild(UIPageSelector.class);    
    if(uiPageSelector == null ) return ;  
    UIPopupWindow uiPopupWindowPage = uiPageSelector.getChild(UIPopupWindow.class);
    if(uiPopupWindowPage == null ) return;
    uiPopupWindowPage.processRender(context);
  }
  
  static public class SaveActionListener extends EventListener<UIPageNodeForm> {
    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiPageNodeForm = event.getSource();
      UIPageSelector pageSelector = uiPageNodeForm.getChild(UIPageSelector.class);
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = uiPageNodeForm.getAncestorOfType(UIPortalApplication.class);
     
      if(pageSelector.getPage() == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.selectPage", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages() );
        return;
      }
      
      PageNode pageNode = uiPageNodeForm.getPageNode();
      if(pageNode == null) pageNode  = new PageNode();
      uiPageNodeForm.invokeSetBindingBean(pageNode) ;
      UIFormInputIconSelector uiIconSelector = uiPageNodeForm.getChild(UIFormInputIconSelector.class);
      
      if(uiIconSelector.getSelectedIcon().equals("Default")) pageNode.setIcon(null);
      else pageNode.setIcon(uiIconSelector.getSelectedIcon());
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      UIPageNodeSelector uiPageNodeSelector = uiControl.findFirstComponentOfType(UIPageNodeSelector.class);   
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      UIPage uiPage = Util.toUIPage(pageSelector.getPage(),uiToolPanel);
      uiToolPanel.setShowMaskLayer(true);
      uiToolPanel.setUIComponent(uiPage);
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);

      String remoteUser = pcontext.getRemoteUser();
      
      Object selectedParent = uiPageNodeForm.getSelectedParent();
      PageNavigation pageNav = null;
      
      if(selectedParent instanceof PageNavigation){
        pageNav = (PageNavigation)selectedParent;
        pageNav.setModifier(remoteUser);
        pageNode.setUri(pageNode.getName());
        //if(!children.contains(pageNode)) children.add(pageNode);
        if(!pageNav.getNodes().contains(pageNode)) {
          if(uiPageNodeSelector.searchPageNodeByUri(pageNav, pageNode.getUri()) != null) {
            //uiPageNodeForm.setRenderedChild("PageNodeSetting") ;
            //uiPageNodeForm.setWithRenderTab(true) ;
            UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
            pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
            uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.SameName", null)) ;
            pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
            return ;
          }
          pageNav.addNode(pageNode);
        }
      } else if(selectedParent instanceof PageNode) {
        PageNode parentNode = (PageNode)selectedParent; 
        List<PageNode> children = parentNode.getChildren();
        if(children == null){ 
          children = new ArrayList<PageNode>();
          parentNode.setChildren((ArrayList<PageNode>)children);
        }
        pageNode.setUri(parentNode.getUri()+"/"+pageNode.getName());
        if(!children.contains(pageNode)) {
          if(PageNavigationUtils.searchPageNodeByUri(parentNode, pageNode.getUri()) != null) {
            //uiPageNodeForm.setRenderedChild("PageNodeSetting") ;
            //uiPageNodeForm.setWithRenderTab(true) ;
            UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
            pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
            uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.SameName", null)) ;
            pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
            return ;
          }          
          children.add(pageNode);
        }
      }
      if(pageNode.getLabel() == null) pageNode.setLabel(pageNode.getName());
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      UIPageManagement uiManagement = uiPortalApp.findFirstComponentOfType(UIPageManagement.class);
      UIPageNodeSelector pageNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);
      pageNodeSelector.selectPageNodeByUri(pageNode.getUri());
      UIPageEditBar editBar = uiManagement.getChild(UIPageEditBar.class);
      editBar.setUIPage(uiPage);
      if(uiPage.getFactoryId() != null) {
        editBar.setRendered(uiPage.isModifiable() && !uiPage.getFactoryId().equals("Desktop"));
      } else {
        editBar.setRendered(uiPage.isModifiable());
      }
      pcontext.addUIComponentToUpdateByAjax(uiManagement);   
      pcontext.addUIComponentToUpdateByAjax(uiToolPanel.getParent());
      pcontext.setFullRender(true);
    }
  }

}
