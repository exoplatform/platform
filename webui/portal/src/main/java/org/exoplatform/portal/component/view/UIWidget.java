/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import java.util.HashMap;

import org.exoplatform.portal.component.view.lifecycle.UIWidgetLifecycle;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.config.annotation.ComponentConfig;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 16, 2007  
 */

@ComponentConfig(lifecycle = UIWidgetLifecycle.class)
public class UIWidget extends UIComponent {
  
  private String applicationInstanceId_ ;
  private String applicationOwnerType_ ;
  private String applicationOwnerId_ ;
  private String applicationGroup_ ;
  private String applicationName_ ;
  private String applicationInstanceUniqueId_ ;
  private String applicationId_ ;
  
  private HashMap<String, String> properties;
  
  public UIWidget() throws Exception {
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
  
  public HashMap<String, String> getProperites() { return properties; }
  public void setProperites(HashMap<String, String> properties) { this.properties = properties; }
  
}