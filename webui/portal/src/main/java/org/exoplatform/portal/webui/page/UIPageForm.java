package org.exoplatform.portal.webui.page;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.portal.webui.application.UIPortlet;
import org.exoplatform.portal.webui.container.UIContainer;
import org.exoplatform.portal.webui.navigation.UIPageManagement;
import org.exoplatform.portal.webui.navigation.UIPageNavigationControlBar;
import org.exoplatform.portal.webui.navigation.UIPageNodeSelector;
import org.exoplatform.portal.webui.page.UIPageBrowseControlBar;
import org.exoplatform.portal.webui.util.PortalDataMapper;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIControlWorkspace;
import org.exoplatform.portal.webui.workspace.UIMaskWorkspace;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.portal.webui.workspace.UIPortalToolPanel;
import org.exoplatform.portal.webui.workspace.UIWorkspace;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemCategory;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputItemSelector;
import org.exoplatform.webui.form.UIFormInputSet;
import org.exoplatform.webui.form.UIFormPopupWindow;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTabPane;
import org.exoplatform.webui.form.validator.EmptyFieldValidator;
import org.exoplatform.webui.form.validator.IdentifierValidator;
import org.exoplatform.webui.organization.UIGroupSelector;
import org.exoplatform.webui.organization.UIListPermissionSelector;
import org.exoplatform.webui.organization.UIPermissionSelector;

@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/form/UIFormTabPane.gtmpl",    
    events = {
      @EventConfig(listeners = UIPageForm.SaveActionListener.class),
      @EventConfig(listeners = UIPageForm.ChangeOwnerTypeActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIPageForm.SelectGroupActionListener.class, phase = Phase.DECODE),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    },
    initParams = @ParamConfig(
      name = "PageTemplate",
      value = "system:/WEB-INF/conf/uiconf/portal/webui/page/PageTemplate.groovy"
    )
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PermissionSetting",
      template = "system:/groovy/webui/core/UITabSelector.gtmpl"
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PageSetting",
      template = "system:/groovy/portal/webui/navigation/UIPageNavigationSetting.gtmpl"
  )
})
public class UIPageForm extends UIFormTabPane {
  
  private UIPage uiPage_ ;
  private UIFormInputSet uiPermissionSetting;
  @SuppressWarnings("unchecked")
  public UIPageForm(InitParams initParams) throws Exception  {
    super("UIPageForm");
    
    List<SelectItemOption<String>> ownerTypes = new ArrayList<SelectItemOption<String>>() ;
    ownerTypes.add(new SelectItemOption<String>("User", PortalConfig.USER_TYPE)) ;
    if(Util.getUIPortal().isModifiable()) {
      ownerTypes.add(new SelectItemOption<String>("Portal", PortalConfig.PORTAL_TYPE)) ;
    }
    PortalRequestContext pcontext = Util.getPortalRequestContext();
    if(pcontext.isUserInRole("admin")) {
      ownerTypes.add(new SelectItemOption<String>("Group", PortalConfig.GROUP_TYPE)) ;
    }
    UIFormSelectBox uiSelectBoxOwnerType = new UIFormSelectBox("ownerType", "ownerType" , ownerTypes) ;
    uiSelectBoxOwnerType.setOnChange("ChangeOwnerType");
    
    UIFormInputSet uiSettingSet = createUIComponent(UIFormInputSet.class, "PageSetting", "PageSetting");
    uiSettingSet.addUIFormInput(new UIFormStringInput("pageId", null, null).setEditable(false)).
                 addUIFormInput(uiSelectBoxOwnerType).
                 addUIFormInput(new UIFormStringInput("ownerId", "ownerId", null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("name", "name", null).
                                addValidator(EmptyFieldValidator.class).addValidator(IdentifierValidator.class)).
                 addUIFormInput(new UIFormStringInput("title", "title", null)).
                 addUIFormInput(new UIFormCheckBoxInput("showMaxWindow", "showMaxWindow", false));
    addUIFormInput(uiSettingSet) ;
    
    uiPermissionSetting = createUIComponent(UIFormInputSet.class, "PermissionSetting", null);
    uiPermissionSetting.setRendered(false);
    addUIComponentInput(uiPermissionSetting);
    
    UIListPermissionSelector uiListPermissionSelector = createUIComponent(UIListPermissionSelector.class, null, null);
    uiListPermissionSelector.configure("UIListPermissionSelector", "accessPermissions");
    uiPermissionSetting.addChild(uiListPermissionSelector);
    
    UIPermissionSelector uiEditPermission = createUIComponent(UIPermissionSelector.class, null, null);
    uiEditPermission.setRendered(false) ;
    uiEditPermission.configure("UIPermissionSelector", "editPermission");
    uiPermissionSetting.addChild(uiEditPermission);

    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("PageTemplate");
    List<SelectItemCategory>  itemCategories =  param.getMapGroovyObject(context) ;
    
    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Template", "template");
    uiTemplate.setItemCategories(itemCategories);
    uiTemplate.setRendered(false);
    addUIFormInput(uiTemplate);
    
    UIFormPopupWindow uiPopupGroupSelector = addChild(UIFormPopupWindow.class, null, "UIPopupGroupSelector");
    uiPopupGroupSelector.setShowCloseButton(false);
    uiPopupGroupSelector.setWindowSize(540, 0);
    UIGroupSelector uiGroupSelector = createUIComponent(UIGroupSelector.class, null, null) ;
    uiPopupGroupSelector.setUIComponent(uiGroupSelector);
    
    setActions(new String[]{"Save", "Close" });
  }
  
  public UIPage getUIPage() { return uiPage_ ; }
  
  public List<UIComponent> getChildren() {
    List<UIComponent> list = new ArrayList<UIComponent>();
    List<UIComponent> children = super.getChildren();
    for(UIComponent uiComp : children) {
      if(uiComp.getId().equals("UIPopupGroupSelector")) continue;
      list.add(uiComp);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public void setValues(UIPage uiPage) throws Exception {
    uiPage_ = uiPage;
    Page page = PortalDataMapper.toPageModel(uiPage) ;
    invokeGetBindingBean(page) ;
    if(uiPage.getOwnerType().equals(PortalConfig.USER_TYPE)) {
      removeChildById("PermissionSetting") ;  
    } else if(getChildById("PermissionSetting") == null) {
      addUIComponentInput(uiPermissionSetting);
    }
    getUIStringInput("name").setEditable(false) ;
    getUIStringInput("pageId").setValue(uiPage.getPageId());
    getUIFormCheckBoxInput("showMaxWindow").setValue(uiPage.isShowMaxWindow());
    getUIFormSelectBox("ownerType").setEnable(false);
    removeChild(UIPageTemplateOptions.class);
    removeChildById("UIPopupGroupSelector");
    
    UIFormInputItemSelector uiTemplate = getChild(UIFormInputItemSelector.class);
    if(uiTemplate == null)  return;
    if(page.getFactoryId() == null || page.getFactoryId().trim().length() < 1) {
      uiTemplate.setValue("Default");
      return;
    }
    uiTemplate.setValue(uiPage.getFactoryId());
  }
  
  public  void invokeSetBindingBean(Object bean) throws Exception {
    super.invokeSetBindingBean(bean);
    Page page = (Page)bean;    
       
    UIFormInputItemSelector uiTemplate = getChildById("Template");
    if(uiTemplate != null) {
      SelectItemOption<?> itemOption = uiTemplate.getSelectedItemOption();
      if(itemOption != null){
        page.setFactoryId(itemOption.getIcon());
        page.setTemplate((String)itemOption.getValue());
        page.setShowMaxWindow(page.getFactoryId().equals(Page.DESKTOP_PAGE));
      } 
    } 
    
    if(!page.isShowMaxWindow()) {
      page.setShowMaxWindow((Boolean) getUIFormCheckBoxInput("showMaxWindow").getValue());      
    }
    
    UIPageTemplateOptions uiConfigOptions = getChild(UIPageTemplateOptions.class);
    if(uiConfigOptions == null) return;
    Page selectedPage = uiConfigOptions.getSelectedOption();
    if(selectedPage == null) return ;
    page.setChildren(selectedPage.getChildren());
    page.setFactoryId(selectedPage.getFactoryId());
    page.setShowMaxWindow(Page.DESKTOP_PAGE.equals(page.getFactoryId()));
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIFormPopupWindow uiPopupGroupSelector = getChildById("UIPopupGroupSelector");
    if(uiPopupGroupSelector != null) uiPopupGroupSelector.processRender(context);
  }

  @SuppressWarnings("unchecked")
  static public class SaveActionListener  extends EventListener<UIPageForm> {
    public void execute(Event<UIPageForm> event) throws Exception {
      
      UIPageForm uiPageForm = event.getSource();   
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPage uiPage = uiPageForm.getUIPage();
      Page page = new Page() ;
      uiPageForm.invokeSetBindingBean(page);
      UserPortalConfigService configService = uiPageForm.getApplicationComponent(UserPortalConfigService.class);

      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      uiMaskWS.setShow(false);
      pcontext.addUIComponentToUpdateByAjax(uiMaskWS) ;
      
      if(uiPage == null)  return;
      
      page.setOwnerType(uiPage.getOwnerType());
      List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
      findAllPortlet(uiPortlets, uiPage);
      ArrayList<Object> applications = new ArrayList<Object>();
      for(UIPortlet uiPortlet : uiPortlets) {
        applications.add(PortalDataMapper.toPortletModel(uiPortlet));
      }

      if(Page.DESKTOP_PAGE.equals(uiPage.getFactoryId()) && !Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        page.setShowMaxWindow(false);
        uiPage.getChildren().clear();
        page.setChildren(applications);

        page.setModifier(pcontext.getRemoteUser());
        PortalDataMapper.toUIPage(uiPage, page);  
        if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>()); 

        UIPageManagement uiManagement = uiPortalApp.findFirstComponentOfType(UIPageManagement.class);
        UIPageEditBar uiEditBar = uiManagement.getChild(UIPageEditBar.class); 
        uiEditBar.setRendered(true);
        uiEditBar.setUIPage(uiPage);

        pcontext.setFullRender(true);
        UIControlWorkspace uiControl = uiPortalApp.findComponentById(UIPortalApplication.UI_CONTROL_WS_ID) ;
        pcontext.addUIComponentToUpdateByAjax(uiControl) ;
        UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
        pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;

        return;
      }

      if(Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
        uiPage.getChildren().clear();         
        page.setChildren(applications);         

        page.setModifier(pcontext.getRemoteUser());
        PortalDataMapper.toUIPage(uiPage, page);  
        if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>()); 

        UIPageManagement uiManagement = uiPortalApp.findFirstComponentOfType(UIPageManagement.class);
        UIPageEditBar uiEditBar = uiManagement.getChild(UIPageEditBar.class); 
        uiEditBar.setUIPage(uiPage);
        Class<?> [] childrenToRender = null;
        if(uiManagement.getChild(UIPageBrowseControlBar.class).isRendered()) {
          childrenToRender = new Class<?>[]{UIPageBrowseControlBar.class};
        } else {
          configService.update(page);
          childrenToRender = new Class<?>[]{UIPageNodeSelector.class, UIPageNavigationControlBar.class};
        }
        uiManagement.setRenderedChildrenOfTypes(childrenToRender);
        pcontext.addUIComponentToUpdateByAjax(uiManagement);
        UIPortalToolPanel toolPanel = Util.getUIPortalToolPanel();
        toolPanel.setShowMaskLayer(true);
        pcontext.setFullRender(true);
        UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
        pcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
        UserPortalConfigService service = uiEditBar.getApplicationComponent(UserPortalConfigService.class);
        service.update(page);
        return;
      } 
      
      List<UIComponent> uiChildren = uiPage.getChildren();
      if(uiChildren == null)  {
        PortalDataMapper.toUIPage(uiPage, page);
        return ;
      }
      ArrayList<Object>  children = new ArrayList<Object>();
      for(UIComponent child : uiChildren){ 
        Object component = PortalDataMapper.buildChild(child);
        if(component != null) children.add(component);
      }
      page.setChildren(children);
      uiPage.getChildren().clear(); 

      page.setModifier(pcontext.getRemoteUser());
      PortalDataMapper.toUIPage(uiPage, page);  
      if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
      if(page.getChildren() == null) page.setChildren(new ArrayList<Object>()); 
    }
    
    protected void findAllPortlet(List<UIPortlet> list, UIContainer uiContainer) {
      List<UIComponent> children = uiContainer.getChildren();
      for(UIComponent ele : children) {
        if(ele instanceof UIPortlet) list.add((UIPortlet)ele);
        else if(ele instanceof UIContainer) findAllPortlet(list, (UIContainer) ele); 
      }
    }
  }
 
  static public class ChangeOwnerTypeActionListener  extends EventListener<UIPageForm> {
    public void execute(Event<UIPageForm> event) throws Exception {
      UIPageForm uiForm = event.getSource();
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
      UIPageForm uiPageForm = uiGroupSelector.getAncestorOfType(UIPageForm.class);
      UIFormStringInput uiOwnerId = uiPageForm.getUIStringInput("ownerId");
      if(uiGroupSelector.getSelectedGroup() == null) {
        UIFormSelectBox uiSelectBox = uiPageForm.getUIFormSelectBox("ownerType");
        uiSelectBox.setValue(PortalConfig.USER_TYPE);
        if(uiPageForm.getChildById("PermissionSetting") != null) uiPageForm.removeChildById("PermissionSetting") ;
      } else {
        uiOwnerId.setValue(uiGroupSelector.getSelectedGroup().getId());
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPageForm.getParent());
    }
  }

}
