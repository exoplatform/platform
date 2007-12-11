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
package org.exoplatform.organization.webui.component;

import org.exoplatform.services.organization.Group;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;

/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:08:51 AM 
 */
@ComponentConfig(template = "app:/groovy/organization/webui/component/UIGroupInfoContainer.gtmpl")
public class UIGroupInfo extends UIContainer {
  
  public UIGroupInfo() throws Exception {
    addChild(UIUserInGroup.class, null, null) ;
  }
  
  public void setGroup(Group group) throws Exception {
    getChild(UIUserInGroup.class).setValues(group);
    setRenderedChild(UIUserInGroup.class);
  }
  
}