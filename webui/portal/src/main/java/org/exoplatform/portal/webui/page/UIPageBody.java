/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import java.util.ResourceBundle;

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
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
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
  
  @SuppressWarnings("unused")
  public void setPageBody(PageNode pageNode, UIPortal uiPortal) throws Exception {
    if(pageNode == null){
      setPage(null, uiPortal); 
      return;
    }
    setPage(pageNode.getPageReference(), uiPortal);
  }
  
  public void setPage(String pageId, UIPortal uiPortal) throws Exception {
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService userPortalConfigService = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    Page page  = null;
    UIPage uiPage = null;
    if(pageId != null){
      try {
        page  = userPortalConfigService.getPage(pageId, context.getRemoteUser());
      }catch (Exception e) {
        UIPortalApplication uiApp = getAncestorOfType(UIPortalApplication.class);
        uiApp.addMessage(new ApplicationMessage(e.getMessage(), new Object[]{}));
      }
    }
    
    if(page != null) {
      if(Page.DEFAULT_PAGE.equals(page.getFactoryId())) page.setFactoryId(null);
      uiPage = createUIComponent(context, UIPage.class, page.getFactoryId(), null);
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
      return ;
    }
    WebuiRequestContext rcontext = WebuiRequestContext.getCurrentInstance();
    ResourceBundle res = rcontext.getApplicationResourceBundle() ;
    rcontext.getWriter().append(res.getString("UIPageBody.msg.pageNotFound"));

  }

  public UIPortalComponent getMaximizedUIComponent() { return maximizedUIComponent; }

  public void setMaximizedUIComponent(UIPortalComponent uiMaximizedComponent) { this.maximizedUIComponent = uiMaximizedComponent; }
 
}
