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
import org.exoplatform.portal.application.UserGadgetStorage;
import org.exoplatform.portal.application.UserWidgetStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIAddNewApplication;
import org.exoplatform.portal.webui.application.UIApplication;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.application.UIWidget;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIExoStart;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SAS
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
public class UIPageActionListener {

  @SuppressWarnings("unchecked")
  static public class ChangePageNodeActionListener  extends EventListener {
    
    private UIPortal uiPortal ;
    private List<PageNode> selectedPaths_;
    
    public void execute(Event event) throws Exception {
      PageNodeEvent<?> pnevent = (PageNodeEvent<?>) event ;
      uiPortal = (UIPortal) event.getSource();
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class); 
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      uiPortalApp.setEditting(false) ;
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = Util.getPortalRequestContext();     
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);      
      uiPortal.setRenderSibbling(UIPortal.class);
      pcontext.setFullRender(true);

      UIControlWorkspace uiControl = uiPortalApp.getChildById(UIPortalApplication.UI_CONTROL_WS_ID);
      if(uiControl != null) {
        UIControlWSWorkingArea uiWorking = uiControl.getChild(UIControlWSWorkingArea.class);
        pcontext.addUIComponentToUpdateByAjax(uiControl);  
        UIExoStart exoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);
        pcontext.addUIComponentToUpdateByAjax(exoStart);
        if(!UIWelcomeComponent.class.isInstance(uiWorking.getUIComponent())) {
          uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null));
        }
      }

      selectedPaths_ = new ArrayList<PageNode>(5);

      List<PageNavigation> navigations = uiPortal.getNavigations();
      String uri = pnevent.getTargetNodeUri();
      if(uri == null || (uri = uri.trim()).length() < 1) return;
      if(uri.length() == 1 && uri.charAt(0) == '/') {
        for(PageNavigation nav: navigations){
          for(PageNode child: nav.getNodes()){
            if(PageNavigationUtils.filter(child, pcontext.getRemoteUser()) != null) {
              selectedPaths_.add(child);
              uiPortal.setSelectedNode(child);
              uiPortal.setSelectedPaths(selectedPaths_);  
              uiPageBody.setPageBody(uiPortal.getSelectedNode(), uiPortal);
              return;
            }
          }
        }
      }
      if(uri.charAt(0) == '/') uri = uri.substring(1);

      int idx = uri.lastIndexOf("::");
      if(idx < 0)  {
        for(PageNavigation nav : navigations){
          List<PageNode>  nodes = nav.getNodes();
          PageNode nodeResult = null;
          for(PageNode node : nodes){       
            nodeResult = searchPageNodeByUri(uri, node);
            if(nodeResult == null) continue;
            selectedPaths_.add(0, nodeResult);          
            break;
          }
          if(nodeResult != null) {
            uiPortal.setSelectedNavigation(nav);
            break;
          }
        }      
        uiPortal.setSelectedPaths(selectedPaths_);     
        uiPageBody.setPageBody(uiPortal.getSelectedNode(), uiPortal);
        return;
      }
      String navId = uri.substring(0, idx);
      uri = uri.substring(idx+2, uri.length());
      PageNavigation nav = null;
      for(PageNavigation ele : navigations){
        if(ele.getId().equals(navId)) {
          nav = ele;
          break;
        }
      }
      if(nav != null) {
        List<PageNode>  nodes = nav.getNodes();
        for(PageNode node : nodes){       
          PageNode nodeResult = searchPageNodeByUri(uri, node);
          if(nodeResult == null) continue;
          selectedPaths_.add(0, nodeResult);          
          break;
        }
        uiPortal.setSelectedNavigation(nav);
      }
      uiPortal.setSelectedPaths(selectedPaths_);
      uiPageBody.setPageBody(uiPortal.getSelectedNode(), uiPortal);
    }

    private PageNode searchPageNodeByUri(String uri, PageNode node){
      if(node.getUri().equals(uri)){
        uiPortal.setSelectedNode(node);
        return node;
      }
      List<PageNode> children = node.getChildren();
      if(children == null) return null;
      for(PageNode ele : children){
        PageNode nodeResult = searchPageNodeByUri(uri, ele);
        if(nodeResult == null) continue;
        selectedPaths_.add(0, nodeResult);
        return node; 
      }
      return null;
    }
  }

  static public class EditPageActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {      
      UIPage uiPage = event.getSource();
      UIPageForm uiForm = uiPage.createUIComponent(UIPageForm.class, null, null);
      uiForm.setValues(uiPage);
      UIPortalApplication uiPortalApp = uiPage.getAncestorOfType(UIPortalApplication.class);
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setUIComponent(uiForm) ;
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;     
    }
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      WebuiRequestContext pContext = event.getRequestContext();
      String id  = pContext.getRequestParameter(UIComponent.OBJECTID);
      UIPage uiPage = event.getSource();
      List<UIWidget> uiWidgets = new ArrayList<UIWidget>();
      uiPage.findComponentOfType(uiWidgets, UIWidget.class);
      for(UIWidget uiWidget : uiWidgets) {
        if(uiWidget.getApplicationInstanceUniqueId().equals(id)) {
          uiPage.getChildren().remove(uiWidget);
          String userName = pContext.getRemoteUser() ;
          if(userName != null && userName.trim().length() > 0) {
            UserWidgetStorage widgetDataService = uiPage.getApplicationComponent(UserWidgetStorage.class) ;
            widgetDataService.delete(userName, uiWidget.getApplicationName(), uiWidget.getApplicationInstanceUniqueId()) ;            
          }
          if(uiPage.isModifiable()) {
            Page page = PortalDataMapper.toPageModel(uiPage);    
            UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);     
            if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
            configService.update(page);
          }
          break;
        }
      }
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      pcontext.setFullRender(false);
      pcontext.setResponseComplete(true) ;
      pcontext.getWriter().write(EventListener.RESULT_OK) ;
    }
  }
  
  static public class DeleteGadgetActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      WebuiRequestContext pContext = event.getRequestContext();
      String id  = pContext.getRequestParameter(UIComponent.OBJECTID);
      UIPage uiPage = event.getSource();
      List<UIGadget> uiWidgets = new ArrayList<UIGadget>();
      uiPage.findComponentOfType(uiWidgets, UIGadget.class);
      for(UIGadget uiWidget : uiWidgets) {
        if(uiWidget.getApplicationInstanceUniqueId().equals(id)) {
          uiPage.getChildren().remove(uiWidget);
          String userName = pContext.getRemoteUser() ;
          if(userName != null && userName.trim().length() > 0) {
            UserGadgetStorage widgetDataService = uiPage.getApplicationComponent(UserGadgetStorage.class) ;
            widgetDataService.delete(userName, uiWidget.getApplicationName(), uiWidget.getApplicationInstanceUniqueId()) ;            
          }
          if(uiPage.isModifiable()) {
            Page page = PortalDataMapper.toPageModel(uiPage);    
            UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);     
            if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
            configService.update(page);
          }
          break;
        }
      }
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      pcontext.setFullRender(false);
      pcontext.setResponseComplete(true) ;
      pcontext.getWriter().write(EventListener.RESULT_OK) ;
    }
  }
  
  static public class RemoveChildActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPage uiPage = event.getSource();
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
      if(uiPage.isModifiable()) {
        uiPage.removeChildById(id);
        Page page = PortalDataMapper.toPageModel(uiPage); 
        UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);     
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
        configService.update(page);
        pcontext.setFullRender(false);
        pcontext.setResponseComplete(true) ;
        pcontext.getWriter().write(EventListener.RESULT_OK) ;
      } else{
        org.exoplatform.webui.core.UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPage.msg.EditPermission.null", null)) ;

        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
      }
    }
  }
    
  static public class SaveWidgetPropertiesActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
     
      UIPage uiPage = event.getSource();
      String objectId  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      List<UIWidget> uiWidgets = new ArrayList<UIWidget>();
      uiPage.findComponentOfType(uiWidgets, UIWidget.class);
      UIWidget uiWidget = null;
      for(UIWidget ele : uiWidgets) {
        if(ele.getApplicationInstanceUniqueId().equals(objectId)) {
          uiWidget = ele;
          break;
        }
      }
      if(uiWidget == null) return;
      String posX  = event.getRequestContext().getRequestParameter("posX");
      String posY  = event.getRequestContext().getRequestParameter("posY");
      String zIndex = event.getRequestContext().getRequestParameter(UIApplication.zIndex);
      
      uiWidget.getProperties().put(UIApplication.locationX, posX) ;
      uiWidget.getProperties().put(UIApplication.locationY, posY) ;
      uiWidget.getProperties().put(UIApplication.zIndex, zIndex) ;
      
      if(!uiPage.isModifiable()) return;
      Page page = PortalDataMapper.toPageModel(uiPage);
      UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);
      if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
      configService.update(page);
    }
  }
  
  static public class SaveGadgetPropertiesActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
     
      UIPage uiPage = event.getSource();
      String objectId  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      List<UIGadget> uiGadgets = new ArrayList<UIGadget>();
      uiPage.findComponentOfType(uiGadgets, UIGadget.class);
      UIGadget uiGadget = null;
      for(UIGadget ele : uiGadgets) {
        if(ele.getApplicationInstanceUniqueId().equals(objectId)) {
          uiGadget = ele;
          break;
        }
      }
      if(uiGadget == null) return;
      String posX  = event.getRequestContext().getRequestParameter("posX");
      String posY  = event.getRequestContext().getRequestParameter("posY");
      String zIndex = event.getRequestContext().getRequestParameter(UIApplication.zIndex);
      
      uiGadget.getProperties().put(UIApplication.locationX, posX) ;
      uiGadget.getProperties().put(UIApplication.locationY, posY) ;
      uiGadget.getProperties().put(UIApplication.zIndex, zIndex) ;
      
      if(!uiPage.isModifiable()) return;
      Page page = PortalDataMapper.toPageModel(uiPage);
      UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);
      if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
      configService.update(page);
    }
  }
  
  static public class SaveWindowPropertiesActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPage uiPage = event.getSource();
      String objectId  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      
      UIApplication uiApp = uiPage.getChildById(objectId) ;
      if(uiApp == null) return ;
      
      /*########################## Save Position ##########################*/
      String posX = event.getRequestContext().getRequestParameter("posX");
      String posY = event.getRequestContext().getRequestParameter("posY");
      
      if(posX != null) uiApp.getProperties().put(UIApplication.locationX, posX);
      if(posY != null) uiApp.getProperties().put(UIApplication.locationY, posY);
      
      //System.out.println("\n\n\n\n\n\n\n\n\n\n\n SAVE POSX: "+posX+"\n SAVE POSY: "+posY+"\n\n\n\n\n\n\n\n\n");
      /*########################## Save ZIndex ##########################*/
      String zIndex = event.getRequestContext().getRequestParameter(UIApplication.zIndex);
      
      if(zIndex != null) uiApp.getProperties().put(UIApplication.zIndex, zIndex) ;
      
      /*########################## Save Dimension ##########################*/
      String windowWidth = event.getRequestContext().getRequestParameter("windowWidth");
      String windowHeight = event.getRequestContext().getRequestParameter("windowHeight");
      
      if(windowWidth != null) uiApp.getProperties().put("windowWidth", windowWidth);
      if(windowHeight != null) uiApp.getProperties().put("windowHeight", windowHeight);
      
//      if(appWidth != null) uiComponent.getProperties().put(UIApplication.appWidth, appWidth);
//      if(appHeight != null) uiComponent.getProperties().put(UIApplication.appHeight, appHeight);
      
//      String applicationHeight = event.getRequestContext().getRequestParameter("applicationHeight");
//      if(applicationHeight != null) uiComponent.getProperties().put("applicationHeight", applicationHeight);
      
      /*########################## Save Window status (SHOW / HIDE) ##########################*/
      String appStatus = event.getRequestContext().getRequestParameter(UIApplication.appStatus);
      if(appStatus != null) uiApp.getProperties().put(UIApplication.appStatus, appStatus);
      
//      if(!uiPage.isModifiable()) return;
//      Page page = PortalDataMapper.toPageModel(uiPage);
//      UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);
//      if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
//      configService.update(page);
    }
  }
  
  static public class ShowAddNewApplicationActionListener extends EventListener<UIPage> {

    @Override
    public void execute(Event<UIPage> event) throws Exception {

      UIPage uiPage = event.getSource();

      UIPortalApplication uiPortalApp = uiPage.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWorkspace = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID);      

      UIAddNewApplication uiAddApplication = uiPage.createUIComponent(UIAddNewApplication.class,
          null, null);
      uiAddApplication.setInPage(true);
      uiAddApplication.setUiComponentParent(uiPage);
      uiAddApplication.getApplicationCategories(event.getRequestContext().getRemoteUser(), null);

      uiMaskWorkspace.setWindowSize(700, 375);
      uiMaskWorkspace.setUIComponent(uiAddApplication);
      uiMaskWorkspace.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWorkspace);

    }
  }
}
