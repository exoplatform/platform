/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIBreadcumbs;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 8, 2007  
 */

@ComponentConfigs({ 
  @ComponentConfig(
      template = "system:/groovy/organization/webui/component/UIGroupSelector.gtmpl",
      events = {
          @EventConfig(phase = Phase.DECODE, listeners = UIGroupSelector.ChangeNodeActionListener.class)
//          @EventConfig(phase = Phase.DECODE, listeners = SelectMembershipActionListener.class),
//          @EventConfig(phase = Phase.DECODE, listeners = SelectPathActionListener.class)  
      }  
  ),
  @ComponentConfig(
      type = UITree.class, id = "UITreeGroupSelector",
      template = "system:/groovy/webui/component/UITree.gtmpl",
      events = @EventConfig(listeners = UITree.ChangeNodeActionListener.class)
  ),
  @ComponentConfig(
      type = UIBreadcumbs.class, id = "BreadcumbGroupSelector",
      template = "system:/groovy/webui/component/UIBreadcumbs.gtmpl",
      events = @EventConfig(listeners = UIBreadcumbs.SelectPathActionListener.class)
  )
})

public class UIGroupSelector extends UIContainer {
  
  List<String> listMemberhip;
  
  public UIGroupSelector() throws Exception {
    UIBreadcumbs uiBreadcumbs = addChild(UIBreadcumbs.class, "BreadcumbGroupSelector", "BreadcumbGroupSelector") ;
    UITree tree = addChild(UITree.class, "UITreeGroupSelector", "TreeGroupSelector");
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    Collection sibblingsGroup = service.getGroupHandler().findGroups(null);
    
    Collection collection = service.getMembershipTypeHandler().findMembershipTypes();
    listMemberhip  = new ArrayList<String>(5);
    for(Object obj : collection){
      listMemberhip.add(((MembershipType)obj).getName());
    }
    listMemberhip.add("any"); 
    
    tree.setSibbling((List)sibblingsGroup);
    tree.setIcon("Icon GroupAdminIcon");
    tree.setSelectedIcon("Icon PortalIcon");
    tree.setBeanIdField("id");
    tree.setBeanLabelField("groupName");
    uiBreadcumbs.setBreadcumbsStyle("UIExplorerHistoryPath") ;
  }
  
  public void processDecode(WebuiRequestContext context) throws Exception {   
    super.processDecode(context);
    System.out.println("ProcessDecode");
    UIForm uiForm  = getAncestorOfType(UIForm.class);
    String action =  null;
    if(uiForm != null){
      action =  uiForm.getSubmitAction(); 
    }else {
      action = context.getRequestParameter(UIForm.ACTION);
    }    
    if(action == null)  return;    
    Event<UIComponent> event = createEvent(action, Event.Phase.DECODE, context) ;   
    if(event != null) event.broadcast()  ;   
    if(!UIContainer.class.isInstance(this)) return;    
    
    UIContainer uicontainer = UIContainer.class.cast(this);
    List<UIComponent>  children =  uicontainer.getChildren() ;
    for(UIComponent uiChild : children) {
      uiChild.processDecode(context);      
    }
  }

  static  public class ChangeNodeActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      System.out.println("\n\n\n\n\n ChangeNodeActionListener In UIGroupSelector \n\n\n\n\n");
    }
  }
}
