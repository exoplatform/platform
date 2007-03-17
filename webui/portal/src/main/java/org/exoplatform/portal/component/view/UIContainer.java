/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view;

import org.exoplatform.portal.component.view.listener.UIContainerActionListener.EditContainerActionListener;
import org.exoplatform.portal.component.view.listener.UIPortalComponentActionListener.DeleteComponentActionListener;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 19, 2006
 */
@ComponentConfig(   
    events = {
      @EventConfig(listeners = EditContainerActionListener.class),
      @EventConfig(listeners = DeleteComponentActionListener.class)
    }
)
public class UIContainer extends  UIPortalComponent {
  
  protected String icon;
  protected String title;
  
  public String getIcon() { return icon ; }
  public void   setIcon(String s) { icon = s ; }
  
  public String getTitle(){ return title; }
  public void setTitle(String s) { title = s ;}
}
