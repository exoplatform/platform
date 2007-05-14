/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.application.registry.Application;
import org.exoplatform.application.registry.ApplicationCategory;
import org.exoplatform.application.registry.ApplicationRegistryService;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.customization.UIPortalForm;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIJSApplication;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 20, 2006
 */
public class UIPortalActionListener { 
  
  static public class ChangeWindowStateActionListener extends EventListener<UIPortal> {
    public void execute(Event<UIPortal> event) throws Exception {
      UIPortal uiPortal  = event.getSource();
      String portletId = event.getRequestContext().getRequestParameter("portletId");
      UIPortlet uiPortlet = uiPortal.findComponentById(portletId);
      WebuiRequestContext context = event.getRequestContext();
      uiPortlet.createEvent("ChangeWindowState", event.getExecutionPhase(), context).broadcast();
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
      
      Application portlet = getPortlet(uiPortal, portletId);
      if(portlet != null){
        if(portlet.getDisplayName() != null) {
          uiPortlet.setTitle(portlet.getDisplayName());
        } else if(portlet.getApplicationName() != null) {
          uiPortlet.setTitle(portlet.getApplicationName());
        }
        uiPortlet.setDescription(portlet.getDescription());
      }
      
      uiPage.addChild(uiPortlet);
      
      String save = event.getRequestContext().getRequestParameter("save");
      if(save != null && Boolean.valueOf(save).booleanValue()) {
        Page page = PortalDataModelUtil.toPageModel(uiPage); 
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
    private Application getPortlet(UIPortal uiPortal, String id) throws Exception {
      ApplicationRegistryService service = uiPortal.getApplicationComponent(ApplicationRegistryService.class) ;
      List<ApplicationCategory> pCategories = service.getApplicationCategories();   

      for(ApplicationCategory pCategory : pCategories) {
        List<Application> portlets = service.getApplications(pCategory) ;
        for(Application portlet : portlets){
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