/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.application.registry;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.container.xml.InitParams;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * May 3, 2007  
 */
public class AddAvailableExoApplication extends BaseComponentPlugin {
  private Application application ;
  
  public AddAvailableExoApplication(InitParams params) {
    
  }
  
  public Application getApplication() {
    return null ;
  }
}
