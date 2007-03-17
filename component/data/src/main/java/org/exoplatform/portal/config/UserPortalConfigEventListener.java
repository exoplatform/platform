/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.config;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 22, 2006
 */

public abstract class UserPortalConfigEventListener extends BaseComponentPlugin {
  
  abstract PortalConfig onComputePortalConfig(
      PortalConfig config, String portalOwner, String accessUser ) throws Exception ;
  
  abstract PageNavigation onComputePageNavigation(
      PageNavigation navigation, String portalOwner, String accessUser ) throws Exception ;
}
