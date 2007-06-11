/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.organization.account;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.webui.bean.SelectItemOption;
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
