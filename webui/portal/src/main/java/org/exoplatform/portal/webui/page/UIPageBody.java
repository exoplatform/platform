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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageBody;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponentDecorator;

/**
 * May 19, 2006
 */
@ComponentConfig(template = "system:/groovy/portal/webui/page/UIPageBody.gtmpl")
public class UIPageBody extends UIComponentDecorator {
  
  private UIPortalComponent maximizedUIComponent;
  
  @SuppressWarnings("unused")
  public  UIPageBody(PageBody model) throws Exception {
    setId("UIPageBody");
  }
  
  public UIPageBody() throws Exception {
    setId("UIPageBody");
  }
  
  @SuppressWarnings("unused")
  public  void init(PageBody model) throws Exception {
    setId("UIPageBody");
  }
  
  public void setPageBody(PageNode pageNode, UIPortal uiPortal) throws Exception {
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService userPortalConfigService = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    Page page  = null;
    UIPage uiPage = null;
    if(pageNode != null){
      try {
        page  = userPortalConfigService.getPage(pageNode.getPageReference(), context.getRemoteUser());
      }catch (Exception e) {
        UIPortalApplication uiApp = getAncestorOfType(UIPortalApplication.class);
        uiApp.addMessage(new ApplicationMessage(e.getMessage(), new Object[]{}));
      }
    }
    
    if(page != null) {
      if(Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        uiPage = createUIComponent(context, UIDesktopPage.class, null, null);      	
      } else {
        uiPage = createUIComponent(context, UIPage.class, null, null);      	
      }
      PortalDataMapper.toUIPage(uiPage, page);
      if(uiPage.isShowMaxWindow()) {
        uiPortal.setMaximizedUIComponent(uiPage);
      } else {     
        uiPortal.setMaximizedUIComponent(null);
      }   
    } 
    
   setUIComponent(uiPage);
  }
  
  public void renderChildren() throws Exception {
    if(maximizedUIComponent != null) {
      maximizedUIComponent.processRender((WebuiRequestContext) WebuiRequestContext.getCurrentInstance()) ;
      return;
    }
    if(uicomponent_ == null) {
      setPageBody(Util.getUIPortal().getSelectedNode(), Util.getUIPortal());
    }
    if(uicomponent_ != null) {
      uicomponent_.processRender((WebuiRequestContext)WebuiRequestContext.getCurrentInstance()) ;
    }
  }

  public UIPortalComponent getMaximizedUIComponent() { return maximizedUIComponent; }

  public void setMaximizedUIComponent(UIPortalComponent uiMaximizedComponent) { this.maximizedUIComponent = uiMaximizedComponent; }
 
}
