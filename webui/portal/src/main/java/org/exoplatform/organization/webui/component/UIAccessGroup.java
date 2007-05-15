/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.organization.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.component.UIFormInputContainer;
import org.exoplatform.webui.component.UIFormPopupWindow;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.component.UIPageIterator;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

/**
 * Created by The eXo Platform SARL
 * Author : Pham Dung Ha
 *          ha.pham@exoplatform.com
 * May 7, 2007
 */

@ComponentConfig(
  template = "system:/groovy/organization/webui/component/UIAccessGroup.gtmpl",
  events = {
      @EventConfig(phase = Phase.DECODE, listeners = UIAccessGroup.DeleteActionListener.class, confirm = "UIAccessGroup.deleteAccessGroup"),
      @EventConfig(listeners = UIAccessGroup.SelectGroupActionListener.class)
  }
)
public class UIAccessGroup extends UIFormInputContainer<String> { 

  public UIAccessGroup() throws Exception {
    super(null, null);
    UIGrid uiGrid = addChild(UIGrid.class, null, "TableGroup") ;
    uiGrid.configure("id", new String[]{"id", "label", "description"}, new String[]{"Delete"});
    
    uiGrid.getUIPageIterator().setPageList(new ObjectPageList(new ArrayList<Group>(), 10));
    
    UIFormPopupWindow uiPopup = addChild(UIFormPopupWindow.class, null, "UIGroupSelector");
    uiPopup.setWindowSize(540, 0);
    UIGroupSelector uiGroupSelector = createUIComponent(UIGroupSelector.class, null, null) ;
    uiPopup.setUIComponent(uiGroupSelector);
  }
  
  public void configure(String iname, String bfield) {
    setName(iname) ;
    setBindingField(bfield) ; 
  }
  
  @SuppressWarnings("unchecked")
  public void addGroup(Group...groups) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    list.addAll(uiIterator.getPageList().getAll());
    for(Group group : groups) {
      if(checkAvailable(group)) list.add(group);
    }
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  @SuppressWarnings("unchecked")
  private boolean checkAvailable(Group group) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    list .addAll(uiIterator.getPageList().getAll());
    for(Object ele: list) {
      if(((Group)ele).getId().equals(group.getId())) return false;
    }
    return true;
  }

  public void clearGroups() throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  @SuppressWarnings("unchecked")
  public String [] getAccessGroup() throws Exception {
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    List<Object> values = uiIterator.getPageList().getAll();
    String [] groups = new String[values.size()];
    for(int i = 0; i < values.size(); i++) {
      Group group = (Group) values.get(i);
      groups[i] = group.getId(); 
    }
    return groups;
  }
  
  @SuppressWarnings("unchecked")
  public void setGroups(String [] groups) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    for(String id : groups) {
      Group group = service.getGroupHandler().findGroupById(id);
      list.add(group);
    }
    uiIterator.setPageList(new ObjectPageList(list, 10));
  }
  
  static  public class SelectGroupActionListener extends EventListener<UIGroupSelector> {   
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiGroupSelector = event.getSource();
      if(uiGroupSelector.getSelectedGroup() == null) return;
      UIAccessGroup uiAccessGroup = uiGroupSelector.getAncestorOfType(UIAccessGroup.class);
      Group group = uiGroupSelector.getSelectedGroup();
      if(group.getLabel() == null) group.setLabel("");
      if(group.getDescription() == null) group.setDescription("");
      uiAccessGroup.addGroup(group);
    }
  }
    
  static  public class DeleteActionListener extends EventListener<UIAccessGroup> {   
    public void execute(Event<UIAccessGroup> event) throws Exception {
      String groupId = event.getRequestContext().getRequestParameter(OBJECTID) ;
      UIAccessGroup uiAccessGroup = event.getSource() ;
      
      
      System.out.println("\n\n\n\n\n\n\n  REMOVE OBJECT: "+uiAccessGroup+"  \n\n\n\n\n\n\n");
    }
  }

}