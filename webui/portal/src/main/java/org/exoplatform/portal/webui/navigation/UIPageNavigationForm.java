/***************************************************************************

 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.DataStorage;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Author : Le Bien Thuy  
 *          lebienthuy@gmail.com
 * Aug 30, 2007
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIFormWithTitle.gtmpl",
    events = {
      @EventConfig(listeners = UIPageNavigationForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    }
)
public class UIPageNavigationForm extends UIForm {

  protected PageNavigation pageNav_;
  
  public UIPageNavigationForm() throws Exception {
    PortalRequestContext pContext = PortalRequestContext.getCurrentInstance();
    UserPortalConfigService dataService = getApplicationComponent(UserPortalConfigService.class);
    List<String> list = dataService.getMakableNavigations(pContext.getRemoteUser());
    List<SelectItemOption<String>> makableGroups = new ArrayList<SelectItemOption<String>>() ;
    for(String ele: list){   makableGroups.add(new SelectItemOption<String>(ele)) ; }
    UIFormSelectBox uiSelectBoxOwnerId = new UIFormSelectBox("ownerId","ownerId" , makableGroups) ;
    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for(int i = 1; i < 11; i++){
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }
    addUIFormInput(new UIFormStringInput("ownerType", "ownerType", "group").setEditable(false)).
    addUIFormInput(uiSelectBoxOwnerId).
    addUIFormInput(new UIFormStringInput("creator", "creator", pContext.getRemoteUser()).setEditable(false)).
    addUIFormInput(new UIFormStringInput("modifier", "modifier",null).setEditable(false)).
    addUIFormInput(new UIFormTextAreaInput("description","description", null)).
    addUIFormInput(new UIFormSelectBox("priority", null, priorties));
  }
  
  public void setValues(PageNavigation pageNavigation) throws Exception {
    pageNav_ = pageNavigation;
    invokeGetBindingBean(pageNavigation) ;
    removeChildById("ownerId");
    getUIStringInput("creator").setValue(pageNavigation.getCreator());
    UIFormStringInput ownerId = new UIFormStringInput("ownerId", "ownerId", pageNavigation.getOwnerId());
    ownerId.setEditable(false);
    ownerId.setParent(this);
    getChildren().add(1, ownerId);
    UIFormSelectBox uiSelectBox = findComponentById("priority");
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }

  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {   
      UIPageNavigationForm uiForm = event.getSource();
      PageNavigation pageNav = uiForm.pageNav_;
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();

      if(pageNav != null) {
        uiForm.invokeSetBindingBean(pageNav) ;
        UIFormSelectBox uiSelectBox = uiForm.findComponentById("priority");
        int priority = Integer.parseInt(uiSelectBox.getValue());
        pageNav.setPriority(priority); 
        pageNav.setModifier(pcontext.getRemoteUser());     
        UIComponentDecorator uiFormParent = uiForm.getParent(); 
        uiFormParent.setUIComponent(null);
        pcontext.addUIComponentToUpdateByAjax(uiFormParent); 
        return;
      }

      pageNav = new PageNavigation();
      uiForm.invokeSetBindingBean(pageNav) ;
      UIFormSelectBox uiSelectBox = uiForm.findComponentById("priority");
      int priority = Integer.parseInt(uiSelectBox.getValue());
      pageNav.setPriority(priority);
      pageNav.setModifiable(true);
      pageNav.setCreator(pcontext.getRemoteUser());
      UIPortalApplication uiPortalApp = uiForm.getAncestorOfType(UIPortalApplication.class);
      UIPageNodeSelector uiPageNodeSelector = uiPortalApp.findFirstComponentOfType(UIPageNodeSelector.class);
      
      
      PageNavigation existingNavi = uiPageNodeSelector.getPageNavigation(pageNav.getId()) ; 
      if( existingNavi != null || checkExiting(pageNav.getId())) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNavigationForm.msg.existPageNavigation", new String[]{pageNav.getOwnerId()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return ;        
      }
      
      uiPageNodeSelector.addPageNavigation(pageNav) ;  
      uiPageNodeSelector.selectNavigation(pageNav.getId()) ;
      pcontext.addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      uiMaskWS.setShow(false);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
    }

    private boolean checkExiting(String navId) throws Exception {
       UIPortal portal = Util.getUIPortal();
       DataStorage service = portal.getApplicationComponent(DataStorage.class);
       List<PageNavigation> list = portal.getNavigations();
       if(service.getPageNavigation(navId) == null) return false;
       for(PageNavigation nav: list){
         if(nav.getId().equals(navId) ) return true;
       }
       return false;
    }
  }
}