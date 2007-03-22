/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIRightClickPopupMenu;
import org.exoplatform.webui.component.UIToolbar;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
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
  template = "system:/groovy/webui/component/UIToolbar.gtmpl",
  events = {   
    @EventConfig(listeners = UIPortalManagementEditBar.PreviewActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditPortalActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditContainerActionListener.class),
    @EventConfig(listeners = UIPortalManagementEditBar.EditPortletActionListener.class)
  }
)
public class UIPortalManagementEditBar extends UIToolbar { 
  
  public UIPortalManagementEditBar() throws Exception {
    setToolbarStyle("PolyToolbar") ;
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
      UIPortalForm uiForm = uiMaskWS.createUIComponent(UIPortalForm.class, null, null);
      uiMaskWS.setUIComponent(uiForm) ;
      uiForm.setValues(uiPortal.getUserPortalConfig().getPortalConfig());
     
      uiMaskWS.setShow(true) ;
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      
//      UIPortalForm uiPortalForm = uiApp.setUIControlWSPopupComponent(UIPortalForm.class);
//      event.getRequestContext().addUIComponentToUpdateByAjax(uiPortalForm.getParent());
//      UIPortalForm uiPortalForm = Util.showComponentOnWorking(uiEditBar, UIPortalForm.class);
//      UIPortalManagement uiPManagement = uiEditBar.getParent();
//      Class [] childrenToRender = { UIPortalManagementEditBar.class  }; 
//      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
//      uiPortalForm.setValues(uiPortal.getUserPortalConfig().getPortalConfig());
//      Util.updateUIApplication(event);
    }
  }
  
  static public class EditContainerActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {     
      UIPortalManagementEditBar uiEditBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);   
      
      UIPortalManagement uiPManagement = uiEditBar.getParent();
      Class [] childrenToRender = {UIPortalManagementEditBar.class,
                                   UIContainerConfigOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      Util.updateUIApplication(event);
    }
  }
  
  static public class EditPortletActionListener  extends EventListener<UIPortalManagementEditBar> {
    public void execute(Event<UIPortalManagementEditBar> event) throws Exception {
      UIPortalManagementEditBar uiEditBar = event.getSource();
      UIPortal uiPortal = Util.getUIPortal();
      uiPortal.setRenderSibbling(UIPortal.class);
      
      UIPortalManagement uiPManagement = uiEditBar.getParent();
      Class [] childrenToRender = {UIPortalManagementEditBar.class, 
                                   UIPortletOptions.class, UIPortalManagementControlBar.class};
      uiPManagement.setRenderedChildrenOfTypes(childrenToRender);
      
      Util.updateUIApplication(event);
    }
  }
   
 
}