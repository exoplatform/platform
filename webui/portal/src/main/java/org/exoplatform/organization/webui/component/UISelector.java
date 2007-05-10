/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.List;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIForm;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 19, 2006  
 */
abstract class UISelector extends UIFormInputContainer<String> { 
  
  public UISelector(String name, String bindingField) {
    super(name, bindingField) ;   
  }
  
  abstract void setMembership(String groupId, String membershipType);   
  
  static  public class SelectMembershipActionListener extends EventListener<UIGroupMembershipSelector>  {   
    public void execute(Event<UIGroupMembershipSelector>  event) throws Exception {
      UIGroupMembershipSelector uiMemebershipSelector = event.getSource();
      UISelector uiSelector = uiMemebershipSelector.<UIComponent>getParent().getParent(); 
      String membershipType = event.getRequestContext().getRequestParameter(OBJECTID)  ;
      uiSelector.setMembership(uiMemebershipSelector.getCurrentGroup().getId(), membershipType);
    }
  }
  
}
