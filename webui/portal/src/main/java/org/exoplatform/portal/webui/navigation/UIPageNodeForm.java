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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.page.UIPage;
import org.exoplatform.portal.webui.page.UIPageEditBar;
import org.exoplatform.portal.webui.page.UIPageSelector;
import org.exoplatform.portal.webui.page.UIWizardPageSetInfo;
import org.exoplatform.portal.webui.util.Util;
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
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormInputIconSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.DateTimeValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.form.validator.MandatoryValidator;
import org.exoplatform.webui.form.validator.StringLengthValidator;
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
      @EventConfig(phase = Phase.DECODE, listeners = UIMaskWorkspace.CloseActionListener.class ),
      @EventConfig(listeners = UIPageNodeForm.SwitchPublicationDateActionListener.class, phase = Phase.DECODE ),
      @EventConfig(listeners = UIPageNodeForm.ClearPageActionListener.class, phase = Phase.DECODE)
    }
)
public class UIPageNodeForm extends UIFormTabPane {

  private  PageNode  pageNode_ ; 
  private  Object selectedParent ;
  final private static String   SHOW_PUBLICATION_DATE = "showPublicationDate" ;  
  final private static String   START_PUBLICATION_DATE = "startPublicationDate" ;
  final private static String   END_PUBLICATION_DATE = "endPublicationDate" ; 

  public UIPageNodeForm() throws Exception {
    super("UIPageNodeForm") ;
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PageNodeSetting") ;
    UIFormCheckBoxInput<Boolean> uiDateInputCheck = new UIFormCheckBoxInput<Boolean>(SHOW_PUBLICATION_DATE, SHOW_PUBLICATION_DATE, false) ;
    uiDateInputCheck.setOnChange("SwitchPublicationDate") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("uri", "uri", null).setEditable(false)).                            
    addUIFormInput(new UIFormStringInput("name","name", null).
                   addValidator(MandatoryValidator.class).
                   addValidator(StringLengthValidator.class, 3, 30).
                   addValidator(IdentifierValidator.class)).
    addUIFormInput(new UIFormStringInput("label", "label", null).
                   addValidator(StringLengthValidator.class, 3, 120)).
    addUIFormInput(new UIFormCheckBoxInput<Boolean>("visible", "visible", true).setChecked(true)).
    addUIFormInput(uiDateInputCheck).
    addUIFormInput(new UIFormDateTimeInput(START_PUBLICATION_DATE, null, null).
        addValidator(MandatoryValidator.class).addValidator(DateTimeValidator.class)).
    addUIFormInput(new UIFormDateTimeInput(END_PUBLICATION_DATE, null, null).
        addValidator(MandatoryValidator.class).addValidator(DateTimeValidator.class)) ;
    addUIFormInput(uiSettingSet);
    setSelectedTab(uiSettingSet.getId()) ;

    UIPageSelector uiPageSelector = createUIComponent(UIPageSelector.class, null, null) ;
    uiPageSelector.configure("UIPageSelector", "pageReference") ;
    addUIFormInput(uiPageSelector) ;

    UIFormInputIconSelector uiIconSelector = new UIFormInputIconSelector("Icon", "icon") ;
    addUIFormInput(uiIconSelector) ;
    setActions(new String[] {"Save", "Close"}) ;
  }

  public PageNode getPageNode(){ return pageNode_ ;   }

  public void setValues(PageNode pageNode) throws Exception {
    pageNode_ = pageNode;
    if(pageNode == null) {      
      getUIStringInput("name").setEditable(UIFormStringInput.ENABLE);
      getChild(UIFormInputIconSelector.class).setSelectedIcon("Default");
      setShowPublicationDate(false) ;
      return;
    } 
    getUIStringInput("name").setEditable(UIFormStringInput.DISABLE);    
    invokeGetBindingBean(pageNode_) ;
  }
  
  public void invokeGetBindingBean(Object bean) throws Exception {
    super.invokeGetBindingBean(bean) ;
    PageNode pageNode = (PageNode)bean ;
    String icon = pageNode_.getIcon();
    if( icon == null || icon.length() < 0) icon = "Default" ;
    getChild(UIFormInputIconSelector.class).setSelectedIcon(icon);
    getUIStringInput("label").setValue(pageNode_.getLabel()) ;
    getUIFormCheckBoxInput("visible").setChecked(pageNode_.isVisible()) ;
    setShowPublicationDate(pageNode.isShowPublicationDate()) ;
    Calendar cal = Calendar.getInstance() ;
    if(pageNode.getStartPublicationDate() != null) {
      cal.setTime(pageNode.getStartPublicationDate()) ;
      getUIFormDateTimeInput(START_PUBLICATION_DATE).setCalendar(cal) ;        
    } else getUIFormDateTimeInput(START_PUBLICATION_DATE).setValue(null) ;
    if(pageNode.getEndPublicationDate() != null) {
      cal.setTime(pageNode.getEndPublicationDate()) ;
      getUIFormDateTimeInput(END_PUBLICATION_DATE).setCalendar(cal) ;
    } else getUIFormDateTimeInput(END_PUBLICATION_DATE).setValue(null) ;    
  }
  
  public void invokeSetBindingBean(Object bean) throws Exception {
    super.invokeSetBindingBean(bean) ;
    PageNode node = (PageNode)bean ;
    Calendar cal = getUIFormDateTimeInput(START_PUBLICATION_DATE).getCalendar() ;
    Date date = (cal != null) ? cal.getTime() : null ; 
    node.setStartPublicationDate(date) ;
    cal = getUIFormDateTimeInput(END_PUBLICATION_DATE).getCalendar() ;
    date = (cal != null) ? cal.getTime() : null ;
    node.setEndPublicationDate(date) ;
  }
  
  public void setShowPublicationDate(boolean show) {
    getUIFormCheckBoxInput(SHOW_PUBLICATION_DATE).setChecked(show) ;
    getUIFormDateTimeInput(START_PUBLICATION_DATE).setRendered(show) ;
    getUIFormDateTimeInput(END_PUBLICATION_DATE).setRendered(show) ;    
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
     
      if(uiPageNodeForm.getUIFormCheckBoxInput(SHOW_PUBLICATION_DATE).isChecked()) {
        Calendar startCalendar = uiPageNodeForm.getUIFormDateTimeInput(UIWizardPageSetInfo.START_PUBLICATION_DATE).getCalendar();
        Date startDate = startCalendar.getTime();
        Calendar endCalendar = uiPageNodeForm.getUIFormDateTimeInput(UIWizardPageSetInfo.END_PUBLICATION_DATE).getCalendar();
        Date endDate = endCalendar.getTime();
        if(startDate.after(endDate)) {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.startDateBeforeEndDate", null)) ;
          pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());
          return;
        }
      }
//      if(pageSelector.getPage() == null) {
//        uiPortalApp.addMessage(new ApplicationMessage("UIPageNodeForm.msg.selectPage", null)) ;
//        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages() );
//        return;
//      }
      
      PageNode pageNode = uiPageNodeForm.getPageNode();
      if(pageNode == null) pageNode  = new PageNode();
      uiPageNodeForm.invokeSetBindingBean(pageNode) ;
      if(pageSelector.getPage() == null) pageNode.setPageReference(null) ;
      UIFormInputIconSelector uiIconSelector = uiPageNodeForm.getChild(UIFormInputIconSelector.class);
      
      if(uiIconSelector.getSelectedIcon().equals("Default")) pageNode.setIcon(null);
      else pageNode.setIcon(uiIconSelector.getSelectedIcon());
      //TODO: Delete UIControlWorkspace
      UIPageNodeSelector uiPageNodeSelector = uiPortalApp.findFirstComponentOfType(UIPageNodeSelector.class);   
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel() ;
      UIPage uiPage = null;
      if(pageSelector.getPage() != null) uiPage = Util.toUIPage(pageSelector.getPage(),uiToolPanel);
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
      if(uiPage != null) {
        if(uiPage.getFactoryId() != null) {
          editBar.setRendered(uiPage.isModifiable() && !uiPage.getFactoryId().equals("Desktop"));
        } else {
          editBar.setRendered(uiPage.isModifiable());
        }
      }
      pcontext.addUIComponentToUpdateByAjax(uiManagement);   
      pcontext.addUIComponentToUpdateByAjax(uiToolPanel.getParent());
      pcontext.setFullRender(true);
    }
  }
  
  static public class SwitchPublicationDateActionListener extends EventListener<UIPageNodeForm> {
    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiForm = event.getSource() ;      
      boolean isCheck = uiForm.getUIFormCheckBoxInput(SHOW_PUBLICATION_DATE).isChecked() ;
      uiForm.getUIFormDateTimeInput(START_PUBLICATION_DATE).setRendered(isCheck) ;
      uiForm.getUIFormDateTimeInput(END_PUBLICATION_DATE).setRendered(isCheck) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
    } 
  }  
  
  static public class ClearPageActionListener extends EventListener<UIPageNodeForm> {
    public void execute(Event<UIPageNodeForm> event) throws Exception {
      UIPageNodeForm uiForm = event.getSource() ;
      UIPageSelector pageSelector = uiForm.findFirstComponentOfType(UIPageSelector.class) ;
      pageSelector.setPage(null) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(pageSelector) ;
    }
  }
}
