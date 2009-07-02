/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.portal.portlet;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.portletcontainer.PortletContainerException;
import org.exoplatform.commons.utils.ExceptionUtil;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Apr 29, 2009  
 */
public class LogPortletExceptionListener extends BaseComponentPlugin implements PortletExceptionListener {
  
  protected static Log log = ExoLogger.getLogger("portal:UIPortletLifecycle");
  
  public void handle(PortletContainerException e) {
    log.error("The portlet could not be loaded. Check if properly deployed.", ExceptionUtil.getRootCause(e));
    
  }
}
