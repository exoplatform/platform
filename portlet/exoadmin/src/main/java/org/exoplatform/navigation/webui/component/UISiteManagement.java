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
package org.exoplatform.navigation.webui.component;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.portal.UIPortalForm;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.util.ReflectionUtil;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template = "app:/groovy/navigation/webui/component/UISiteManagement.gtmpl",
  events = {
      @EventConfig(listeners = UISiteManagement.EditPortalLayoutActionListener.class),
      @EventConfig(listeners = UISiteManagement.EditNavigationActionListener.class),
      @EventConfig(listeners = UISiteManagement.AddNewPortalActionListener.class),
      @EventConfig(listeners = UISiteManagement.DeletePortalActionListener.class, confirm = "UIPortalBrowser.deletePortal")
  }
)
public class UISiteManagement extends UIContainer {
  
  //public static String[] SELECT_ACTIONS = {"EditPortalLayout", "EditNavigation", "DeletePortal"} ;
  public static String[] ACTIONS = {"EditNavigation", "DeletePortal"} ;
  
  private LazyPageList pageList;
  
  public UISiteManagement() throws Exception {  
    UIPopupWindow editNavigation = addChild(UIPopupWindow.class, null, "EditPortalNavigation");
    editNavigation.setWindowSize(400, 400);
    loadPortalConfigs();
  }
  
  public List<PortalConfig> getPortalConfigs() throws Exception { 
    return pageList.getAll();
  }
  
  public String[] getActions() { return ACTIONS ; }
  
  public Object getFieldValue(Object bean, String field) throws Exception {
    Method method = ReflectionUtil.getGetBindingMethod(bean, field);
    return method.invoke(bean, ReflectionUtil.EMPTY_ARGS) ;
  }

  public void loadPortalConfigs() throws Exception {    
    DataStorage service = getApplicationComponent(DataStorage.class);
    
    Query<PortalConfig> query = new Query<PortalConfig>(null, null, null, null, PortalConfig.class) ;
    this.pageList = service.find(query, new Comparator<PortalConfig>(){
      public int compare(PortalConfig pconfig1, PortalConfig pconfig2) {
        return pconfig1.getName().toLowerCase().compareTo(pconfig2.getName().toLowerCase());
      }
    });
    
    // Get portals without edit permission
    UserACL userACL = getApplicationComponent(UserACL.class);
    Iterator<PortalConfig> iterPortals  = this.pageList.getAll().iterator();
    PortalConfig portalConfig;
    while (iterPortals.hasNext()) {
        portalConfig = iterPortals.next();
        if (!userACL.hasEditPermission(portalConfig)) {
            iterPortals.remove();
        }
    }    
  } 

  static public class DeletePortalActionListener extends EventListener<UISiteManagement> {
    public void execute(Event<UISiteManagement> event) throws Exception {      
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UserPortalConfigService service = event.getSource().getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UserPortalConfig config = service.getUserPortalConfig(portalName, prContext.getRemoteUser());
      if(config != null && config.getPortalConfig().isModifiable()) {
        service.removeUserPortalConfig(portalName);
      } else if(config != null){
        uiPortalApp.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-deletePermission", new String[]{config.getPortalConfig().getName()})) ;; 
        return;
      }
      
      if(config == null && !Util.getUIPortal().getName().equals(portalName)) {
        uiPortalApp.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-deletePermission", new String[] {portalName}));
        return;
      }
      
      if(config == null || Util.getUIPortal().getName().equals(portalName)) {
        HttpServletRequest request = prContext.getRequest() ;
        request.getSession().invalidate() ;
        prContext.setResponseComplete(true) ;
        prContext.getResponse().sendRedirect(request.getContextPath()) ;
        return;
      }
      
      event.getSource().loadPortalConfigs();
      UIWorkingWorkspace uiWorkingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);    
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS);      
    }    
  }
  
  static public class EditPortalLayoutActionListener extends EventListener<UISiteManagement> {
    public void execute(Event<UISiteManagement> event) throws Exception {
      /*
      UISiteManagement uicomp = event.getSource();
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID);
      UserPortalConfigService service = uicomp.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UserPortalConfig userConfig = service.getUserPortalConfig(portalName, prContext.getRemoteUser());
      PortalConfig portalConfig = userConfig.getPortalConfig();
      UserACL userACL = uiPortalApp.getApplicationComponent(UserACL.class) ;
      if(!userACL.hasEditPermission(portalConfig ,prContext.getRemoteUser())){
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalManager.msg.Invalid-editPermission", null)) ;;  
        return;
      }
      
      uiPortalApp.setEditting(true);
      UIWorkingWorkspace workingWS = uiPortalApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      UIPopupWindow popUp = workingWS.getChild(UIPopupWindow.class);
      if (popUp != null) {        
        workingWS.removeChild(UIPopupWindow.class);
      }
      popUp = workingWS.addChild(UIPopupWindow.class, null, null);
      UIPortalManagement2 portalManager = popUp.createUIComponent(UIPortalManagement2.class, null, null, popUp);
      portalManager.setMode(ManagementMode.EDIT, event);
      
      popUp.setUIComponent(portalManager);
      popUp.setShow(true);
      popUp.setRendered(true);
      popUp.setWindowSize(300, 400);
      prContext.addUIComponentToUpdateByAjax(workingWS);
      */     
    }
  }
  
  static public class EditNavigationActionListener extends EventListener<UISiteManagement> {
    public void execute(Event<UISiteManagement> event) throws Exception {      
      UISiteManagement uicomp = event.getSource();
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UserPortalConfigService service = uicomp.getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prContext = Util.getPortalRequestContext();
      WebuiRequestContext context = event.getRequestContext();
      UIApplication uiApplication = context.getUIApplication();
      
      UserPortalConfig userConfig = service.getUserPortalConfig(portalName, prContext.getRemoteUser());
      PortalConfig portalConfig = userConfig.getPortalConfig();
      
      UserACL userACL = uicomp.getApplicationComponent(UserACL.class) ;
      if(!userACL.hasEditPermission(portalConfig)){
        uiApplication.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-editPermission", null)) ;;  
        return;
      }
      
          
      UIPopupWindow popUp = uicomp.getChild(UIPopupWindow.class);
      
      UINavigationManagement naviManager = popUp.createUIComponent(UINavigationManagement.class, null, null, popUp);
      naviManager.setOwner(portalName);
      naviManager.loadNavigation(new Query<PageNavigation>(PortalConfig.PORTAL_TYPE, portalName, PageNavigation.class));
      popUp.setUIComponent(naviManager);
      popUp.setShow(true);
      
      
    }
  }
  
  static public class AddNewPortalActionListener extends EventListener<UISiteManagement> {
    public void execute(Event<UISiteManagement> event) throws Exception {        
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);  
      UserACL userACL = uiApp.getApplicationComponent(UserACL.class) ;
      if(!userACL.hasCreatePortalPermission()){
        uiApp.addMessage(new ApplicationMessage("UISiteManagement.msg.Invalid-createPermission", null)) ;;  
        return;
      }
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPortalForm uiNewPortal = uiMaskWS.createUIComponent(UIPortalForm.class, "CreatePortal", "UIPortalForm");      
      uiMaskWS.setUIComponent(uiNewPortal);
      uiMaskWS.setShow(true);
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
}
