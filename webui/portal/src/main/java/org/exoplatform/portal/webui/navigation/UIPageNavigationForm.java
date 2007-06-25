/***************************************************************************

 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.model.PageNavigation;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.web.application.RequestContext;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIComponentDecorator;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormPopupWindow;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.webui.organization.UIGroupSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIPermissionSelector;

/**
 * Author : Nguyen Thi Hoa, Pham Dung Ha
 *          hoa.nguyen@exoplatform.com
 * Jun 20, 2006
 */
@ComponentConfigs({
  @ComponentConfig(
      lifecycle = UIFormLifecycle.class,
      template = "system:/groovy/webui/form/UIFormTabPane.gtmpl",
      events = {
        @EventConfig(listeners = UIPageNavigationForm.SaveActionListener.class),
        @EventConfig(listeners = UIPageNavigationForm.ChangeOwnerTypeActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIPageNavigationForm.SelectGroupActionListener.class, phase = Phase.DECODE),
        @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
      }
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PermissionSetting",
      template = "system:/groovy/webui/core/UITabSelector.gtmpl"
  )
})
public class UIPageNavigationForm extends UIFormTabPane {

  public PageNavigation pageNav_;

  public UIPageNavigationForm() throws Exception {
    super("UIPageNavigationForm") ;
    
    List<SelectItemOption<String>> ownerTypes = new ArrayList<SelectItemOption<String>>() ;
    ownerTypes.add(new SelectItemOption<String>("User", PortalConfig.USER_TYPE)) ;
    ownerTypes.add(new SelectItemOption<String>("Portal", PortalConfig.PORTAL_TYPE)) ;
    ownerTypes.add(new SelectItemOption<String>("Group", PortalConfig.GROUP_TYPE)) ;
    UIFormSelectBox uiSelectBoxOwnerType = new UIFormSelectBox("ownerType","ownerType" , ownerTypes) ;
    uiSelectBoxOwnerType.setOnChange("ChangeOwnerType");
    
    UIFormStringInput uiOwnerId = new UIFormStringInput("ownerId", "ownerId", null);
    uiOwnerId.setEditable(false);
    
    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for(int i = 0; i < 10; i++){
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }

    UIFormInputSet uiSettingSet = new UIFormInputSet("PageNavigationSetting") ;    
    uiSettingSet.addUIFormInput(uiSelectBoxOwnerType).
                 addUIFormInput(uiOwnerId).
                 addUIFormInput(new UIFormStringInput("creator", "creator",null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("modifier", "modifier",null).setEditable(false)).
                 addUIFormInput(new UIFormTextAreaInput("description","description", null)).
                 addUIFormInput(new UIFormSelectBox("priority", null, priorties));
    addUIFormInput(uiSettingSet) ;
    
    UIFormInputSet uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    uiPermissionSetting.setRendered(false);
    addUIComponentInput(uiPermissionSetting);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiPermissionSetting.addChild(uiListPermissionSelector);
    
    UIPermissionSelector uiEditPermission = createUIComponent(UIPermissionSelector.class, null, null);
    uiEditPermission.setRendered(false) ;
    uiEditPermission.configure("UIPermissionSelector", "editPermission");
    uiPermissionSetting.addChild(uiEditPermission);
    
    UIFormPopupWindow uiPopupGroupSelector = addChild(UIFormPopupWindow.class, null, "UIPopupGroupSelector");
    uiPopupGroupSelector.setShowCloseButton(false);
    uiPopupGroupSelector.setWindowSize(540, 0);
    UIGroupSelector uiGroupSelector = createUIComponent(UIGroupSelector.class, null, null) ;
    uiPopupGroupSelector.setUIComponent(uiGroupSelector);
    
    WebuiRequestContext rContext =  (WebuiRequestContext)RequestContext.getCurrentInstance();
    uiOwnerId.setValue(rContext.getRemoteUser());
    
    setActions(new String[]{"Save", "Close" });
  }
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIFormPopupWindow uiPopupGroupSelector = getChildById("UIPopupGroupSelector");
    if(uiPopupGroupSelector != null) uiPopupGroupSelector.processRender(context);
  }
  
  public List<UIComponent> getChildren() {
    List<UIComponent> list = new ArrayList<UIComponent>();
    List<UIComponent> children = super.getChildren();
    for(UIComponent uiComp : children) {
      if(uiComp.getId().equals("UIPopupGroupSelector")) continue;
      list.add(uiComp);
    }
    return list;
  }

  public PageNavigation getPageNavigation(){ return pageNav_; }
  
  public void setValues(PageNavigation pageNavigation) throws Exception {
    pageNav_ = pageNavigation;
    invokeGetBindingBean(pageNavigation) ;
    
    getUIFormSelectBox("ownerType").setEnable(false);
    
    UIFormSelectBox uiSelectBox = findComponentById("priority");
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }

  //TODO: Tung.Pham modified
  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {   
      UIPageNavigationForm uiForm = event.getSource();
      boolean isNewNavigation = false;
      PageNavigation pageNav = uiForm.getPageNavigation();
      PortalRequestContext pcontext = (PortalRequestContext) event.getRequestContext();
      
      if(pageNav == null) {
        isNewNavigation = true;
        pageNav = new PageNavigation();
      }
      
      uiForm.invokeSetBindingBean(pageNav) ;
      UIFormSelectBox uiSelectBox = uiForm.findComponentById("priority");
      int priority = Integer.parseInt(uiSelectBox.getValue());
      pageNav.setPriority(priority);
      if(isNewNavigation) {
        pageNav.setModifiable(true);
        pageNav.setCreator(pcontext.getRemoteUser());
      } else {
        pageNav.setModifier(pcontext.getRemoteUser());        
      }
      
      if(isNewNavigation) {
        UIPortalApplication uiPortalApp = uiForm.getAncestorOfType(UIPortalApplication.class);
        UIPageNodeSelector uiPageNodeSelector = uiPortalApp.findFirstComponentOfType(UIPageNodeSelector.class);
        boolean hasUIPageNodeSelector = (uiPageNodeSelector != null) ;
        if((hasUIPageNodeSelector && isExist(uiPageNodeSelector.getNavigations(), pageNav))) {
          uiPortalApp.addMessage(new ApplicationMessage("UIPageNavigationForm.msg.existPageNavigation", new String[]{pageNav.getOwnerId()})) ;;
          pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
          return ;
        }

        if(hasUIPageNodeSelector) {
          //Util.getUIPortal().getNavigations().add(pageNav);
          //uiPageNodeSelector.loadNavigations();
          uiPageNodeSelector.addNavigation(pageNav) ;  
          uiPageNodeSelector.selectNavigation(pageNav.getId()) ;
          pcontext.addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
        }
      } 
      
      UIComponentDecorator uiFormParent = uiForm.getParent(); 
      uiFormParent.setUIComponent(null);
      pcontext.addUIComponentToUpdateByAjax(uiFormParent); 
    }
    
    //TODO: Tung.Pham added
    private boolean isExist(List<PageNavigation> navis, PageNavigation navi) {
      for(PageNavigation ele : navis) {
        if(ele.getId().equals(navi.getId())) return true ;
      }
      
      return false ;
    }

  }
  
  static public class ChangeOwnerTypeActionListener  extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {
      UIPageNavigationForm uiForm = event.getSource();
      UIFormSelectBox uiSelectBox = uiForm.getUIFormSelectBox("ownerType");
      String ownerType = uiSelectBox.getValue();
      PortalRequestContext prContext = Util.getPortalRequestContext();
      UIFormStringInput uiOwnerId = uiForm.getUIStringInput("ownerId");
      
      if(PortalConfig.USER_TYPE.equals(ownerType)){
        uiOwnerId.setValue(prContext.getRemoteUser());
      } else if(PortalConfig.PORTAL_TYPE.equals(ownerType)){
        uiOwnerId.setValue(Util.getUIPortal().getName());
      } else {
        String script = "eXo.webui.UIPopupWindow.show('UIPopupGroupSelector');";
        prContext.getJavascriptManager().addCustomizedOnLoadScript(script);
      }
      prContext.addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }
  
  static public class SelectGroupActionListener  extends EventListener<UIGroupSelector> {
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiGroupSelector = event.getSource();
      UIPageNavigationForm uiPageNavigationForm = uiGroupSelector.getAncestorOfType(UIPageNavigationForm.class);
      UIFormStringInput ownerIdStringInput = uiPageNavigationForm.getUIStringInput("ownerId");
      if(uiGroupSelector.getSelectedGroup() == null) {
        UIFormSelectBox uiSelectBox = uiPageNavigationForm.getUIFormSelectBox("ownerType");
        uiSelectBox.setValue(PortalConfig.USER_TYPE);
        PortalRequestContext prContext = Util.getPortalRequestContext();
        ownerIdStringInput.setValue(prContext.getRemoteUser());
        return;
      }
      ownerIdStringInput.setValue(uiGroupSelector.getSelectedGroup().getId());
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNavigationForm.getParent());
    }
  }

}
