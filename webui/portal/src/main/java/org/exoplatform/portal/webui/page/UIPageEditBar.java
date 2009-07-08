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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.webui.UIManagement.ManagementMode;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.navigation.UIPageNavigationControlBar;
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(  
    template = "system:/groovy/webui/core/UIToolbar.gtmpl",
    events = {
        @EventConfig(listeners = UIPageEditBar.PagePreviewActionListener.class),
        @EventConfig(listeners = UIPageEditBar.EditPageActionListener.class),        
        @EventConfig(listeners = UIPageEditBar.EditContainerActionListener.class),
        @EventConfig(listeners = UIPageEditBar.EditPortletActionListener.class),
        @EventConfig(listeners = UIPageEditBar.SavePageActionListener.class)
    }
)
public class UIPageEditBar extends UIToolbar { 

  transient UIPage uiPage_;

  public UIPageEditBar() throws Exception {
    setToolbarStyle("EditToolbar") ;
    setJavascript("PagePreview","onclick='eXo.portal.UIPortal.switchModeForPage(this)';") ;
  }
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
    Util.getUIPortalToolPanel().findComponentOfType(uiPortlets, UIPortlet.class);
    for (UIPortlet uiPortlet : uiPortlets) {
      uiPortlet.setShowEditControl(b);
    }
    return super.<T>setRendered(b) ;
  } 

  public UIPage getUIPage(){ return uiPage_; }  
  public void setUIPage(UIPage uiPage){ uiPage_ = uiPage; }

  public void showUIPage(){
    if(uiPage_ == null) return;    
    UIPortalToolPanel uiToolPanel =  Util.getUIPortalToolPanel();
    uiToolPanel.setUIComponent(uiPage_);    
  }

  static public class PagePreviewActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
    }
  }

  static public class EditContainerActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {      
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.showUIPage();      

      UIPageManagement uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {};
      if(uiPManagement.getChild(UIPageNavigationControlBar.class).isRendered()) {
        childrenToRender = new Class<?>[]{UIPageEditBar.class, UIContainerConfigOptions.class, UIPageNavigationControlBar.class};
      } else {
        childrenToRender = new Class<?>[]{UIPageEditBar.class, UIContainerConfigOptions.class, UIPageBrowseControlBar.class};
      }
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
//      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiControl);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);   
      UIPortalToolPanel toolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
      toolPanel.setShowMaskLayer(false);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
    }
  }

  static public class EditPortletActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {     
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.showUIPage();

      UIPageManagement uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {};
      if(uiPManagement.getChild(UIPageNavigationControlBar.class).isRendered()) {
        childrenToRender = new Class[]{UIPageEditBar.class, UIPortletOptions.class, UIPageNavigationControlBar.class};
      } else {
        childrenToRender = new Class[]{UIPageEditBar.class, UIPortletOptions.class, UIPageBrowseControlBar.class};
      }
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
//      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
//      pcontext.addUIComponentToUpdateByAjax(uiControl);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID); 
      UIPortalToolPanel toolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
      toolPanel.setShowMaskLayer(false);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
    }
  }

  static public class EditPageActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {      
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPageEditBar uiEditBar = event.getSource();
      if(uiEditBar.getUIPage() == null) return;
      uiEditBar.showUIPage();
      UIPageForm uiPageForm = uiMaskWS.createUIComponent(UIPageForm.class, null, null);
      uiPageForm.setValues(uiEditBar.getUIPage());
      uiMaskWS.setUIComponent(uiPageForm);
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }

  public void savePage() throws Exception {
    if(uiPage_ == null) return;
    Page page = PortalDataMapper.toPageModel(getUIPage());      
    UserPortalConfigService dataService = getApplicationComponent(UserPortalConfigService.class);
    dataService.update(page); 
  }
  
  static public class SavePageActionListener  extends EventListener<UIPageEditBar> {
    public void execute(Event<UIPageEditBar> event) throws Exception {
      UIPageEditBar uiEditBar = event.getSource();
      uiEditBar.savePage();
      if(!uiEditBar.getUIPage().isModifiable()) {
        UIPageManagement uiManagement = uiEditBar.getParent() ;
        if(uiManagement.getChild(UIPageNavigationControlBar.class).isRendered()) {          
          List<UIComponent> list = uiManagement.getChildren();
          for(int i = 0;i < list.size();i++){
            list.get(i).setRendered(false);
          }
          uiManagement.getChild(UIPageNavigationControlBar.class).setRendered(true);
          uiManagement.getChild(UIPageNodeSelector.class).setRendered(true);
        }
        else {          
          uiManagement.setRenderedChild(UIDescription.class);
          uiManagement.setMode(ManagementMode.BROWSE, event);          
        }
        event.getRequestContext().addUIComponentToUpdateByAjax(uiManagement) ;
      }
    }
  }
  
}