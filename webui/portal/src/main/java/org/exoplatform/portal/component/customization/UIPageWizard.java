/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIControlWorkspace;
import org.exoplatform.portal.component.control.UIExoStart;
import org.exoplatform.portal.component.view.UIPortal;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.component.widget.UIWelcomeComponent;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIContainer;
import org.exoplatform.webui.component.UIDescription;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.UIWizard;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 21, 2007  
 */
public abstract class UIPageWizard extends UIWizard {
  
  protected UIPopupWindow uiHelpWindow;
  
  public UIPageWizard() throws Exception {
    uiHelpWindow = createUIComponent(UIPopupWindow.class, null, null);      
    uiHelpWindow.setWindowSize(300, 200);  
    uiHelpWindow.setShow(false);
    uiHelpWindow.setId("help") ;
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    uiHelpWindow.processRender(context);
  }
  
  public UIPopupWindow getHelpWindow() { return uiHelpWindow; }
  
  void updateUIPortal(UIPortalApplication uiPortalApp, Event<? extends UIPageWizard> event) throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();

    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIComponentDecorator uiWorkingArea = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    uiWorkingArea.setUIComponent(uiWorkingArea.createUIComponent(UIWelcomeComponent.class, null, null)) ;
    pcontext.addUIComponentToUpdateByAjax(uiControl);  

    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    uiPortal.setRenderSibbling(UIPortal.class) ;    
    pcontext.setFullRender(true);
    
    updateAjax();    
  }
  
  void updateAjax(){
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class) ;
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class) ;
    PortalRequestContext pcontext = Util.getPortalRequestContext();
    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    UIComponentDecorator uiWorkingArea = uiExoStart.<UIContainer>getParent().findComponentById(UIControlWorkspace.WORKING_AREA_ID);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingArea);      
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);    
  }
  
  public void setDescriptionWizard() throws Exception {
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);
    uiExoStart.setUIControlWSWorkingComponent(UIPageCreateDescription.class);
    UIPageCreateDescription uiPageDescription = uiExoStart.getUIControlWSWorkingComponent();
    uiPageDescription.setTitle("Page Creation Wizard");
    uiPageDescription.addChild(UIDescription.class, null, "pageWizard");
  }
  
  static  public class ViewStep2ActionListener extends EventListener<UIPageWizard> {
    public void execute(Event<UIPageWizard> event) throws Exception {
      UIPageWizard uiWizard = event.getSource();
      uiWizard.setDescriptionWizard();
      
      uiWizard.updateAjax();
      uiWizard.viewStep(2);
    }
  }
  
  static public class AbortActionListener extends EventListener<UIPageWizard> {
    public void execute(Event<UIPageWizard> event) throws Exception {
      UIPageWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      uiWizard.updateUIPortal(uiPortalApp, event);
    }
  }
  
  
}
