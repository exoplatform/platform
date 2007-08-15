/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.page.UIPage;
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
import org.exoplatform.webui.form.validator.NullFieldValidator;
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
    addUIFormInput(new UIFormStringInput("label", "label", null));
    
    addUIFormInput(uiSettingSet);

    UIPageSelector uiPageSelector = createUIComponent(UIPageSelector.class, null, null) ;
//    uiPageSelector.addValidator(NullFieldValidator.class);
    uiPageSelector.configure("UIPageSelector", "pageReference") ;
    uiPageSelector.setRendered(false) ;
    addUIFormInput(uiPageSelector) ;

    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon") ;
    uiIconSelector.setRendered(false) ;
    addUIFormInput(uiIconSelector) ;   
  }

  public PageNode getPageNode(){ return pageNode_ ;   }

  public void setValues(PageNode pageNode) throws Exception {  
    pageNode_ = pageNode;
    if(pageNode == null) {      
      getUIStringInput("name").setEditable(UIFormStringInput.ENABLE);
      return;
    } 
    getUIStringInput("name").setEditable(UIFormStringInput.DISABLE);    
    String icon = pageNode_.getIcon();
    if(icon != null && icon.length() > 0) {
      getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    }
    invokeGetBindingBean(pageNode_) ;    
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
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
     
      if(pageSelector.getPage() == null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.selectPage", null)) ;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages() );
        return;
      }
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      UIPageNodeSelector uiPageNodeSelector = uiControl.findFirstComponentOfType(UIPageNodeSelector.class);   
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      UIPage uiPage = Util.toUIPage(pageSelector.getPage(),uiToolPanel);
      uiToolPanel.setShowMaskLayer(true);
      uiToolPanel.setUIComponent(uiPage);
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class);
      PageNode pageNode = uiPageNodeForm.getPageNode();
      if(pageNode == null) pageNode  = new PageNode();
      uiPageNodeForm.invokeSetBindingBean(pageNode) ;

      String remoteUser = Util.getPortalRequestContext().getRemoteUser();
      UIFormInputIconSelector uiIconSelector = uiPageNodeForm.getChild(UIFormInputIconSelector.class);
      pageNode.setIcon(uiIconSelector.getSelectedIcon());
      
      Object selectedParent = uiPageNodeForm.getSelectedParent();
      PageNavigation pageNav = null;
      
      if(selectedParent instanceof PageNavigation){
        pageNav = (PageNavigation)selectedParent;
        pageNav.setModifier(remoteUser);
        pageNode.setUri(pageNode.getName());
        //if(!children.contains(pageNode)) children.add(pageNode);
        if(!pageNav.getNodes().contains(pageNode)) {
          if(uiPageNodeSelector.searchPageNodeByUri(pageNav, pageNode.getUri()) != null) {
            uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.SameName", null)) ;
            pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
            uiPageNodeForm.setRenderedChild("PageNodeSetting") ;
            uiPageNodeForm.setWithRenderTab(true) ;
            UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
            pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
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
          if(uiPageNodeSelector.searchPageNodeByUri(pageNav, pageNode.getUri()) != null) {
            uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.SameName", null)) ;
            pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages()) ;
            uiPageNodeForm.setRenderedChild("PageNodeSetting") ;
            uiPageNodeForm.setWithRenderTab(true) ;
            UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
            pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
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
      pcontext.addUIComponentToUpdateByAjax(uiManagement);   
      pcontext.addUIComponentToUpdateByAjax(uiToolPanel.getParent());
      pcontext.setFullRender(true);
      //uiPageNodeSelector.selectPageNodeByUri(pageNode.getUri());
    }
  }

}
