/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.container;

import java.util.List;

import org.exoplatform.portal.webui.container.UIContainerActionListener.AddApplicationActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.AddWidgetContainerActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.DeleteWidgetActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.EditContainerActionListener;
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
  ),
  @ComponentConfig( 
      id = "WidgetContainer",
      template = "system:/groovy/portal/webui/container/UIWidgetContainer.gtmpl",
      events = {
          @EventConfig(listeners = DeleteWidgetActionListener.class),
          @EventConfig(listeners = AddApplicationActionListener.class),
          @EventConfig(listeners = AddWidgetContainerActionListener.class)
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
  
  protected String    creator ;
  protected String    modifier ;
  
  public String getCreator()  {  return creator ; }
  public void   setCreator(String s) { creator = s ; }
  
  public String getModifier() { return modifier ; }
  public void   setModifier(String s) { modifier = s ; }
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }
  
  static  public class SelectTabActionListener extends EventListener<UIContainer> {    
    public void execute(Event<UIContainer> event) throws Exception {
      String objectId = event.getRequestContext().getRequestParameter(OBJECTID);
      UIContainer container = event.getSource();
      UIComponent goal = container.findComponentById(objectId);
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
