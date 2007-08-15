/***************************************************************************

 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.webui.navigation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserACL;
import org.exoplatform.portal.config.UserPortalConfigService;
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
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PageNavigationSetting",
      template = "system:/groovy/portal/webui/navigation/UIPageNavigationSetting.gtmpl"
  )
  
})
public class UIPageNavigationForm extends UIFormTabPane {

  public PageNavigation pageNav_;
  
  private UIFormInputSet uiPermissionSetting;

  public UIPageNavigationForm() throws Exception {
    super("UIPageNavigationForm") ;
    
    List<SelectItemOption<String>> ownerTypes = new ArrayList<SelectItemOption<String>>() ;
    ownerTypes.add(new SelectItemOption<String>(PortalConfig.USER_TYPE)) ;
    
    UserPortalConfigService dataService = getApplicationComponent(UserPortalConfigService.class);
    PortalRequestContext pContext = PortalRequestContext.getCurrentInstance();
    String remoteUser  = pContext.getRemoteUser();
    UserACL userService = getApplicationComponent(UserACL.class);
    String portalName = Util.getUIPortal().getName();
    PageNavigation portalNavigation = dataService.getPageNavigation("portal::" + portalName, remoteUser);
//    System.out.println("\n\n\n-------------------? name? =" + portalNavigation);
    if(portalNavigation != null && userService.hasEditPermission(portalNavigation.getOwnerId(), remoteUser, portalNavigation.getEditPermission()) ) {
      ownerTypes.add(new SelectItemOption<String>(PortalConfig.PORTAL_TYPE)) ;
    }
    PortalRequestContext pcontext = Util.getPortalRequestContext();
    if(pcontext.isUserInRole("admin")) {
      ownerTypes.add(new SelectItemOption<String>(PortalConfig.GROUP_TYPE)) ;
    }
    UIFormSelectBox uiSelectBoxOwnerType = new UIFormSelectBox("ownerType","ownerType" , ownerTypes) ;
    uiSelectBoxOwnerType.setOnChange("ChangeOwnerType");
    
    UIFormStringInput uiOwnerId = new UIFormStringInput("ownerId", "ownerId", null);
    uiOwnerId.setEditable(false);
    
    List<SelectItemOption<String>> priorties = new ArrayList<SelectItemOption<String>>();
    for(int i = 1; i < 11; i++){
      priorties.add(new SelectItemOption<String>(String.valueOf(i), String.valueOf(i)));
    }

    UIFormInputSet uiSettingSet = createUIComponent(UIFormInputSet.class, "PageNavigationSetting", "PageNavigationSetting") ;    
    uiSettingSet.addUIFormInput(uiSelectBoxOwnerType).
                 addUIFormInput(uiOwnerId).
                 addUIFormInput(new UIFormStringInput("creator", "creator",null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("modifier", "modifier",null).setEditable(false)).
                 addUIFormInput(new UIFormTextAreaInput("description","description", null)).
                 addUIFormInput(new UIFormSelectBox("priority", null, priorties));
    addUIFormInput(uiSettingSet) ;

    uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    uiPermissionSetting.setRendered(false);
    
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
    if(pageNavigation.getOwnerType().equals(PortalConfig.USER_TYPE)) {
      removeChildById("PermissionSetting") ;  
    } else if(getChildById("PermissionSetting") == null) {
      addUIComponentInput(uiPermissionSetting);
    }
    invokeGetBindingBean(pageNavigation) ;
    
    getUIFormSelectBox("ownerType").setEnable(false);
    UIFormSelectBox uiSelectBox = findComponentById("priority");
    uiSelectBox.setValue(String.valueOf(pageNavigation.getPriority()));
  }

  static public class SaveActionListener extends EventListener<UIPageNavigationForm> {
    public void execute(Event<UIPageNavigationForm> event) throws Exception {   
      UIPageNavigationForm uiForm = event.getSource();
      PageNavigation pageNav = uiForm.getPageNavigation();
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
      
      //TODO: Tung.Pham modified
      //----------------------------
//    DataStorage storage = uiForm.getApplicationComponent(DataStorage.class);
//    if(storage.getPageNavigation(pageNav.getId()) != null) {
//    uiPortalApp.addMessage(new ApplicationMessage("UIPageNavigationForm.msg.existPageNavigation", new String[]{pageNav.getOwnerId()})) ;;
//    pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
//    return ;
//    }
      PageNavigation existingNavi = uiPageNodeSelector.getPageNavigation(pageNav.getId()) ; 
      if(existingNavi != null) {
        uiPortalApp.addMessage(new ApplicationMessage("UIPageNavigationForm.msg.existPageNavigation", new String[]{pageNav.getOwnerId()})) ;;
        pcontext.addUIComponentToUpdateByAjax(uiPortalApp.getUIPopupMessages());  
        return ;        
      }
      //----------------------------
      uiPageNodeSelector.addPageNavigation(pageNav) ;  
      uiPageNodeSelector.selectNavigation(pageNav.getId()) ;
      pcontext.addUIComponentToUpdateByAjax(uiPageNodeSelector.getParent());
      
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      uiMaskWS.setShow(false);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
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
        uiForm.removeChildById("PermissionSetting") ;  
      } else {
        if(uiForm.getChildById("PermissionSetting") == null) {
          uiForm.addUIComponentInput(uiForm.uiPermissionSetting);
        }
        if(PortalConfig.PORTAL_TYPE.equals(ownerType)){
          uiOwnerId.setValue(Util.getUIPortal().getName());
        } else {
          String script = "eXo.webui.UIPopupWindow.show('UIPopupGroupSelector');";
          prContext.getJavascriptManager().addCustomizedOnLoadScript(script);
        }
      }
      prContext.addUIComponentToUpdateByAjax(uiForm.getParent());
    }
  }
  
  static public class SelectGroupActionListener  extends EventListener<UIGroupSelector> {
    public void execute(Event<UIGroupSelector> event) throws Exception {
      UIGroupSelector uiGroupSelector = event.getSource();
      UIPageNavigationForm uiPageNavigationForm = uiGroupSelector.getAncestorOfType(UIPageNavigationForm.class);
      UIFormStringInput uiOwnerId = uiPageNavigationForm.getUIStringInput("ownerId");
      if(uiGroupSelector.getSelectedGroup() == null) {
        UIFormSelectBox uiSelectBox = uiPageNavigationForm.getUIFormSelectBox("ownerType");
        uiSelectBox.setValue(PortalConfig.USER_TYPE);
        uiPageNavigationForm.removeChildById("PermissionSetting") ;
        PortalRequestContext prContext = Util.getPortalRequestContext();
        uiOwnerId.setValue(prContext.getRemoteUser());
        return;
      }
      String groupId = uiGroupSelector.getSelectedGroup().getId();
      if(groupId.charAt(0) == '/') groupId = groupId.substring(1);
      uiOwnerId.setValue(groupId);
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageNavigationForm.getParent());
    }
  }

}
