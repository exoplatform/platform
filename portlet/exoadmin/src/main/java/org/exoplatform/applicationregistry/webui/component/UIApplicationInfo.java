/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applicationregistry.webui.component;

import org.exoplatform.application.newregistry.Application;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jul 4, 2008  
 */

@ComponentConfig(
    template = "app:/groovy/applicationregistry/webui/component/UIApplicationInfo.gtmpl"
)

public class UIApplicationInfo extends UIContainer {
  
  private Application application_ ;
  
  public UIApplicationInfo() throws Exception {
    addChild(UIPermissionForm.class, null, null) ;
  }

  public Application getApplication() { return application_ ; }
  
  public void setApplication(Application app) throws Exception { 
    application_ = app ;
    UIPermissionForm uiPermissionForm = getChild(UIPermissionForm.class) ;
    uiPermissionForm.setValue(application_) ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
  }

}
