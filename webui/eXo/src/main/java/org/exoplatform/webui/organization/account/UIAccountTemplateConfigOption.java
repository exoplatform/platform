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
package org.exoplatform.webui.organization.account;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.organization.UIUserMembershipSelector.Membership;

/**
 * Created by The eXo Platform SARL
 * Author : Nguyen Viet Chung
 *          nguyenchung136@yahoo.com
 * Aug 7, 2006  
 */
public class UIAccountTemplateConfigOption extends SelectItemOption<String> {

  private List<Membership> listMembership_ ;  
  
  @SuppressWarnings("unused")
  public UIAccountTemplateConfigOption(String label, String value, String desc, String icon) throws Exception {
    super(label, value, desc, icon);
    listMembership_ = new ArrayList<Membership>() ;
  }
  
  public List<Membership> getMemberships() { return listMembership_ ; }  
  
  public UIAccountTemplateConfigOption addMembership(Membership membership) {
    listMembership_.add(membership) ;
    return this ;
  }
  
}
