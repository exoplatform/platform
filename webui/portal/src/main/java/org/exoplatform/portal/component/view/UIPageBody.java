/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PageBody;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 19, 2006
 */
@ComponentConfig(template = "system:/groovy/portal/webui/component/view/UIPageBody.gtmpl")
public class UIPageBody extends UIComponentDecorator {
  
  @SuppressWarnings("unused")
  public  void init(PageBody model) throws Exception {
    setId("PageBody");
  }
  
  @SuppressWarnings("unused")
  public void setPageBody(PageNode pageNode, UIPortal uiPortal) throws Exception {
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService userPortalConfigService = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    Page page  = userPortalConfigService.getPage(pageNode.getPageReference(), context.getRemoteUser());
    UIPage uiPage = null;
    if(page != null) {
    if("Default".equals(page.getFactoryId())) page.setFactoryId(null);
      uiPage = createUIComponent(context, UIPage.class, page.getFactoryId(),  null);
      PortalDataModelUtil.toUIPage(uiPage, page);
    } else {
      uiPage = createUIComponent(context, UIPage.class, null,  null);
      uiPage.setOwnerId(context.getRemoteUser());
      uiPage.setOwnerType(DataStorage.USER_TYPE);
    }
    
    setUIComponent(uiPage);  
    
    if(uiPage.isShowMaxWindow()){
      uiPortal.setMaximizedUIComponent(uiPage);
    }else{     
      uiPortal.setMaximizedUIComponent(null);
    }
  }
 
}
