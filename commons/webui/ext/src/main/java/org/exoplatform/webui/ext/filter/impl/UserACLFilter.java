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
package org.exoplatform.webui.ext.filter.impl;

import java.util.List;
import java.util.Map;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;

/**
 * This filter is used to add some access permissions to a specific extension
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * 14 mai 2009  
 */
public class UserACLFilter implements UIExtensionFilter {

  /**
   * The list of all access permissions allowed
   */
  protected List<String> permissions;
    
  /**
   * {@inheritDoc}
   */
  public boolean accept(Map<String, Object> context) throws Exception {
    if (permissions == null || permissions.isEmpty()) {
      return true;
    }
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    UserACL userACL = (UserACL) container.getComponentInstance(UserACL.class);
    for (int i = 0, length = permissions.size(); i < length; i++) {
      String permission = permissions.get(i);
      if (userACL.hasPermission(permission)) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public UIExtensionFilterType getType() {
    return UIExtensionFilterType.MANDATORY;
  }

  /**
   * {@inheritDoc}
   */
  public void onDeny(Map<String, Object> context) throws Exception {}
}
