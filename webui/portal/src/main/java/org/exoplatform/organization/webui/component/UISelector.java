/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 19, 2006  
 */
abstract class UISelector<T> extends UIFormInputContainer<T> { 
  
  protected UISelector() {
  }
  
  public UISelector(String name, String bindingField) {
    super(name, bindingField) ;   
  }
  
  abstract void setMembership(String groupId, String membershipType) throws Exception ;   
  
  static  public class SelectMembershipActionListener extends EventListener<UIGroupMembershipSelector>  {   
    public void execute(Event<UIGroupMembershipSelector>  event) throws Exception {
      UIGroupMembershipSelector uiMemebershipSelector = event.getSource();
      UISelector uiSelector = uiMemebershipSelector.<UIComponent>getParent().getParent(); 
      String membershipType = event.getRequestContext().getRequestParameter(OBJECTID)  ;
      uiSelector.setMembership(uiMemebershipSelector.getCurrentGroup().getId(), membershipType);
    }
  }
  
}
