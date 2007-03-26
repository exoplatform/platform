/***************************************************************************

 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.config.UserACL.Permission;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponentDecorator;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIFormTextAreaInput;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

/**
 * Author : Nguyen Thi Hoa  
 *          hoa.nguyen@exoplatform.com
 * Jun 20, 2006
 */
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/component/UIFormTabPane.gtmpl",   
    events = {
      @EventConfig(listeners = UIPageNavigationForm.SaveActionListener.class)
//      @EventConfig(phase = Phase.DECODE, listeners = UIPageNavigationForm.BackActionListener.class)
    }
)
public class UIPageNavigationForm extends UIFormTabPane {

  public PageNavigation pageNav_;
//  private UIComponent backComponent_ ;
  private String helpUri_ ;

  public UIPageNavigationForm() throws Exception {
    
    super("UIPageNavigationForm") ;    
    
    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for(int i = 0; i < 10; i++){
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }    

    UIFormInputSet uiSettingSet = new UIFormInputSet("PageNavigationSetting") ;    
    uiSettingSet.addUIFormInput(new UIFormStringInput("owner","owner",null).setEditable(false)).
                 addUIFormInput(new UIFormTextAreaInput("description","description", null)).
                 addUIFormInput(new UIFormSelectBox("priority", null, priorties));
    addUIFormInput(uiSettingSet) ;
    
    UIPermissionSelector uiPermissionSelector = createUIComponent(UIPermissionSelector.class, null, null);
    uiPermissionSelector.configure("Permission", null, null) ;
    uiPermissionSelector.setRendered(false);
    addUIFormInput(uiPermissionSelector) ;    
  }

  public PageNavigation getPageNavigation(){ return pageNav_; }
  
//  public UIComponent getBackComponent() { return backComponent_ ; }
//  public void setBackComponent(UIComponent uiComp) throws Exception {
//    backComponent_ = uiComp ;
//  }
  
  public void setUriForQuickHelp(String helpUri) { helpUri_ = helpUri ; }
  public String getUriForQuickHelp() { return helpUri_ ; }

  public void setValues(PageNavigation pageNavigation) throws Exception {
    pageNav_ = pageNavigation;
    invokeGetBindingBean(pageNavigation) ;    
    UIPermissionSelector uiPermissionSelector = getChild(UIPermissionSelector.class);
    uiPermissionSelector.createPermission("AccessPermission", pageNav_.getAccessPermission());
    uiPermissionSelector.createPermission("EditPermission", pageNav_.getEditPermission());
    
    UIFormSelectBox uiSelectBox = findFirstComponentOfType(UIFormSelectBox.class);
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);   
    
    UIPermissionSelector uiPermissionSelector = getChild(UIPermissionSelector.class);
    if(uiPermissionSelector == null) return;
    UIPopupWindow uiPopupPermission = uiPermissionSelector.getChild(UIPopupWindow.class);
    uiPopupPermission.processRender(context);
  }

  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {   
     
      UIPageNavigationForm uiForm = event.getSource();
      PageNavigation pageNav = uiForm.getPageNavigation();
      uiForm.invokeSetBindingBean(pageNav) ;
      UIFormSelectBox uiSelectBox = uiForm.findFirstComponentOfType(UIFormSelectBox.class);
      int priority = Integer.parseInt(uiSelectBox.getValue());
      pageNav.setPriority(priority);
      
      UIPermissionSelector uiPermissionSelector = uiForm.getChild(UIPermissionSelector.class);
      if(uiPermissionSelector == null)  return;
      Permission  permission = uiPermissionSelector.getPermission("AccessPermission");
      if(permission != null) pageNav.setAccessPermission(permission.getValue());

      permission = uiPermissionSelector.getPermission("EditPermission");
      if(permission != null) pageNav.setEditPermission(permission.getValue());
      
      UIComponentDecorator uiFormParent = uiForm.getParent(); 
      uiFormParent.setUIComponent(null);

      event.getRequestContext().addUIComponentToUpdateByAjax(uiFormParent);    
    }
  }
  
 /* static public class BackActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {
      UIPageNavigationForm uiForm = event.getSource();
//      System.out.println("Back roi neeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
      UIPortalToolPanel uiToolPanel = Util.getUIPortalToolPanel();
      UIComponent uiComp = uiForm.getBackComponent() ;
      uiToolPanel.setRenderSibbling(UIPortalToolPanel.class) ;
      if(uiComp == null)  return;
      if(uiComp instanceof UIQuickHelp) {
        String helpUri = uiForm.getUriForQuickHelp() ;
        UIQuickHelp uiQuickHelp = (UIQuickHelp) uiComp ;
        uiQuickHelp.setHelpUri(helpUri) ;
        uiToolPanel.setUIComponent(uiQuickHelp) ;
      } else uiToolPanel.setUIComponent(uiComp) ;
      
      
//      UIPageNavigationForm uiComponent = event.getSource();
//      UIPortalApplication uiPortalApp = uiComponent.getAncestorOfType(UIPortalApplication.class);      
//      Util.showPortalComponentLayoutMode(uiPortalApp);    
//      Util.updateUIApplication(event);
    }
  }*/

}
