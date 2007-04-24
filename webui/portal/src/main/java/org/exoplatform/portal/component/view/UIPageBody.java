/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.container.ExoContainer;
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
@ComponentConfig(
    template = "system:/groovy/portal/webui/component/view/UIPageBody.gtmpl"
)
public class UIPageBody extends UIComponentDecorator {
  
  protected String template_ ;
  protected String decorator_ ;
  protected String width_ ;
  protected String height_ ;
  private transient boolean modifiable_ ;
  private String title_; 
  
  @SuppressWarnings("hiding")
  public  void init(PageBody config) throws Exception {
    setId(config.getId());
    template_ = config.getTemplate();
    decorator_ = config.getDecorator();
    width_ = config.getWidth();
    height_ = config.getHeight();
    modifiable_ = config.isModifiable();
  }
  
  @SuppressWarnings("unused")
  public void setPageBody(PageNode pageNode, UIPortal uiPortal) throws Exception {
    WebuiRequestContext  context = Util.getPortalRequestContext() ;
    ExoContainer appContainer  =  context.getApplication().getApplicationServiceContainer() ;
    UserPortalConfigService userPortalConfigService = 
      (UserPortalConfigService)appContainer.getComponentInstanceOfType(UserPortalConfigService.class);
    Page page  = userPortalConfigService.getPage(pageNode.getPageReference(), context.getRemoteUser());
    if("Default".equals(page.getFactoryId())) page.setFactoryId(null);
    UIPage uiPage = createUIComponent(context, UIPage.class, page.getFactoryId(),  null);
    PortalDataModelUtil.toUIPage(uiPage, page);
    
    setUIComponent(uiPage);  
    
    if(uiPage.isShowMaxWindow()){
      uiPortal.setMaximizedUIComponent(uiPage);
    }else{     
      uiPortal.setMaximizedUIComponent(null);
    }
  }
  
  public String getDecorator() { return decorator_; }
  public void   setDecorator(String decorator) { decorator_ = decorator ; }
  
  public String getTemplate() {
    if(template_ == null || template_.length() == 0)  return getComponentConfig().getTemplate() ;
    return template_ ; 
  }
  public void   setTemplate(String s) { template_ = s ; }
  
  public String getWidth() { return width_ ; }
  public void   setWidth(String s) { width_ = s ;}
  
  public String getHeight() { return height_ ; }
  public void   setHeight(String s) { height_ = s ;}
  
  public boolean isModifiable() { return modifiable_ ; }
  public void    setModifiable(boolean b) { modifiable_ = b ; }
  
  public String getTitle() { return title_ ; }
  public void   setTitle(String s) { title_ = s ; } 
}
