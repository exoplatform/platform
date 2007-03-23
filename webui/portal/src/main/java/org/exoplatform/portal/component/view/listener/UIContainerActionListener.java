/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.view.listener;

import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.customization.UIContainerForm;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Dang Van Minh
 *          minhdv81@yahoo.com
 * Jun 13, 2006
 */
public class UIContainerActionListener {  
  
  static public class EditContainerActionListener  extends EventListener<UIContainer> {
    public void execute(Event<UIContainer> event) throws Exception {
      UIContainer uiContainer = event.getSource();
//      UIContainerForm uiForm = uiContainer.createUIComponent(UIContainerForm.class, null, null) ;
//      uiForm.setValues(uiContainer);      
//      UIPortalApplication uiPortalApp = uiContainer.getAncestorOfType(UIPortalApplication.class);
//      UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//      UIPortalToolPanel uiToolPanel = uiWorkingWS.findFirstComponentOfType(UIPortalToolPanel.class);
//      PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();
//      pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
//      
//      if(Util.getUIPortal().isRendered()){
//        UIPortal uiPortal = Util.getUIPortal() ;
//        uiForm.setBackComponent(uiPortal) ;        
//      } else {        
//        uiForm.setBackComponent(uiToolPanel.getUIComponent()) ;
//      }
//      uiToolPanel.setUIComponent(uiForm) ;
//      uiWorkingWS.setRenderedChild(UIPortalToolPanel.class) ;
      UIPortal uiPortal = Util.getUIPortal();
      UIPortalApplication uiApp = uiPortal.getAncestorOfType(UIPortalApplication.class);      
      UIMaskWorkspace uiMaskWS = uiApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;       
    
      
      UIContainerForm containerForm = uiMaskWS.createUIComponent(UIContainerForm.class, null, null); 
      containerForm.setValues(uiContainer);
      uiMaskWS.setUIComponent(containerForm);      
      
      uiMaskWS.setShow(true);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiMaskWS);
      Util.updateUIApplication(event);
    }
  }
  
}
