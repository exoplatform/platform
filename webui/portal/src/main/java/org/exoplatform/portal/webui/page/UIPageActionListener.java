/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.application.UIAddNewApplication;
import org.exoplatform.portal.webui.application.UIApplication;
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
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.web.application.ApplicationMessage;
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
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = Util.getPortalRequestContext();     
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);      
      uiPortal.setRenderSibbling(UIPortal.class);
      pcontext.setFullRender(true);

      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
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
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setUIComponent(uiForm) ;
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;     
    }
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIPage uiPage = event.getSource();
      List<UIWidget> uiWidgets = new ArrayList<UIWidget>();
      uiPage.findComponentOfType(uiWidgets, UIWidget.class);
      for(UIWidget uiWidget : uiWidgets) {
        if(uiWidget.getApplicationInstanceUniqueId().equals(id)) {
          uiPage.getChildren().remove(uiWidget);
          
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
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(false);
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
      } else{
        org.exoplatform.webui.core.UIApplication uiApp = pcontext.getUIApplication() ;
        uiApp.addMessage(new ApplicationMessage("UIPage.msg.EditPermission.null", null)) ;

        pcontext.addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages() );
      }
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
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
