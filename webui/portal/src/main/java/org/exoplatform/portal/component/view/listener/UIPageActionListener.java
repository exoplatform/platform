/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.customization.UIPageForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPageBody;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.view.event.PageNodeEvent;
import org.exoplatform.portal.config.PortalDAO;
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
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      if(uiControl  != null) pcontext.addUIComponentToUpdateByAjax(uiControl);      
      pcontext.setForceFullUpdate(true);
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
  
  static public class RemoveChildActionListener  extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPage uiPage = event.getSource();
      String id  = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID);
      uiPage.removeChildById(id);  
      Page page = PortalDataModelUtil.toPageModel(uiPage, true);    
      PortalDAO configService = uiPage.getApplicationComponent(PortalDAO.class);
      configService.savePage(page);
      
      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();      
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setForceFullUpdate(true);
    }
  }
  
}
