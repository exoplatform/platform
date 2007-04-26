/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.application.eXoFramework.ExoFrameworkApplication;
import org.exoplatform.application.eXoFramework.ExoFrameworkRequestContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.component.view.lifecycle.UIExoApplicationLifecycle;
import org.exoplatform.web.WebAppController;
import org.exoplatform.webui.config.annotation.ComponentConfig;
/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Feb 9, 2007
 */
@ComponentConfig(
  lifecycle = UIExoApplicationLifecycle.class,    
  template = "system:/groovy/portal/webui/component/view/UIPortlet.gtmpl"
)
public class UIExoApplication extends UIPortalComponent {
  transient private ExoFrameworkApplication application_ ;
  transient private ExoFrameworkRequestContext requestContext_ ;
  
  private String applicationInstanceId_ ;

  private boolean  showInfoBar = true ;
  private boolean  showWindowState = true ;
  private boolean  showPortletMode = true ;
  private String   description;
  private String   icon;
  
  private String applicationOwnerType_ ;
  private String applicationOwnerId_ ;
  private String applicationGroup_ ;
  private String applicationName_ ;
  private String applicationInstanceUniqueId_ ;
  
  public UIExoApplication(){
    System.out.print("\n\nCreate UIExoApplication Component\n\n") ;
  }

  public void init() throws Exception { 
    PortalContainer pcontainer = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)pcontainer.getComponentInstanceOfType(WebAppController.class) ;
    application_ = controller.getApplication("") ;
    System.out.print("\n\nInit UIExoApplication Component\n\n") ;
  }
  
  public String getApplicationInstanceId() { return applicationInstanceId_ ; }
  public void   setApplicationInstanceId(String s) {  
    applicationInstanceId_ = s ;
    String[]  tmp =  applicationInstanceId_.split("/") ;
    applicationGroup_ = tmp[1] ;
    applicationName_ = tmp[2] ;
    applicationInstanceUniqueId_ = tmp[3] ;
  }
  
  public String getDescription() {  return  description ; }
  public void   setDescription(String s) { description = s ;}
  
  public boolean getShowInfoBar() { return showInfoBar ; }
  public void    setShowInfoBar(boolean b) {showInfoBar = b ;}
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; } 
  
  public boolean getShowWindowState() { return showWindowState ; }
  public void    setShowWindowState(boolean b) { showWindowState = b ; }
  
  public boolean getShowPortletMode() { return showPortletMode ; }
  public void    setShowPortletMode(boolean b) { showPortletMode = b ; }
  
  public String getApplicationOwnerType() { return applicationOwnerType_ ;}
  public String getApplicationOwnerId() { return applicationOwnerId_ ;}
  public String getApplicationGroup() { return applicationGroup_ ;}
  public String getApplicationName() { return applicationName_ ;}
  public String getApplicationInstanceUniqueId() { return applicationInstanceUniqueId_ ;}
  
  public ExoFrameworkApplication getApplication() { return application_ ; }
  
  public ExoFrameworkRequestContext getExoFrameworkRequestContext() { return requestContext_ ; }
  public void setExoFrameworkRequestContext(ExoFrameworkRequestContext context) {
    requestContext_ = context ;
  }
}
