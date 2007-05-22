package org.exoplatform.portal.component.customization;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.organization.webui.component.UIListPermissionSelector;
import org.exoplatform.organization.webui.component.UIPermissionSelector;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.component.UIPortalApplication;
import org.exoplatform.portal.component.UIWorkspace;
import org.exoplatform.portal.component.control.UIMaskWorkspace;
import org.exoplatform.portal.component.view.PortalDataModelUtil;
import org.exoplatform.portal.component.view.UIContainer;
import org.exoplatform.portal.component.view.UIPage;
import org.exoplatform.portal.component.view.UIPortlet;
import org.exoplatform.portal.component.view.Util;
import org.exoplatform.portal.config.UserPortalConfigService;
import org.exoplatform.portal.config.model.Page;
import org.exoplatform.portal.config.model.PortalConfig;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.component.UIComponent;
import org.exoplatform.webui.component.UIFormCheckBoxInput;
import org.exoplatform.webui.component.UIFormInputItemSelector;
import org.exoplatform.webui.component.UIFormInputSet;
import org.exoplatform.webui.component.UIFormSelectBox;
import org.exoplatform.webui.component.UIFormStringInput;
import org.exoplatform.webui.component.UIFormTabPane;
import org.exoplatform.webui.component.UIGrid;
import org.exoplatform.webui.component.UIPageIterator;
import org.exoplatform.webui.component.UIPopupWindow;
import org.exoplatform.webui.component.UITree;
import org.exoplatform.webui.component.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.component.model.SelectItemCategory;
import org.exoplatform.webui.component.model.SelectItemOption;
import org.exoplatform.webui.component.validator.EmptyFieldValidator;
import org.exoplatform.webui.component.validator.IdentifierValidator;
import org.exoplatform.webui.config.InitParams;
import org.exoplatform.webui.config.Param;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.ComponentConfigs;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.config.annotation.ParamConfig;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;

@ComponentConfigs({
  @ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template =  "system:/groovy/webui/component/UIFormTabPane.gtmpl",    
    events = {
      @EventConfig(listeners = UIPageForm.SaveActionListener.class),
      @EventConfig(listeners = UIMaskWorkspace.CloseActionListener.class, phase = Phase.DECODE)
    },
    initParams = @ParamConfig(
      name = "PageTemplate",
      value = "system:/WEB-INF/conf/uiconf/portal/webui/component/customization/PageTemplate.groovy"
    )
  ),
  @ComponentConfig(
      type = UIFormInputSet.class,
      id = "PermissionSetting",
      template = "system:/groovy/webui/component/UITabSelector.gtmpl"
  )
})
public class UIPageForm extends UIFormTabPane {
  
  private UIPage uiPage_ ;
  private UIComponent returnComponent_ ;
  
  @SuppressWarnings("unchecked")
  public UIPageForm(InitParams initParams) throws Exception  {
    super("UIPageForm");
    
    List<SelectItemOption<String>> ls = new ArrayList<SelectItemOption<String>>() ;
    ls.add(new SelectItemOption<String>("User", "User")) ;
    ls.add(new SelectItemOption<String>("Portal", "Portal")) ;
    ls.add(new SelectItemOption<String>("Group", "Group")) ;
    UIFormSelectBox uiSelectBox = new UIFormSelectBox("ownerType","ownerType" , ls) ;
    uiSelectBox.setOnChange("ChangeOwnerType");
//    uiSelectBox.setEditable(false);
    
    UIFormInputSet uiSettingSet = new UIFormInputSet("PageSetting") ;
    uiSettingSet.addUIFormInput(new UIFormStringInput("pageId", null, null).setEditable(false)).
                 addUIFormInput(uiSelectBox).
                 addUIFormInput(new UIFormStringInput("ownerId", "ownerId", null).setEditable(false)).
                 addUIFormInput(new UIFormStringInput("name", "name", null).
                                addValidator(EmptyFieldValidator.class).addValidator(IdentifierValidator.class)).
                 addUIFormInput(new UIFormStringInput("title", "title", null)).
                 addUIFormInput(new UIFormCheckBoxInput("showMaxWindow", "showMaxWindow", false));
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

    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    Param param = initParams.getParam("PageTemplate");
    List<SelectItemCategory>  itemCategories =  param.getMapGroovyObject(context) ;
    
    UIFormInputItemSelector uiTemplate = new UIFormInputItemSelector("Template", "template");
    uiTemplate.setItemCategories(itemCategories);
    uiTemplate.setRendered(false);
    addUIFormInput(uiTemplate);
  }
  
  public UIPage getUIPage() { return uiPage_ ; }
  
  @SuppressWarnings("unchecked")
  public void setValues(UIPage uiPage) throws Exception {
    uiPage_ = uiPage;
    Page page = PortalDataModelUtil.toPageModel(uiPage) ;
    getUIStringInput("name").setEditable(false) ;
    getUIStringInput("pageId").setValue(uiPage.getPageId());
    invokeGetBindingBean(page) ;
    getUIFormCheckBoxInput("showMaxWindow").setValue(uiPage.isShowMaxWindow());
    
    removeChild(UIPageTemplateOptions.class);
    
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
      SelectItemOption itemOption = uiTemplate.getSelectedItemOption();
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

  public UIComponent getBackUIComponent() { return returnComponent_ ; }
  public void setBackUIComponent(UIComponent uiComp) throws Exception {
    returnComponent_ = uiComp ;
  }   
  
  public void processRender(WebuiRequestContext context) throws Exception {
    super.processRender(context);
    
    UIPermissionSelector uiPermissionSelector = getChild(UIPermissionSelector.class);
    if(uiPermissionSelector == null) return;
    UIPopupWindow uiPopupWindow = uiPermissionSelector.getChild(UIPopupWindow.class);
    if(uiPopupWindow == null) return;
    uiPopupWindow.processRender(context);
  }

  @SuppressWarnings("unchecked")
  static public class SaveActionListener  extends EventListener<UIPageForm> {
    public void execute(Event<UIPageForm> event) throws Exception {
      UIPageForm uiPageForm = event.getSource();   
      PortalRequestContext pcontext = Util.getPortalRequestContext();
      UIPage uiPage = uiPageForm.getUIPage();
      Page page = new Page() ;
      uiPageForm.invokeSetBindingBean(page);
      UserPortalConfigService configService = uiPageForm.getApplicationComponent(UserPortalConfigService.class);
      
      if(uiPage != null) {
        page.setOwnerType(uiPage.getOwnerType());
        List<UIPortlet> uiPortlets = new ArrayList<UIPortlet>();
        findAllPortlet(uiPortlets, uiPage);
        ArrayList<Object> applications = new ArrayList<Object>();
        for(UIPortlet uiPortlet : uiPortlets) {
          applications.add(PortalDataModelUtil.toPortletModel(uiPortlet));
        }
        
        if(Page.DESKTOP_PAGE.equals(uiPage.getFactoryId()) && !Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
          page.setShowMaxWindow(false);
          uiPage.getChildren().clear();
          page.setChildren(applications);
        } else if(!Page.DESKTOP_PAGE.equals(uiPage.getFactoryId()) && Page.DESKTOP_PAGE.equals(page.getFactoryId())) {
          uiPage.getChildren().clear();         
          page.setChildren(applications);   
        } else {
          List<UIComponent> uiChildren = uiPage.getChildren();
          if(uiChildren == null)  return ;
          ArrayList<Object>  children = new ArrayList<Object>();
          for(UIComponent child : uiChildren){ 
            Object component = PortalDataModelUtil.buildChild(child);
            if(component != null) children.add(component);
          }
          page.setChildren(children);
          uiPage.getChildren().clear(); 
        }
        page.setModifier(pcontext.getRemoteUser());
        PortalDataModelUtil.toUIPage(uiPage, page);  
        if(page.getTemplate() == null) page.setTemplate(uiPage.getTemplate()) ;
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>()); 
        configService.update(page);
      } else {
        page.setCreator(pcontext.getRemoteUser());
        page.setModifiable(true);
        page.setOwnerType(PortalConfig.USER_TYPE);
        page.setOwnerId(pcontext.getRemoteUser());
        if(page.getChildren() == null) page.setChildren(new ArrayList<Object>());
        configService.create(page);
      }
      
      WebuiRequestContext rcontext = event.getRequestContext();
      UIPortalApplication uiPortalApp = event.getSource().getAncestorOfType(UIPortalApplication.class);
      UIMaskWorkspace uiMaskWS = uiPortalApp.getChildById(UIPortalApplication.UI_MASK_WS_ID) ;
      uiMaskWS.setUIComponent(null);
      uiMaskWS.setShow(false);
      rcontext.addUIComponentToUpdateByAjax(uiMaskWS) ; 
      
      UIPageManagement uiManagement = uiPortalApp.findFirstComponentOfType(UIPageManagement.class);
      
      UIPageBrowser uiBrowser = uiPortalApp.findFirstComponentOfType(UIPageBrowser.class);
      if(uiBrowser != null) {
        UIPageIterator  iterator = uiBrowser.getChild(UIGrid.class).getUIPageIterator();
        int currentPage = iterator.getCurrentPage();
        uiBrowser.defaultValue(uiBrowser.getLastQuery());
        if(currentPage > iterator.getAvailablePage()) currentPage = iterator.getAvailablePage();
        iterator.setCurrentPage(currentPage);
        UIWorkspace uiWorkingWS = uiPortalApp.findComponentById(UIPortalApplication.UI_WORKING_WS_ID);    
        rcontext.addUIComponentToUpdateByAjax(uiWorkingWS) ;
        return;
      } 
      
      if(uiManagement == null)  return;
      UIPageNodeSelector uiNodeSelector = uiManagement.getChild(UIPageNodeSelector.class);
      UITree uiTree = uiNodeSelector.getChild(UITree.class);        
      uiTree.createEvent("ChangeNode", event.getExecutionPhase(), rcontext).broadcast();
    }
    
    private void findAllPortlet(List<UIPortlet> list, UIContainer uiContainer) {
      List<UIComponent> children = uiContainer.getChildren();
      for(UIComponent ele : children) {
        if(ele instanceof UIPortlet) list.add((UIPortlet)ele);
        else if(ele instanceof UIContainer) findAllPortlet(list, (UIContainer) ele); 
      }
    }
  }
 
  static public class ChangeOwnerTypeActionListener  extends EventListener<UIPageForm> {
    public void execute(Event<UIPageForm> event) throws Exception {
      System.out.println("\n\n\n\n----------------------------->>>>>>>>>>> ChangeOwnerType");
      
    }
  }
  
}
