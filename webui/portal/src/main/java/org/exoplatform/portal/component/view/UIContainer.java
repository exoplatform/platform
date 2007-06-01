/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.portal.component.view.listener.UIContainerActionListener.AddApplicationActionListener;
import org.exoplatform.portal.component.view.listener.UIContainerActionListener.DeleteWidgetActionListener;
import org.exoplatform.portal.component.view.listener.UIContainerActionListener.EditContainerActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;

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
      template = "system:/groovy/portal/webui/component/view/UIWidgetContainer.gtmpl",
      events = {
          @EventConfig(listeners = DeleteWidgetActionListener.class),
          @EventConfig(listeners = AddApplicationActionListener.class)
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
  
}
