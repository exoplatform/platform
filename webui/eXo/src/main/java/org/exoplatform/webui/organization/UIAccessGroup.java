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
package org.exoplatform.webui.organization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.LazyPageList;
import org.exoplatform.commons.utils.ObjectPageList;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIGrid;
import org.exoplatform.webui.core.UIPageIterator;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputContainer;
import org.exoplatform.webui.form.UIFormPopupWindow;

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
    
    uiGrid.getUIPageIterator().setPageList(new LazyPageList(new AccessGroupListAccess(null), 10));
    
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
    uiIterator.setPageList(new LazyPageList(new AccessGroupListAccess(list), 10));
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
    uiIterator.setPageList(new LazyPageList(new AccessGroupListAccess(list), 10));
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
  
  public void setGroups(String [] groups) throws Exception {
    List<Object> list = new ArrayList<Object>();
    UIPageIterator uiIterator = getChild(UIGrid.class).getUIPageIterator();
    OrganizationService service = getApplicationComponent(OrganizationService.class) ;
    for(String id : groups) {
      Group group = service.getGroupHandler().findGroupById(id);
      list.add(group);
    }
    uiIterator.setPageList(new LazyPageList(new AccessGroupListAccess(list), 10));
  }
  
  public Class<String> getTypeValue() { return String.class; }
  
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
    }
  }

}