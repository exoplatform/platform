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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.portlet.WindowState;

import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.application.UIAddNewApplication;
import org.exoplatform.portal.webui.application.UIApplication;
import org.exoplatform.portal.webui.application.UIGadget;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.navigation.PageNavigationUtils;
import org.exoplatform.portal.webui.page.UIPageActionListener.DeleteGadgetActionListener;
import org.exoplatform.portal.webui.page.UIPageActionListener.RemoveChildActionListener;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.ShowLoginFormActionListener;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * May 19, 2006
 */

@ComponentConfig(
	lifecycle = UIPageLifecycle.class,
	template = "system:/groovy/portal/webui/page/UIDesktopPage.gtmpl",
	events = {
		@EventConfig(listeners = ShowLoginFormActionListener.class),
		@EventConfig(listeners = DeleteGadgetActionListener.class),
		@EventConfig(listeners = RemoveChildActionListener.class),
		@EventConfig(listeners = UIDesktopPage.SaveGadgetPropertiesActionListener.class),
		@EventConfig(listeners = UIDesktopPage.SaveWindowPropertiesActionListener.class),
		@EventConfig(listeners = UIDesktopPage.ShowAddNewApplicationActionListener.class),
		@EventConfig(listeners = UIDesktopPage.ChangePageActionListener.class),
		@EventConfig(listeners = UIDesktopPage.ShowPortletActionListener.class)
	}
)
public class UIDesktopPage extends UIPage {
  
  public UIDesktopPage() {
    setChildren((List<UIComponent>)new CopyOnWriteArrayList<UIComponent>()) ;
  }
  
  
	public boolean isShowMaxWindow() { return true; }
	
  public List<PageNavigation> getNavigations() throws Exception {
    List<PageNavigation> allNav =Util.getUIPortal().getNavigations() ;
    String removeUser = Util.getPortalRequestContext().getRemoteUser();
    List<PageNavigation> result = new ArrayList<PageNavigation>();
    for(PageNavigation nav: allNav){
      result.add(PageNavigationUtils.filter(nav, removeUser));
    }
    return result;
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
  
  static public class ChangePageActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      String uri  = event.getRequestContext().getRequestParameter(OBJECTID);
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
      UIPageBody uiPageBody = uiPortal.findFirstComponentOfType(UIPageBody.class);
      if(uiPageBody != null) {
        if(uiPageBody.getMaximizedUIComponent() != null) {
          UIPortlet currentPortlet =  (UIPortlet) uiPageBody.getMaximizedUIComponent();
          currentPortlet.setCurrentWindowState(WindowState.NORMAL);
          uiPageBody.setMaximizedUIComponent(null);
        }
      }
      PageNodeEvent<UIPortal> pnevent = 
        new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, uri) ;      
      uiPortal.broadcast(pnevent, Event.Phase.PROCESS) ;      
    }
  }
  
  static public class ShowPortletActionListener extends EventListener<UIPage> {
    public void execute(Event<UIPage> event) throws Exception {
      UIPage uiPage = event.getSource() ;
      String portletId = event.getRequestContext().getRequestParameter(UIComponent.OBJECTID) ;
      UIPortlet uiPortlet = uiPage.getChildById(portletId) ;
      uiPortlet.getProperties().setProperty("appStatus", "SHOW") ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortlet) ;
    }
  }
  
}