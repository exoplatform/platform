/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.page;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.UIWelcomeComponent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIExoStart;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.core.UIDescription;
import org.exoplatform.webui.core.UIPopupWindow;
import org.exoplatform.webui.core.UIWizard;
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
  private int numberStep_ ; 
  private boolean showWelcome = true;
  
  public UIPageWizard() throws Exception {
    uiHelpWindow = createUIComponent(UIPopupWindow.class, null, null);      
    uiHelpWindow.setWindowSize(300, 200);  
    uiHelpWindow.setShow(false);
    uiHelpWindow.setId("UIPageWizardHelp") ;
  }
  
  public void setNumberSteps(int s) { numberStep_ = s; }
  public int getNumberSteps() {return numberStep_; }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    uiHelpWindow.processRender(context);
  }
  
  public boolean isShowWelcomeComponent(){ return showWelcome; }
  public void setShowWelcomeComponent(boolean value) { showWelcome = value; }
  
  public UIPopupWindow getHelpWindow() { return uiHelpWindow; }
  
  void updateUIPortal(UIPortalApplication uiPortalApp, Event<? extends UIPageWizard> event) throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext)event.getRequestContext();

    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID);
    UIComponentDecorator uiWorkingArea = uiControl.getChildById(UIControlWorkspace.WORKING_AREA_ID);
    uiWorkingArea.setUIComponent(uiWorkingArea.createUIComponent(UIWelcomeComponent.class, null, null)) ;
//    pcontext.addUIComponentToUpdateByAjax(uiControl);  

    UIPortal uiPortal = Util.getUIPortal();
    uiPortal.setMode(UIPortal.COMPONENT_VIEW_MODE);
    uiPortal.setRenderSibbling(UIPortal.class) ;    
    pcontext.setFullRender(true);
    
//    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
//    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);      
  }
  
  void updateWizardComponent(){
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class) ;
    PortalRequestContext pcontext = Util.getPortalRequestContext();

    UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);
    pcontext.addUIComponentToUpdateByAjax(uiWorkingWS);    

    UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID) ;
    pcontext.addUIComponentToUpdateByAjax(uiControl) ;
    
    pcontext.setFullRender(true) ;
  }
  
  public void setDescriptionWizard() throws Exception {
    UIPortalApplication uiPortalApp = getAncestorOfType(UIPortalApplication.class);
    UIExoStart uiExoStart = uiPortalApp.findFirstComponentOfType(UIExoStart.class);
    uiExoStart.setUIControlWSWorkingComponent(UIPageCreateDescription.class);
    UIPageCreateDescription uiPageDescription = uiExoStart.getUIControlWSWorkingComponent();
    if(this.getClass() == UIPageEditWizard.class){
      uiPageDescription.setTitle("Page Edit Wizard");
      uiPageDescription.addChild(UIDescription.class, null, "pageEditWizard");
      return;
    }
    uiPageDescription.setTitle("Page Creation Wizard");
    uiPageDescription.addChild(UIDescription.class, null, "pageWizard");
  }
  
  static public class AbortActionListener extends EventListener<UIPageWizard> {
    public void execute(Event<UIPageWizard> event) throws Exception {
      UIPageWizard uiWizard = event.getSource();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      uiWizard.updateUIPortal(uiPortalApp, event);
    }
  }
  
  
}
