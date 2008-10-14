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

import java.io.Writer;

import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIContainer;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig()
public class UIMembershipManagement extends UIContainer {

  public UIMembershipManagement() throws Exception {
    addChild(UIListMembershipType.class, null, null);
    addChild(UIMembershipTypeForm.class, null, null);
  }

  public UIGroupMembershipForm getGroupMembershipForm () {
    UIOrganizationPortlet uiParent = getParent() ;
    UIGroupManagement groupManagement = uiParent.getChild(UIGroupManagement.class) ;
    UIGroupDetail groupDetail = groupManagement.getChild(UIGroupDetail.class) ;
    UIGroupInfo groupInfo = groupDetail.getChild(UIGroupInfo.class) ;
    UIUserInGroup userIngroup = groupInfo.getChild(UIUserInGroup.class) ;
    return userIngroup.getChild(UIGroupMembershipForm.class) ;
  }

  public void addOptions(MembershipType option) {
    getGroupMembershipForm().addOptionMembershipType(option) ;
  }

  public void deleteOptions(MembershipType option) {
    getGroupMembershipForm().removeOptionMembershipType(option) ;
  }

  @SuppressWarnings("unused")
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.write("<div id=\"UIMembershipManagement\" class=\"UIMembershipManagement\">");
    renderChildren();
    w.write("</div>");
  }

}
