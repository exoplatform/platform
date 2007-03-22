/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIPortalForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIJSApplication;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UILoginForm;
import org.exoplatform.portal.config.PortalDAO;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.services.portletregistery.Portlet;
import org.exoplatform.services.portletregistery.PortletCategory;
import org.exoplatform.services.portletregistery.PortletRegisteryService;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 20, 2006
 */
public class UIPortalActionListener { 
  
  static public class MaximizeActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      
      UIPortal uiPortal = Util.getUIPortal();
      String portletId = event.getRequestContext().getRequestParameter("portletId");
      portletId = portletId.split("-")[0];
      UIPortlet portlet = uiPortal.findComponentById(portletId);
      if(portlet == null ) return ;
      uiPortal.setMaximizedUIComponent(portlet);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortal);
    }
  }
  
  static public class MinimizeActionListener extends EventListener<UIComponent> {
    public void execute(Event<UIComponent> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setMaximizedUIComponent(null);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortal);
    }
  }
  
  static public class ShowLoginFormActionListener  extends EventListener<UIPortal> {    
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      
      if(uiMaskWS  == null) { return; }
      UILoginForm uiForm = uiMaskWS.createUIComponent(UILoginForm.class, null, null);
      
      uiMaskWS.setUIComponent(uiForm) ;
      uiMaskWS.setShow(true) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class EditPortalActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = event.getSource();
      UIPortalForm uiForm = uiPortal.createUIComponent(UIPortalForm.class, null, null);
      uiForm.setValues(uiPortal.getUserPortalConfig().getPortalConfig());
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
      uiToolPanel.setUIComponent(uiForm);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;      
    }
  }
  
  static public class AddPortletToDesktopActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();  
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIPage uiPage = null;
      if(uiPortal.isRendered()){
        uiPage = uiPortal.findFirstComponentOfType(UIPage.class);
      } else {
        UIPortalToolPanel uiPortalToolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
        uiPage = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      }      
      
      UIPortlet uiPortlet =  uiPage.createUIComponent(UIPortlet.class, null, null);      
      String portletId = event.getRequestContext().getRequestParameter("portletId");    
      StringBuilder windowId = new StringBuilder(Util.getUIPortal().getOwner());
      windowId.append(":/").append(portletId).append('/').append(uiPortlet.hashCode());
      uiPortlet.setWindowId(windowId.toString());
      
      Portlet portlet = getPortlet(uiPortal, portletId);
      if(portlet != null){
        if(portlet.getDisplayName() != null) {
          uiPortlet.setTitle(portlet.getDisplayName());
        } else if(portlet.getPortletName() != null) {
          uiPortlet.setTitle(portlet.getPortletName());
        }
        uiPortlet.setDescription(portlet.getDescription());
      }
      
      uiPage.addChild(uiPortlet);
      
      Page page = PortalDataModelUtil.toPageModel(uiPage, true); 
      PortalDAO configService = uiPage.getApplicationComponent(PortalDAO.class);      
      configService.savePage(page);

      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
      pcontext.setForceFullUpdate(true);
    }
    
    @SuppressWarnings("unchecked")
    private Portlet getPortlet(UIPortal uiPortal, String id) throws Exception {
      PortletRegisteryService service = uiPortal.getApplicationComponent(PortletRegisteryService.class) ;
      List<PortletCategory> pCategories = service.getPortletCategories() ;    

      for(PortletCategory pCategory : pCategories) {
        List<Portlet> portlets = service.getPortlets(pCategory.getId()) ;
        for(Portlet portlet : portlets){
          if(portlet.getId().equals(id)) return portlet;
        }  
      }    
      return null;
    }
  }
  
  static public class AddJSApplicationToDesktopActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      String application  = event.getRequestContext().getRequestParameter("jsApplication");
      String applicationId  = event.getRequestContext().getRequestParameter("jsApplicationId");
      String instanceId  = event.getRequestContext().getRequestParameter("jsInstanceId");
      String appLoc= event.getRequestContext().getRequestParameter("jsApplicationLocation");
      UIPortal uiPortal = Util.getUIPortal();  
      UIPortalApplication uiPortalApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      UIPage uiPage = null;
      if(uiPortal.isRendered()){
        uiPage = uiPortal.findFirstComponentOfType(UIPage.class);
      } else {
        UIPortalToolPanel uiPortalToolPanel = uiPortalApp.findFirstComponentOfType(UIPortalToolPanel.class);
        uiPage = uiPortalToolPanel.findFirstComponentOfType(UIPage.class);
      }
      
      StringBuilder builder  = new StringBuilder();
      builder.append("eXo.desktop.UIDesktop.createJSApplication('");
      builder.append(application).append("','").append(applicationId).
              append("','").append(instanceId).append("','").append(appLoc).append("');");
      UIJSApplication jsApplication = uiPage.createUIComponent(UIJSApplication.class, null, null);
      jsApplication.setJSApplication(builder.toString());
      jsApplication.setId(instanceId);
      uiPage.addChild(jsApplication);
    }
  }
  
  static public class RemoveJSApplicationToDesktopActionListener  extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      String instanceId  = event.getRequestContext().getRequestParameter("jsInstanceId");
      UIPage uiPage  = event.getSource().findFirstComponentOfType(UIPage.class);
      uiPage.removeChildById(instanceId);
    }
  }
  
}