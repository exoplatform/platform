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
package org.exoplatform.portal.webui.container;

import java.util.List;

import org.exoplatform.portal.webui.container.UIContainerActionListener.DeleteGadgetActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.EditContainerActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.ShowAddNewApplicationActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * May 19, 2006
 */
@ComponentConfigs({
  @ComponentConfig(   
      events = {
          @EventConfig(listeners = EditContainerActionListener.class),
          @EventConfig(listeners = DeleteComponentActionListener.class, confirm = "UIContainer.deleteContainer")
      }
  ),/*
  @ComponentConfig( 
      id = "WidgetContainer",
      template = "system:/groovy/portal/webui/container/UIWidgetContainer.gtmpl",
      events = {
          @EventConfig(listeners = ShowAddNewApplicationActionListener.class)
      }
  ),*/
  @ComponentConfig( 
      id = "GadgetContainer",
      template = "system:/groovy/portal/webui/container/UIGadgetContainer.gtmpl",
      events = {
          @EventConfig(listeners = DeleteGadgetActionListener.class),
          @EventConfig(listeners = ShowAddNewApplicationActionListener.class)
      }
  ),
  @ComponentConfig(
      id = "TabContainer", 
      template = "system:/groovy/portal/webui/container/UITabContainer.gtmpl",
      events = {
          @EventConfig(listeners = EditContainerActionListener.class),
          @EventConfig(listeners = DeleteComponentActionListener.class, confirm = "UIContainer.deleteContainer"),
          @EventConfig(listeners = UIContainer.SelectTabActionListener.class)
      }
  )
})
public class UIContainer extends  UIPortalComponent {
  
  protected String icon;
  protected String description;
  protected String    creator ;
  protected String    modifier ;
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }
  
  public String getDescription() { return description; }
  public void setDescription(String desc) {this.description = desc; }
  
  static  public class SelectTabActionListener extends EventListener<UIContainer> {    
    public void execute(Event<UIContainer> event) throws Exception {
      String objectId = event.getRequestContext().getRequestParameter(OBJECTID);
      UIContainer container = event.getSource();
      UIComponent goal = container.findComponentById(objectId);
      if(goal == null) { return; }
      UIContainer parent = goal.getParent();
      List<UIComponent> children = parent.getChildren();
      for(UIComponent child: children){
        if(child.getId().equals(objectId)){
          child.setRendered(true); 
          continue;
        }
        child.setRendered(false);
      }
    }
  }
}
