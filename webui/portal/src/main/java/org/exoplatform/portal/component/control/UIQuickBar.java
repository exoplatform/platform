/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.control;

import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.customization.UIPortalToolPanel;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIQuickHelp;
import org.exoplatform.webui.component.debug.UIApplicationTree;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 15, 2006
 */

@ComponentConfig(
  template = "system:/groovy/portal/webui/component/control/UIQuickBar.gtmpl",
  events = {
    @EventConfig(listeners = UIQuickBar.QuickTourActionListener.class),
    @EventConfig(listeners = UIQuickBar.BasicCustomizationActionListener.class),
    @EventConfig(listeners = UIQuickBar.AdvanceCustomizationActionListener.class),
    @EventConfig(listeners = UIQuickBar.ToggleAjaxActionListener.class)
  }
)
@SuppressWarnings("unused")
public class UIQuickBar extends UIComponent {
  
  public List getEvents() { return getComponentConfig().getEvents() ; } 
    
  static  public class QuickTourActionListener extends EventListener<UIQuickBar> {
    public void execute(Event<UIQuickBar> event) throws Exception {
      UIQuickBar uiQuickBar = event.getSource() ; 
      UIPortalControlPanel uiControlPanel = uiQuickBar.getAncestorOfType(UIPortalControlPanel.class);
      String helpUri = uiControlPanel.getQuickHelpUri() ;
      UIComponent uiCurrent = uiControlPanel.getChild(1) ;
      UIQuickHelp uiQuickHelp = (UIQuickHelp)uiControlPanel.getHelpComponent() ; 
      uiQuickHelp.setHelpUri(helpUri) ;
      uiControlPanel.replaceChild(uiCurrent.getId(), uiQuickHelp) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiControlPanel) ;
    }
  }  
  
  static  public class BasicCustomizationActionListener extends EventListener<UIQuickBar> {
    public void execute(Event<UIQuickBar> event) throws Exception {
//      UIQuickBar uiQuickBar = event.getSource() ;
//      UIPortalControlPanel uiControlPanel = uiQuickBar.getAncestorOfType(UIPortalControlPanel.class);
//      uiControlPanel.replaceWorkingUIComponent(UIBasicCustomization.class, null, null);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiControlPanel) ;
//      
//      UIPortalApplication uiPortalApp = uiQuickBar.getAncestorOfType(UIPortalApplication.class);
//      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
    }
  }
  
  static  public class AdvanceCustomizationActionListener extends EventListener<UIQuickBar> {
    public void execute(Event<UIQuickBar> event) throws Exception {
//      UIQuickBar uiQuickBar = event.getSource() ;
//      UIPortalControlPanel uiControlPanel = uiQuickBar.getAncestorOfType(UIPortalControlPanel.class);
//      uiControlPanel.replaceWorkingUIComponent(UIAdvancedCustomization.class, null, null);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiControlPanel) ;
//      
//      UIPortalApplication uiPortalApp = uiQuickBar.getAncestorOfType(UIPortalApplication.class);
//      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//      uiWorkingWS.setRenderedChild(UIPortal.class) ;  
    }
  }
  
  static  public class ToggleAjaxActionListener extends EventListener<UIQuickBar> {
    public void execute(Event<UIQuickBar> event) throws Exception {
      UIQuickBar uiQuickBar = event.getSource() ;
      UIPortalApplication uiApp = uiQuickBar.getAncestorOfType(UIPortalApplication.class);
      uiApp.setUseAjax(!uiApp.useAjax()) ;
    }
  }
  
  static  public class ShowAppTreeActionListener extends EventListener<UIQuickBar> {
    public void execute(Event<UIQuickBar> event) throws Exception {
      UIQuickBar uiQuickBar = event.getSource() ;
      UIPortalApplication uiApp = uiQuickBar.getAncestorOfType(UIPortalApplication.class);
      UIWorkspace uiWorkingWS = uiApp.getChildById(UIPortalApplication.UI_WORKING_WS_ID);
      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortalToolPanel uiToolPanel = uiWorkingWS.getChild(UIPortalToolPanel.class) ;
      uiToolPanel.setWorkingComponent(UIApplicationTree.class, null) ;
    }
  }

}