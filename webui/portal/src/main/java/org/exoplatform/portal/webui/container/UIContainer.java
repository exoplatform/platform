/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.container;

import org.exoplatform.portal.webui.container.UIContainerActionListener.AddApplicationActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.AddWidgetContainerActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.DeleteWidgetActionListener;
import org.exoplatform.portal.webui.container.UIContainerActionListener.EditContainerActionListener;
import org.exoplatform.portal.webui.portal.UIPortalComponent;
import org.exoplatform.portal.webui.portal.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIContainerLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
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
      events = @EventConfig(listeners = UIContainer.SelectTabActionListener.class)
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
      System.out.println("\n\n ==  > select tab ne \n\n");
    }
  }
  
}
