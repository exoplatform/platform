/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace.UIControlWSWorkingArea;
import org.exoplatform.portal.component.customization.UIPageForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.PortalDataMapper;
import org.exoplatform.portal.component.view.UIExoApplication;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPageBody;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.UIWidget;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 14, 2006
 */
public class UIPageActionListener {

  static public class ChangePageNodeActionListener  extends EventListener {    
    
    private UIPortal uiPortal_ ;    
    private List<PageNode> selectedPaths_;   
    
    public void execute(Event event) throws Exception {     
      PageNodeEvent pnevent = (PageNodeEvent) event ;
      uiPortal_ = (UIPortal) event.getSource();
      selectedPaths_ = new ArrayList<PageNode>(5);

      List<PageNavigation> navigations = uiPortal_.getNavigations();
      String uri = pnevent.getTargetNodeUri();

      for(PageNavigation nav : navigations){
        List<PageNode>  nodes = nav.getNodes();
        for(PageNode node : nodes){       
          PageNode nodeResult = searchPageNodeByUri(uri, node);
          if(nodeResult == null) continue;
          selectedPaths_.add(0, nodeResult);          
          break;
        }
      }      
      
      uiPortal_.setSelectedPaths(selectedPaths_);     
      UIPageBody uiPageBody = uiPortal_.findFirstComponentOfType(UIPageBody.class);          
      uiPageBody.setPageBody(uiPortal_.getSelectedNode(), uiPortal_);
      
      UIPortalApplication uiPortalApp = uiPortal_.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      PortalRequestContext pcontext = Util.getPortalRequestContext();     
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);      
      uiPortal_.setRenderSibbling(UIPortal.class);
      pcontext.setFullRender(true);

      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      if(uiControl == null) return;
      UIControlWSWorkingArea uiWorking = uiControl.getChild(UIControlWSWorkingArea.class);
      if(uiControl != null) pcontext.addUIComponentToUpdateByAjax(uiControl);      
      if(!UIWelcomeComponent.class.isInstance(uiWorking.getUIComponent())) {
        uiWorking.setUIComponent(uiWorking.createUIComponent(UIWelcomeComponent.class, null, null)) ;
      } 
    }

    private PageNode searchPageNodeByUri(String uri, PageNode node){
      if(node.getUri().equals(uri)){
        uiPortal_.setSelectedNode(node);
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
  
  static public class AddApplicationActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();  
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIPage uiPage = null;
      if(uiPortal.isRendered()){
        uiPage = uiPortal.findFirstComponentOfType(UIPage.class);
      } else {
        UIPortalToolPanel uiPortalToolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
        uiPage = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      }      
      
      String applicationId = event.getRequestContext().getRequestParameter("applicationId");  
      StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
      windowId.append(":/").append(applicationId).append('/');
      Application application = getApplication(uiPortal, applicationId);
      
      if(org.exoplatform.portal.config.model.Application.EXO_APPLICATION_TYPE.equals(application.getApplicationType())){
        UIExoApplication exoApplication = uiPage.createUIComponent(UIExoApplication.class, null, null);
        windowId.append(exoApplication.hashCode());
        exoApplication.setApplicationInstanceId(windowId.toString());
        exoApplication.init();
        uiPage.addChild(exoApplication);
      } else {// if(org.exoplatform.portal.config.model.Application.PORTLET_TYPE.equals(application.getApplicationType())){
        UIPortlet uiPortlet =  uiPage.createUIComponent(UIPortlet.class, null, null);  
        windowId.append(":/").append(applicationId).append('/').append(uiPortlet.hashCode());
        uiPortlet.setWindowId(windowId.toString());
        if(application != null){
          if(application.getDisplayName() != null) {
            uiPortlet.setTitle(application.getDisplayName());
          } else if(application.getApplicationName() != null) {
            uiPortlet.setTitle(application.getApplicationName());
          }
          uiPortlet.setDescription(application.getDescription());
        }
        uiPage.addChild(uiPortlet);
      }

      String save = event.getRequestContext().getRequestParameter("save");
      if(save != null && Boolean.valueOf(save).booleanValue()) {
        Page page = PortalDataMapper.toPageModel(uiPage); 
        UserPortalConfigService configService = uiPortalApp.getApplicationComponent(UserPortalConfigService.class);     
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
        configService.update(page);
      }

      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
    }
    
    @SuppressWarnings("unchecked")
    private Application getApplication(UIPortal uiPortal, String id) throws Exception {
      ApplicationRegistryService service = uiPortal.getApplicationComponent(ApplicationRegistryService.class) ;
      List<ApplicationCategory> pCategories = service.getApplicationCategories();   

      for(ApplicationCategory pCategory : pCategories) {
        List<Application> applications = service.getApplications(pCategory) ;
        for(Application application : applications){
          if(application.getId().equals(id)) return application;
        }  
      }    
      return null;
    }
  }
  
  static public class DeleteWidgetActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      UIPage uiPage = event.getSource();
      List<UIComponent> children = uiPage.getChildren();
      Iterator<UIComponent> iter = children.iterator();
      while(iter.hasNext()) {
        UIWidget uiWidget = (UIWidget) iter.next();
        if(uiWidget.getApplicationId().equals(id)) {
          iter.remove();
          break;
        }
      }
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
    }
  }
  
  static public class RemoveChildActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPage uiPage = event.getSource();
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      uiPage.removeChildById(id);  
      Page page = PortalDataMapper.toPageModel(uiPage);    
      UserPortalConfigService configService = uiPage.getApplicationComponent(UserPortalConfigService.class);     
      if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
      configService.update(page);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setFullRender(true);
    }
  }
}
