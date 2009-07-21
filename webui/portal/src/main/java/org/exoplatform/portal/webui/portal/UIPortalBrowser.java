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
package org.exoplatform.portal.webui.portal;

import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.Query;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfig;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkingWorkspace;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(
  template = "app:/groovy/portal/webui/portal/UIPortalBrowser.gtmpl",
  events = { 
      @EventConfig(listeners = UIPortalBrowser.AddNewPortalActionListener.class),
      @EventConfig(listeners = UIPortalBrowser.DeletePortalActionListener.class, confirm = "UIPortalBrowser.deletePortal"),
      @EventConfig(listeners = UIPortalBrowser.BackActionListener.class)
  }
)
public class UIPortalBrowser extends UIContainer {

  //public static String[] BEAN_FIELD = {"creator", "name", "skin", "factoryId"} ;  
  public static String[] BEAN_FIELD = {"creator", "name", "skin","accessPermissions", "editPermission"} ;
  public static String[] SELECT_ACTIONS = {"DeletePortal"} ; 
  
  public UIPortalBrowser() throws Exception {
    setId("UIPortalBrowser");
    
    UIGrid uiGrid = addChild(UIGrid.class, null, null) ;
    uiGrid.configure("name", BEAN_FIELD, SELECT_ACTIONS) ;
    addChild(uiGrid.getUIPageIterator()) ;
    uiGrid.getUIPageIterator().setId("UIPortalBrowserPageInterator");
    uiGrid.getUIPageIterator().setRendered(false) ;
    
    loadPortalConfigs();
  }
  
  public String event(String name, String beanId) throws Exception {
    if(Util.getUIPortal().getName().equals(beanId)) return super.url(name, beanId); 
    return super.event(name, beanId);
  }

  public void loadPortalConfigs() throws Exception {    
    DataStorage service = getApplicationComponent(DataStorage.class) ;
//    UserACL userACL = getApplicationComponent(UserACL.class) ;
//    String accessUser = Util.getPortalRequestContext().getRemoteUser() ;
    UIGrid uiGrid = findFirstComponentOfType(UIGrid.class) ;
    int currentPage = uiGrid.getUIPageIterator().getCurrentPage() ;
    Query<PortalConfig> query = new Query<PortalConfig>(null, null, null, null, PortalConfig.class) ;
    LazyPageList pageList = service.find(query, new Comparator<PortalConfig>(){
      public int compare(PortalConfig pconfig1, PortalConfig pconfig2) {
        return pconfig1.getName().compareTo(pconfig2.getName());
      }
    }) ;
    pageList.setPageSize(10) ;
//    int i = 1 ;
//    System.out.println("\n\n++++++++> PortalBrowse: " + pageList.getAvailable());
//    while(i <= pageList.getAvailablePage()) {
//      List<?> list = pageList.getPage(i) ;
//      Iterator<?> itr = list.iterator() ;
//      while(itr.hasNext()) {
//        PortalConfig portalConfig = (PortalConfig)itr.next() ;
//        if(!userACL.hasPermission(portalConfig,accessUser) )itr.remove() ;
//      }
//      i++ ;
//    }
    uiGrid.setUseAjax(false);
    uiGrid.getUIPageIterator().setPageList(pageList);
    while(currentPage > uiGrid.getUIPageIterator().getAvailablePage()) currentPage-- ;
    uiGrid.getUIPageIterator().setCurrentPage(currentPage) ;
  } 

  static public class DeletePortalActionListener extends EventListener<UIPortalBrowser> {
    public void execute(Event<UIPortalBrowser> event) throws Exception {
      String portalName = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UserPortalConfigService service = event.getSource().getApplicationComponent(UserPortalConfigService.class);
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UserPortalConfig config = service.getUserPortalConfig(portalName, prContext.getRemoteUser());
      if(config != null && config.getPortalConfig().isModifiable()) {
        service.removeUserPortalConfig(portalName);
      } else if(config != null){
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-deletePermission", new String[]{config.getPortalConfig().getName()})) ;; 
        return;
      }
      
      if(config == null && !Util.getUIPortal().getName().equals(portalName)) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-deletePermission", new String[] {portalName}));
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
      event.getRequestContext().addUIComponentToUpdateByAjax(uiWorkingWS) ;
    }
  }
  
  static public class AddNewPortalActionListener extends EventListener<UIPortalBrowser> {
    public void execute(Event<UIPortalBrowser> event) throws Exception {
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIPortalApplication uiApp = event.getSource().getAncestorOfType(UIPortalApplication.class);  
      UserACL userACL = uiApp.getApplicationComponent(UserACL.class) ;
      if(!userACL.hasCreatePortalPermission()){
        uiApp.addMessage(new ApplicationMessage("UIPortalBrowser.msg.Invalid-createPermission", null)) ;;  
        return;
      }
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      UIPortalForm uiNewPortal = uiMaskWS.createUIComponent(UIPortalForm.class, "CreatePortal", "UIPortalForm");
      uiMaskWS.setUIComponent(uiNewPortal);
      uiMaskWS.setShow(true);
      prContext.addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class BackActionListener extends EventListener<UIPortalBrowser> {

    public void execute(Event<UIPortalBrowser> event) throws Exception {
      UIPortalApplication uiPortalApp = Util.getUIPortalApplication();
      uiPortalApp.setEditMode(UIPortalApplication.NORMAL_MODE);
      UIPortal uiPortal = Util.getUIPortal();
      String uri = uiPortal.getSelectedNavigation().getId() + "::" + uiPortal.getSelectedNode().getUri();
      PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal,
                                                                    PageNodeEvent.CHANGE_PAGE_NODE,
                                                                    uri);
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS);
    }
    
  }
}
