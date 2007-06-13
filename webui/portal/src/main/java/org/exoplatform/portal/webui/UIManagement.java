/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.event.Event;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Dec 1, 2006  
 */
abstract public class UIManagement extends UIContainer {
  
  protected ManagementMode mode_ = ManagementMode.EDIT;
  
  public static enum ManagementMode { EDIT, BROWSE }
  
  public ManagementMode getMode(){ return mode_; }
  
  abstract public void setMode(ManagementMode mode, Event<? extends UIComponent> event) throws Exception ;
  
}
