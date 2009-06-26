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
package org.exoplatform.webui.organization;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Thanh Tung
 *          thanhtungty@gmail.com
 * Jun 26, 2009  
 */
public class OrganizationUtils {
  
  static public String getGroupLabel(String groupId) throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    ExoContainer container = context.getApplication().getApplicationServiceContainer() ;
    OrganizationService orgService = (OrganizationService) 
                        container.getComponentInstanceOfType(OrganizationService.class);
    Group group = orgService.getGroupHandler().findGroupById(groupId);
    String label = group.getLabel();
    return (label != null && label.trim().length() > 0) ? label : group.getGroupName();     
  }

}
