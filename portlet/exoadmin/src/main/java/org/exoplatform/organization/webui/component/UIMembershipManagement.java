/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.io.Writer;

import org.exoplatform.organization.webui.component.UIOrganizationPortlet.UIViewMode;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
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
    UIViewMode uiParent = getParent() ;
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
