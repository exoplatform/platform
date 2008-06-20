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
package org.exoplatform.portal.webui.application;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.config.model.Properties;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.gadget.GadgetApplication;
import org.exoplatform.web.application.gadget.GadgetRegistryService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIComponent;
/**
 * Created by The eXo Platform SAS
 * Author : dang.tung
 *          tungcnw@gmail.com
 * May 06, 2008   
 */
@ComponentConfig(
    template = "system:/groovy/portal/webui/application/UIGadget.gtmpl"
)
public class UIGadget extends UIComponent {
  
  private String applicationInstanceId_ ;
  private String applicationOwnerType_ ;
  private String applicationOwnerId_ ;
  private String applicationGroup_ ;
  private String applicationName_ ;
  private String applicationInstanceUniqueId_ ;
  private String applicationId_ ;
  private String userPref_ ;
  private Properties properties;
  
  public UIGadget() throws Exception {
  }
  
  public String getApplicationInstanceId() { return applicationInstanceId_ ; }
  public void   setApplicationInstanceId(String s) {  
    applicationInstanceId_ = s ;
    String[]  tmp =  applicationInstanceId_.split("/") ;
    applicationGroup_ = tmp[1] ;
    applicationName_ = tmp[2] ;
    applicationId_ =  applicationGroup_ + "/" + applicationName_ ;
    applicationInstanceUniqueId_ = tmp[3] ;
  }
  
  public String getApplicationOwnerType() { return applicationOwnerType_ ;}
  public void setApplicationOwnerType(String ownerType){ applicationOwnerType_ = ownerType;}
  
  public String getApplicationOwnerId() { return applicationOwnerId_ ;}
  public void setApplicationOwnerId(String ownerId){ applicationOwnerId_ = ownerId;} 
  
  public String getApplicationGroup() { return applicationGroup_ ;}
  public void setApplicationGroup(String group){ applicationGroup_ = group;}
  
  public String getApplicationName() { return applicationName_ ;}
  public void setApplicationName(String name) { applicationName_ = name;}
  
  public String getApplicationId() { return applicationId_ ; }
  
  public String getApplicationInstanceUniqueId() { return applicationInstanceUniqueId_ ;}
  
  public Properties getProperties() {
    if(properties == null) properties  = new Properties();
    return properties; 
  }
  public void setProperties(Properties properties) { this.properties = properties; }

  private GadgetApplication getApplication() {
    PortalContainer container = PortalContainer.getInstance() ;
//    WebAppController controller =
//      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    GadgetRegistryService gadgetService = (GadgetRegistryService) container.getComponentInstanceOfType(GadgetRegistryService.class) ;
    //TODO: Review try catch block
    GadgetApplication application = null ;
    try {
      application = gadgetService.getGadget(applicationId_.split("/")[1]) ;
    } catch (Exception e) {}
//    controller.addApplication(application) ;
    return application;
  }

  public String getUrl() {
    GadgetApplication application = getApplication();
    return application.getUrl() ;
  }

  public String getMetadata() {
    GadgetApplication application = getApplication();
    return application.getMetadata();
  }
  
  public String getUserPref() { return userPref_ ;}
  public void setUserPref(String userPref) {userPref_ = userPref ;}
}