/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.portal;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.application.UIPortletOptions;
import org.exoplatform.portal.webui.container.UIContainerConfigOptions;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIToolbar;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
/**
 * Created by The eXo Platform SARL
 * Author : chungnv
 *          nguyenchung136@yahoo.com
 * Jun 23, 2006
 * 10:07:15 AM
 */
@ComponentConfig(    
  template = "system:/groovy/webui/core/UIToolbar.gtmpl",
  events = {   
    @EventConfig(listeners = UIPortalManagementEditBar.PreviewActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditPortalActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditContainerActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditPortletActionListener.class)
  }
)
public class UIPortalManagementEditBar extends UIToolbar { 
  
  public UIPortalManagementEditBar() throws Exception {
    setToolbarStyle("EditToolbar") ;
    setJavascript("Preview","onClick='eXo.portal.UIPortal.switchMode(this);'") ;
  }
  
  public <T extends UIComponent> T setRendered(boolean b) { 
    List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
    Util.getUIPortal().findComponentOfType(uiPortlets, UIPortlet.class);
    for (UIPortlet uiPortlet : uiPortlets) {
      uiPortlet.setShowEditControl(b);
    }
    return super.<T>setRendered(b) ;    
  } 
  
  @SuppressWarnings("unused")
  static public class PreviewActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {
    }
  }
  
  static public class EditPortalActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);
      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.createUIComponent(UIPortalForm.class, null, "UIPortalForm");
      uiMaskWS.setWindowSize(700, -1);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
    }
  }
  
  static public class EditContainerActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {     
      UIPortalManagementEditBar uiEditBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);   
      
      UIPortalManagement uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {UIPortalManagementEditBar.class,
                                      UIContainerConfigOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
    }
  }
  
  static public class EditPortletActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {
      UIPortalManagementEditBar uiEditBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);
      
      UIPortalManagement uiPManagement = uiEditBar.getParent();
      Class<?> [] childrenToRender = {UIPortalManagementEditBar.class, 
                                   UIPortletOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext() ;
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      
      UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
      pcontext.addUIComponentToUpdateByAjax(uiControl);
      
      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;    
      pcontext.setFullRender(true);
    }
  }
   
 
}